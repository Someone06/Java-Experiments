package deserilization.deserializers;

import deserilization.Deserializer;

public final class LongDeserializer
        implements Deserializer.DeserializeClass<Long> {
    @Override
    public Class<Long> getDeserializedClass() {
        return Long.class;
    }

    @Override
    public Long deserialize(final String serialized) {
        try {
            return Long.valueOf(serialized);
        } catch (final NumberFormatException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
