package configuration;

import java.util.Map;
import java.util.Optional;

public final class ConfigFromEnv extends ConfigFromStrings {

    public ConfigFromEnv(final Config basedOn) {
        super(basedOn, key -> lookupEnv(key, System.getenv()));
    }

    private static Optional<String> lookupEnv(final ConfigurationKey key,
            final Map<String, String> env) {
        final String envVariable = key.toString()
                .substring(1)
                .replace('/', '_')
                .toUpperCase();
        return Optional.ofNullable(env.get(envVariable));
    }

    @Override
    public String toString() {
        return "ConfigFromEnv{super=%s}".formatted(super.toString());
    }
}
