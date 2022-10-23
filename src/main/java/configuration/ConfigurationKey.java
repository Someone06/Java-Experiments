package configuration;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;


public final class ConfigurationKey {
    private final static Pattern piecePattern = Pattern.compile("[a-zA-Z0-9]+");
    private final static Pattern keyPatter = Pattern.compile(
            "(?:/[a-zA-Z0-9]+)+");
    private final String key;

    public ConfigurationKey(final List<String> pieces) {
        if (!isValidKey(requireNonNull(pieces))) {
            throw new IllegalArgumentException(
                    "Invalid key. Has to consist of one or more pieces, each "
                            + "piece consisting of lowercase characters and "
                            + "digits");
        }

        key = '/' + String.join("/", pieces);
    }

    public ConfigurationKey(final String... pieces) {
        this(List.of(pieces));
    }

    public ConfigurationKey(final String key) {
        if (!isValidKey(requireNonNull(key))) {
            throw new IllegalArgumentException(
                    "Invalid key. Has to consist of one or more pieces, each "
                            + "piece consisting of lowercase characters and "
                            + "digits");
        }

        this.key = key;
    }

    private static boolean isValidKey(final String key) {
        return keyPatter.matcher(key).matches();
    }

    private static boolean isValidKey(final List<String> list) {
        return !list.isEmpty() && list.stream()
                .allMatch(piece -> piecePattern.matcher(requireNonNull(piece))
                        .matches());
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof ConfigurationKey o) {
            return this.key.equals(o.key);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key;
    }
}
