package configuration;

import java.util.Set;

public interface Config {
    boolean containsKey(ConfigurationKey key);

    <T> T getValue(ConfigurationKey key, Class<T> clazz);

    Object getValue(ConfigurationKey key);

    Class<?> getClazz(ConfigurationKey key);

    Set<ConfigurationKey> getKeys();

    @Override
    String toString();
}
