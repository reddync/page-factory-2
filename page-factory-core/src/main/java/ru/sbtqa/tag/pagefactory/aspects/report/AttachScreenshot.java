package ru.sbtqa.tag.pagefactory.aspects.report;

import cucumber.api.Result;
import io.qameta.allure.Attachment;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.environment.Environment;
import ru.sbtqa.tag.pagefactory.properties.Configuration;
import ru.sbtqa.tag.pagefactory.utils.ScreenshotUtils;

@Aspect
public class AttachScreenshot {

    private static final Configuration PROPERTIES = Configuration.create();
    private static final Logger LOG = LoggerFactory.getLogger(AttachScreenshot.class);

    @Attachment(value = "Screenshot of failed step", type = "image/png")
    public byte[] attach() {
        try {
            ScreenshotUtils screenshot = ScreenshotUtils.valueOf(PROPERTIES.getScreenshotStrategy().toUpperCase());
            return screenshot.take();
        } catch (Exception e) {
            LOG.error("Can't attach screenshot to allure reports", e);
        }
        return new byte[0];
    }

    @Around("call(* cucumber.runtime.junit.JUnitReporter.handleStepResult(..))")
    public Object errorAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object arg = (Result) joinPoint.getArgs()[1];
        if (((Result) arg).getStatus() == Result.Type.FAILED && !Environment.isDriverEmpty()) {
            this.attach();
        }
        return joinPoint.proceed();
    }
}
