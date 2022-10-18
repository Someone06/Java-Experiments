package deserilization.deserializers;

import deserilization.Deserializer;

public final class DoubleDeserializer
        implements Deserializer.DeserializeClass<Double> {
    @Override
    public Class<Double> getDeserializedClass() {
        return Double.class;
    }

    @Override
    public Double deserialize(final String serialized) {
        try {
            return Double.valueOf(serialized);
        } catch (final NumberFormatException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
