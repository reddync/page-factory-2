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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.datajack.providers.AbstractDataProvider;
import ru.sbtqa.tag.pagefactory.data.DataFactory;
import ru.sbtqa.tag.pagefactory.exceptions.FragmentException;
import ru.sbtqa.tag.pagefactory.properties.Configuration;
import ru.sbtqa.tag.pagefactory.reflection.DefaultReflection;

class FragmentCacheUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentCacheUtils.class);
    private static final Configuration PROPERTIES = Configuration.create();
    private static final String FRAGMENT_TAG = "@fragment";

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
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            Feature feature = gherkinDocument.getFeature();
            List<ScenarioDefinition> scenarioDefinitions = feature.getChildren();

            for (ScenarioDefinition scenario : scenarioDefinitions) {
                graph.addNode(scenario);

                List<Step> steps = scenario.getSteps();
                for (Step step : steps) {
                    String language = scenarioLanguageMap.get(scenario);

                    if (FragmentUtils.isStepFragmentRequire(step, language)) {
                        String scenarioName = FragmentUtils.getFragmentName(step, language);
                        ScenarioDefinition scenarioAsFragment = fragmentsMap.get(scenarioName);

                        if (scenarioAsFragment == null) {
                            try {
                                String data = (String) FieldUtils.readField(scenario, "description", true);
                                Pattern stepDataPattern = Pattern.compile(AbstractDataProvider.PATH_PARSE_REGEX);
                                Matcher stepDataMatcher = stepDataPattern.matcher(scenarioName);
                                StringBuilder replacedStep = new StringBuilder(scenarioName);

                                while (stepDataMatcher.find()) {
                                    String collection = stepDataMatcher.group(1);
                                    String value = stepDataMatcher.group(2);

                                    if (collection == null) {
                                        DataFactory.updateCollection(DataFactory.getDataProvider().getByPath(data));// не учтен тег фичи (если у сценария нет)
                                    }

                                    String builtPath = "$" + (collection == null ? "" : collection) + value;
                                    String parsedValue = DataFactory.getDataProvider().getByPath(builtPath).getValue();
                                    replacedStep = replacedStep.replace(stepDataMatcher.start(), stepDataMatcher.end(), parsedValue);
                                    stepDataMatcher = stepDataPattern.matcher(replacedStep);
                                }
                                scenarioAsFragment = fragmentsMap.get(parsedValue);
                            } catch (IllegalAccessException e) {
                                throw new FragmentException(String.format("There is no scenario (fragment) with name \"%s\"", scenarioName));
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
