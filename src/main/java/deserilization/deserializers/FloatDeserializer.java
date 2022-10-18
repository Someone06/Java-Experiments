package deserilization.deserializers;

import deserilization.Deserializer;

public final class FloatDeserializer
        implements Deserializer.DeserializeClass<Float> {
    @Override
    public Class<Float> getDeserializedClass() {
        return Float.class;
    }

    @Override
    public Float deserialize(final String serialized) {
        try {
            return Float.valueOf(serialized);
        } catch (final NumberFormatException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
