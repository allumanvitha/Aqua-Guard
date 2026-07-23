package appium.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import selenium.utils.ConfigReader;
import java.net.URL;
import java.time.Duration;

public class DriverFactory {
    private static ThreadLocal<AndroidDriver> driver = new ThreadLocal<>();

    public static AndroidDriver getDriver() {
        if (driver.get() == null) {
            try {
                UiAutomator2Options options = new UiAutomator2Options();
                options.setPlatformName(ConfigReader.getProperty("appium.platformName"));
                options.setDeviceName(ConfigReader.getProperty("appium.deviceName"));
                options.setAutomationName(ConfigReader.getProperty("appium.automationName"));
                options.setAppPackage(ConfigReader.getProperty("appium.appPackage"));
                options.setAppActivity(ConfigReader.getProperty("appium.appActivity"));
                
                // If running in CI or if the app exists, set it
                String appPath = ConfigReader.getProperty("appium.app");
                if (appPath != null && !appPath.isEmpty()) {
                    options.setApp(appPath);
                }

                URL serverUrl = new URL(ConfigReader.getProperty("appium.serverUrl"));
                AndroidDriver androidDriver = new AndroidDriver(serverUrl, options);
                androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.set(androidDriver);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize Appium AndroidDriver: " + e.getMessage());
            }
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
