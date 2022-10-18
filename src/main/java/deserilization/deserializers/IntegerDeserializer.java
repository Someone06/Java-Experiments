package deserilization.deserializers;

import deserilization.Deserializer;

public final class IntegerDeserializer
        implements Deserializer.DeserializeClass<Integer> {
    @Override
    public Class<Integer> getDeserializedClass() {
        return Integer.class;
    }

    @Override
    public Integer deserialize(final String serialized) {
        try {
            return Integer.valueOf(serialized);
        } catch (final NumberFormatException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
