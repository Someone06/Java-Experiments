package deserialization;

public interface DeserializeClass<T> {

    Class<T> getDeserializedClass();

    T deserialize(String serialized);
}
