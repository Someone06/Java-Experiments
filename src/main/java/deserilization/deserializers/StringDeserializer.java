package deserilization.deserializers;

import deserilization.Deserializer;

public final class StringDeserializer
        implements Deserializer.DeserializeClass<String> {

    @Override
    public Class<String> getDeserializedClass() {
        return String.class;
    }

    @Override
    public String deserialize(final String serialized) {
        return serialized;
    }
}
