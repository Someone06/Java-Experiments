package configuration;

import java.util.Optional;
import java.util.Properties;

public final class ConfigFromProperties extends ConfigFromStrings {

    public ConfigFromProperties(final Config basedOn,
            final Properties properties) {
        super(basedOn, key -> lookupEnv(key, properties));
    }

    private static Optional<String> lookupEnv(final ConfigurationKey key,
            final Properties properties) {
        final var propertyKey = key.toString().substring(1).replace('/', '_');

        return Optional.ofNullable(properties.getProperty(propertyKey));
    }

    @Override
    public String toString() {
        return "ConfigFromProperties{super=%s}".formatted(super.toString());
    }
}
