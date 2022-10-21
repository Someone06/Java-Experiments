package configuration;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ConfigFromEnv implements Config {
    private final Config config;

    public ConfigFromEnv(final Config basedOn) {
        final var env = System.getenv();
        config = new ConfigFromStrings(basedOn, key -> lookupEnv(key, env));
    }

    public static Optional<String> lookupEnv(final ConfigurationKey key,
            final Map<String, String> env) {
        final String envVariable = key.toString()
                .substring(1)
                .replace('/', '_')
                .toUpperCase();
        return Optional.ofNullable(env.get(envVariable));
    }

    @Override
    public boolean containsKey(final ConfigurationKey key) {
        return config.containsKey(key);
    }

    @Override
    public <T> T getValue(final ConfigurationKey key, final Class<T> clazz) {
        return config.getValue(key, clazz);
    }

    @Override
    public Object getValue(final ConfigurationKey key) {
        return config.getValue(key);
    }

    @Override
    public Class<?> getClazz(final ConfigurationKey key) {
        return config.getClazz(key);
    }

    @Override
    public Set<ConfigurationKey> getKeys() {
        return config.getKeys();
    }

    @Override
    public String toString() {
        return "ConfigFromEnv{config=%s}".formatted(config);
    }
}
