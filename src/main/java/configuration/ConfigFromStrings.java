package configuration;


import deserilization.Deserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ConfigFromStrings implements Config {
    private final Config baseConfig;
    private final Map<ConfigurationKey, ConfigBase.ClassAndValue<?>> configs;

    public ConfigFromStrings(final Config baseConfig,
            final Function<ConfigurationKey, Optional<String>> lookup) {
        this.baseConfig = requireNonNull(baseConfig);
        configs = init(baseConfig, requireNonNull(lookup));
    }

    private static Map<ConfigurationKey, ConfigBase.ClassAndValue<?>> init(
            final Config base,
            final Function<ConfigurationKey, Optional<String>> lookup) {
        final var configs
                = new HashMap<ConfigurationKey, ConfigBase.ClassAndValue<?>>();
        final var deserializer = new Deserializer();

        base.getKeys().forEach(key -> {
            final var value = lookup.apply(key);
            if (value.isPresent()) {
                final var clazz = base.getClazz(key);
                final var record = deserialize(
                        clazz, value.get(), deserializer);
                configs.put(key, record);
            }
        });

        return configs;
    }

    private static <T> ConfigBase.ClassAndValue<T> deserialize(
            final Class<T> clazz, final String serialized,
            final Deserializer deserializer) {
        final var deserialized = deserializer.deserialize(clazz, serialized);
        return new ConfigBase.ClassAndValue<>(clazz, deserialized);
    }

    @Override
    public final boolean containsKey(final ConfigurationKey key) {
        return configs.containsKey(key) || baseConfig.containsKey(key);
    }

    @Override
    public final <T> T getValue(ConfigurationKey key, Class<T> clazz) {
        final var record = configs.get(key);
        if (record != null) {
            if (clazz.isAssignableFrom(record.clazz())) {
                /*
                 * Safety: We know this cast is safe because of the if-condition
                 * check above.
                 */
                return (T) record.value();
            } else {
                throw new IllegalArgumentException(
                        ("Cannot assign an object of type '%s' to an object of "
                                + "type '%s'").formatted(
                                record.clazz(), clazz));
            }
        } else {
            return baseConfig.getValue(key, clazz);
        }
    }

    @Override
    public final Object getValue(ConfigurationKey key) {
        final var record = configs.get(key);
        return record != null ? record.value() : baseConfig.getValue(key);
    }

    @Override
    public final Class<?> getClazz(final ConfigurationKey key) {
        final var record = configs.get(key);
        return record != null ? record.clazz() : baseConfig.getClazz(key);
    }

    @Override
    public final Set<ConfigurationKey> getKeys() {
        return baseConfig.getKeys();
    }

    @Override
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "ConfigFromStrings{baseConfig=%s, configs=%s}".formatted(
                baseConfig, configs);
    }
}
