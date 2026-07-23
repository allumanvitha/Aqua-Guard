package selenium.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestListener implements ITestListener {
    private static final Logger log = LogManager.getLogger(TestListener.class);
    private long startTime;

    @Override
    public void onStart(ITestContext context) {
        log.info("Starting Test Suite execution: " + context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("Executing test case: " + result.getName());
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = System.currentTimeMillis() - startTime;
        log.info("Test passed: " + result.getName() + " (Time: " + duration + "ms)");
        
        // Add to reports
        String testId = getTestId(result);
        String module = getModule(result);
        String feature = getFeature(result);
        String priority = getPriority(result);
        
        ReportHelper.addResult("Selenium", testId, module, feature, priority, "QA-Staging", "Chrome",
                "Action should succeed", "Action succeeded successfully", "PASS", "N/A", duration, "Completed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = System.currentTimeMillis() - startTime;
        log.error("Test failed: " + result.getName() + " | Exception: " + result.getThrowable().getMessage());
        
        String testId = getTestId(result);
        String module = getModule(result);
        String feature = getFeature(result);
        String priority = getPriority(result);

        WebDriver driver = DriverFactory.getDriver();
        String screenshotPath = "N/A";
        if (driver != null) {
            try {
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String fileLoc = "automation/reports/screenshots/" + testId + "_fail.png";
                Files.createDirectories(Paths.get("automation/reports/screenshots/"));
                Files.copy(srcFile.toPath(), Paths.get(fileLoc), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                screenshotPath = "screenshots/" + testId + "_fail.png";
                log.info("Screenshot saved at: " + fileLoc);
            } catch (IOException e) {
                log.error("Failed to capture screenshot: " + e.getMessage());
            }
        }

        ReportHelper.addResult("Selenium", testId, module, feature, priority, "QA-Staging", "Chrome",
                "Action should succeed", "Failed: " + result.getThrowable().getMessage(), "FAIL", screenshotPath, duration, result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Test skipped: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Finished Test Suite execution: " + context.getName());
        ReportHelper.saveAndGenerateUnifiedReport("selenium");
    }

    private String getTestId(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(TestMetadata.class)
                ? result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestMetadata.class).testId()
                : result.getName().toUpperCase();
    }

    private String getModule(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(TestMetadata.class)
                ? result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestMetadata.class).module()
                : "General";
    }

    private String getFeature(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(TestMetadata.class)
                ? result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestMetadata.class).feature()
                : "Feature";
    }

    private String getPriority(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(TestMetadata.class)
                ? result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestMetadata.class).priority()
                : "Medium";
    }
}
