package deserilization.deserializers;

import deserilization.Deserializer;

public final class ByteDeserializer
        implements Deserializer.DeserializeClass<Byte> {
    @Override
    public Class<Byte> getDeserializedClass() {
        return Byte.class;
    }

    @Override
    public Byte deserialize(final String serialized) {
        try {
            return Byte.valueOf(serialized);
        } catch (final NumberFormatException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
