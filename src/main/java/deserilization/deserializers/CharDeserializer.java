package deserilization.deserializers;

import deserilization.Deserializer;

public final class CharDeserializer
        implements Deserializer.DeserializeClass<Character> {
    @Override
    public Class<Character> getDeserializedClass() {
        return Character.class;
    }

    @Override
    public Character deserialize(final String serialized) {
        if (serialized.length() != 1) {
            throw new Deserializer.DeserializeClassException(
                    "The string has be of length 1.");
        } else {
            return serialized.charAt(0);
        }
    }
}
