package deserilization.deserializers;

import deserilization.Deserializer;

public final class ShortDeserializer
        implements Deserializer.DeserializeClass<Short> {
    @Override
    public Class<Short> getDeserializedClass() {
        return Short.class;
    }

    @Override
    public Short deserialize(final String serialized) {
        try {
            return Short.valueOf(serialized);
        } catch (final NumberFormatException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
