package hotreload.api;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

public final class AppConfig {

    private String startClass;
    private String appName;
    private Properties properties;

    public AppConfig(File configFile) throws Exception {
        properties = new Properties();
        FileInputStream fis = new FileInputStream(configFile);
        properties.load(fis);
        startClass = (String) properties.remove("start_class");
        appName = (String) properties.remove("app_name");
        fis.close();
    }

    public String getAppName() {
        return appName;
    }

    public String getStartClass() {
        return startClass;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj))
            return false;
        if (this == obj)
            return true;
        if (obj instanceof AppConfig) {
            AppConfig another = (AppConfig) obj;
            return startClass.equals(another.startClass) && appName.equals(appName)
                    && properties.equals(another.properties);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return startClass.hashCode() ^ appName.hashCode() ^ properties.hashCode();
    }

}
