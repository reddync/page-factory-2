package ru.sbtqa.tag.pagefactory.fragments;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import gherkin.ast.Feature;
import gherkin.ast.GherkinDocument;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.Step;
import gherkin.ast.Tag;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.data.DataUtils;
import ru.sbtqa.tag.pagefactory.exceptions.FragmentException;
import ru.sbtqa.tag.pagefactory.properties.Configuration;
import ru.sbtqa.tag.pagefactory.reflection.DefaultReflection;

class FragmentCacheUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentCacheUtils.class);
    private static final Configuration PROPERTIES = Configuration.create();
    private static final String FRAGMENT_TAG = "@fragment";

    private static final String PARAMETER_REGEXP = "\"([^\"]*)\"";
    private static final String ERROR_FRAGMENT_NOT_FOUND = "There is no scenario (fragment) with name \"%s\"";
    private static final String STEP_FIELD_NAME = "text";

    private FragmentCacheUtils() {
    }

    static List<CucumberFeature> cacheFragmentsToFeatures(Class clazz, List<CucumberFeature> features) {
        if (PROPERTIES.getFragmentsPath().isEmpty()) {
            return features;
        } else {
            ClassLoader classLoader = clazz.getClassLoader();
            ResourceLoader resourceLoader = new MultiLoader(classLoader);
            List<CucumberFeature> fragmentsRaw = CucumberFeature.load(resourceLoader, Collections.singletonList(PROPERTIES.getFragmentsPath()));
            return Stream.concat(features.stream(), fragmentsRaw.stream()).collect(Collectors.toList());
        }
    }

    static Map<String, ScenarioDefinition> cacheFragmentsAsMap(List<CucumberFeature> features) {
        Map<String, ScenarioDefinition> fragments = new HashMap<>();

        for (CucumberFeature cucumberFeature : features) {
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            Feature feature = gherkinDocument.getFeature();
            List<ScenarioDefinition> scenarioDefinitions = feature.getChildren();
            for (ScenarioDefinition scenario : scenarioDefinitions) {
                List<Tag> tags = new DefaultReflection().getScenarioTags(scenario);
                if (isFragmentTagContains(tags)) {
                    fragments.put(scenario.getName(), scenario);
                }
            }
        }

        return fragments;
    }

    private static boolean isFragmentTagContains(List<Tag> tags) {
        return tags.stream().anyMatch(tag -> tag.getName().equals(FRAGMENT_TAG));
    }

    static MutableGraph<Object> cacheFragmentsAsGraph(List<CucumberFeature> features,
                                                      Map<String, ScenarioDefinition> fragmentsMap,
                                                      Map<ScenarioDefinition, String> scenarioLanguageMap) throws FragmentException, DataException {
        MutableGraph<Object> graph = GraphBuilder.directed().allowsSelfLoops(false).build();

        for (CucumberFeature cucumberFeature : features) {
            String featureDataTagValue = DataUtils.formFeatureData(cucumberFeature);

            for (ScenarioDefinition scenario : DataUtils.getScenarioDefinitions(cucumberFeature)) {
                graph.addNode(scenario);
                String scenarioDataTagValue = DataUtils.formScenarioDataTag(scenario, featureDataTagValue);

                for (Step step : scenario.getSteps()) {
                    String language = scenarioLanguageMap.get(scenario);

                    if (FragmentUtils.isStepFragmentRequire(step, language)) {
                        String scenarioName = FragmentUtils.getFragmentName(step, language);
                        ScenarioDefinition scenarioAsFragment = fragmentsMap.get(scenarioName);

                        if (scenarioAsFragment == null) {
                            String scenarioNameFromData = getScenarioNameFromData(step, scenarioName, scenarioDataTagValue);
                            scenarioAsFragment = fragmentsMap.get(scenarioNameFromData);

                            if (scenarioAsFragment == null) {
                                throw new FragmentException(String.format(ERROR_FRAGMENT_NOT_FOUND, scenarioNameFromData));
                            }
                        }
                        graph.putEdge(scenario, scenarioAsFragment);
                    }
                }
            }
        }
        if (Graphs.hasCycle(graph)) {
            LOG.error("Fragments graph contains cycles");
        }
        return graph;
    }

    private static String getScenarioNameFromData(Step step, String scenarioName, String scenarioDataTagValue) throws FragmentException, DataException {
        try {
            String scenarioNameFromData = DataUtils.replaceDataPlaceholders(scenarioName, scenarioDataTagValue);

            if (scenarioNameFromData.equals(scenarioName)) {
                throw new FragmentException(String.format(ERROR_FRAGMENT_NOT_FOUND, scenarioName));
            }

            String replacedStepText = step.getText().replaceFirst(PARAMETER_REGEXP, "\"" + scenarioNameFromData + "\"");
            FieldUtils.writeField(step, STEP_FIELD_NAME, replacedStepText, true);

            return scenarioNameFromData;
        } catch (IllegalAccessException e) {
            throw new FragmentException(String.format("The field \"%s\" is missing from the class \"%s\"", STEP_FIELD_NAME, step.getClass()));
        }
    }

    static Map<ScenarioDefinition, String> cacheScenarioLanguage(List<CucumberFeature> features) {
        Map<ScenarioDefinition, String> scenarioLanguageMap = new HashMap<>();

        for (CucumberFeature cucumberFeature : features) {
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            Feature feature = gherkinDocument.getFeature();
            List<ScenarioDefinition> scenarios = feature.getChildren();
            for (ScenarioDefinition scenario : scenarios) {
                scenarioLanguageMap.put(scenario, feature.getLanguage());
            }
        }

        return scenarioLanguageMap;
    }
}
