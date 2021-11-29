package envLoader;

import java.util.Map;

class envLoaderImpl implements envLoader {
    private transient final Map<String, String> envVars;

    public envLoaderImpl(Map<String, String> envVars) {
        this.envVars = envVars;
    }


    @Override
    public String get(String key) {
        return this.envVars.get(key);
    }

    @Override
    public String get(String key, String defaultValue) {
        var value = this.envVars.get(key);

        return value != null ? value : defaultValue;
    }
}
