package deserializers;

import deserialization.DeserializeClass;

public final class StringDeSer implements DeserializeClass<String> {

    @Override
    public Class<String> getDeserializedClass() {
        return String.class;
    }

    @Override
    public String deserialize(final String serialized) {
        return serialized;
    }
}
