package deserilization.deserializers;

import deserilization.Deserializer;

public final class BooleanDeserializer
        implements Deserializer.DeserializeClass<Boolean> {
    @Override
    public Class<Boolean> getDeserializedClass() {
        return Boolean.class;
    }

    @Override
    public Boolean deserialize(final String serialized) {
        try {
            return Boolean.valueOf(serialized);
        } catch (final NumberFormatException e) {
            throw new Deserializer.DeserializeClassException(e);
        }
    }
}
