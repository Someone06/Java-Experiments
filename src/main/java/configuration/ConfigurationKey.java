package configuration;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

public final class ConfigurationKey {

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
        final var len = key.length();

        if (len < 2 || key.charAt(0) != '/' || key.charAt(len - 1) == '/') {
            return false;
        } else {
            return IntStream.range(1, len).noneMatch(i -> {
                final var c = key.charAt(i);
                return isLowercaseOrDigit(c) || (c == '/'
                        && key.charAt(i - 1) != '/');
            });
        }
    }

    private static boolean isValidKey(final List<String> list) {
        return !list.isEmpty() && list.stream()
                .allMatch(ConfigurationKey::isValidPiece);
    }

    private static boolean isValidPiece(final String piece) {
        return !piece.isEmpty() && piece.codePoints()
                .allMatch(ConfigurationKey::isLowercaseOrDigit);

    }

    private static boolean isLowercaseOrDigit(final int codePoint) {
        return Character.isLowerCase(codePoint) || Character.isDigit(codePoint);
    }

    private static boolean isLowercaseOrDigit(final char c) {
        return Character.isLowerCase(c) || Character.isDigit(c);
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
