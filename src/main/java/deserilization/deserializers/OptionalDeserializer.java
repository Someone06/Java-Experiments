package deserilization.deserializers;

import deserilization.Deserializer;

import java.util.Optional;
import java.util.regex.Pattern;

public final class OptionalDeserializer
        implements Deserializer.DeserializeClass<Optional<String>> {

    private static final String REGEX = "Optional\\[(.*)\\]";

    private static final Pattern pattern = Pattern.compile(REGEX);


    @Override
    public Class<Optional<String>> getDeserializedClass() {
        return (Class<Optional<String>>) (Class<?>) Optional.class;
    }


    @Override
    public Optional<String> deserialize(final String serialized) {
        if (serialized == null) {
            throw new Deserializer.DeserializeClassException(
                    "Cannot deserialize a null value.");
        }

        if ("Optional.empty".equals(serialized)) {
            return Optional.empty();
        }

        var matcher = pattern.matcher(serialized);
        if (!matcher.matches()) {
            throw new Deserializer.DeserializeClassException(
                    "Cannot deserialize '%s' as a Optional."
                        .formatted(serialized));
        }

        var matched = matcher.toMatchResult().group();
        return Optional.of(matched);
    }
}
