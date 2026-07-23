package selenium.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try {
            FileInputStream fileInputStream = new FileInputStream("automation/config.properties");
            properties = new Properties();
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load automation/config.properties");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
