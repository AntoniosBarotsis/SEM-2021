package envloader;

import java.util.Map;

class EnvLoaderImpl implements EnvLoader {
    private final transient Map<String, String> envVars;

    public EnvLoaderImpl(Map<String, String> envVars) {
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
