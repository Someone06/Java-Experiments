package configuration;

import java.util.*;

import static java.util.Objects.requireNonNull;

public final class ConfigBase implements Config {

    private final Map<ConfigurationKey, ClassAndValue<?>> configs;

    private ConfigBase(final Map<ConfigurationKey, ClassAndValue<?>> configs) {
        this.configs = requireNonNull(configs);
    }

    @Override
    public boolean containsKey(final ConfigurationKey key) {
        return configs.containsKey(requireNonNull(key));
    }

    @Override
    public <T> T getValue(final ConfigurationKey key, final Class<T> clazz) {
        requireNonNull(key);
        requireNonNull(clazz);

        final var record = configs.get(key);
        if (record == null) {
            throw new NoSuchElementException(
                    "The configuration does not contain the key '%s'.".formatted(
                            key));
        } else if (!clazz.isAssignableFrom(record.clazz())) {
            throw new IllegalArgumentException(
                    ("Cannot assign an object of type '%s' to an object of "
                            + "type '%s'").formatted(
                            record.clazz(), clazz));
        } else {
            /*
             * Safety: We know this cast is safe because of the if-condition
             * check above.
             */
            return (T) record.value();
        }
    }

    @Override
    public Object getValue(final ConfigurationKey key) {
        requireNonNull(key);

        final var record = configs.get(key);
        if (record == null) {
            throw new NoSuchElementException(
                    "The configuration does not contain the key '%s'.".formatted(
                            key));
        } else {
            return record.value();
        }
    }

    @Override
    public Class<?> getClazz(final ConfigurationKey key) {
        requireNonNull(key);

        final var record = configs.get(key);
        if (record == null) {
            throw new NoSuchElementException(
                    "The configuration does not contain the key '%s'.".formatted(
                            key));
        } else {
            return record.clazz();
        }
    }

    @Override
    public Set<ConfigurationKey> getKeys() {
        return Collections.unmodifiableSet(configs.keySet());
    }

    @Override
    public String toString() {
        return "ConfigBase{configs=%s}".formatted(configs);
    }

    record ClassAndValue<T>(Class<T> clazz, T value) {
        public ClassAndValue(final Class<T> clazz, final T value) {
            this.clazz = requireNonNull(clazz);
            this.value = requireNonNull(value);
        }
    }

    public static final class ConfigBaseBuilder {
        private Map<ConfigurationKey, ClassAndValue<?>> configs
                = new HashMap<>();
        private boolean build = false;

        public <T> ConfigBaseBuilder add(final ConfigurationKey key,
                final Class<T> clazz, final T defaultValue) {
            if (build) {
                throw new IllegalStateException(
                        "Cannot add a key after the config has been build.");
            }

            final var record = new ClassAndValue<>(clazz, defaultValue);
            final var oldValue = configs.putIfAbsent(key, record);

            if (oldValue == null) {
                return this;
            } else {
                throw new IllegalStateException(
                        "The key '%s' has already been added.".formatted(key));
            }
        }

        public ConfigBase build() {
            if (!build) {
                final var result = new ConfigBase(configs);
                build = true;
                configs = null;
                return result;
            } else {
                throw new IllegalStateException(
                        "Can only use .build() once on a ConfigBaseBuilder "
                                + "object.");
            }
        }

        @Override
        public String toString() {
            return "ConfigBaseBuilder{configs=%s, build=%s}".formatted(
                    configs, build);
        }
    }
}
