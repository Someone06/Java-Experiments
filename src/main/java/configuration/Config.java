package configuration;

public interface Config {
    boolean containsKey(ConfigurationKey key);

    <T> T getValue(ConfigurationKey key, Class<T> clazz);

    Object getValue(ConfigurationKey key);

    Class<?> getClazz(ConfigurationKey key);

    @Override
    String toString();
}
