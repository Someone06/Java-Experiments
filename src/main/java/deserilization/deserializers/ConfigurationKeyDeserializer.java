package deserilization.deserializers;

import configuration.ConfigurationKey;
import deserilization.Deserializer;

public final class ConfigurationKeyDeserializer
        implements Deserializer.DeserializeClass<ConfigurationKey> {
    @Override
    public Class<ConfigurationKey> getDeserializedClass() {
        return ConfigurationKey.class;
    }

    @Override
    public ConfigurationKey deserialize(final String serialized) {
        try {
            return new ConfigurationKey(serialized);
        } catch (final IllegalArgumentException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
