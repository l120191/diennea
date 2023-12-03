package org.example.property;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class PropertyManager {
    private static final String PROPS_FILE = "configuration.properties";
    private static PropertyManager instance;
    private Properties prop = null;
    public static PropertyManager getInstance() throws IOException {
        if (instance == null)
            instance = new PropertyManager();
        return instance;
    }
    private PropertyManager() throws IOException {

        InputStream inputStream = null;
        try {
            prop = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(PROPS_FILE);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + PROPS_FILE + "' not found in the classpath");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    public String getPropertyByName(String propertyKey){
        return prop.getProperty(propertyKey);
    }

}
