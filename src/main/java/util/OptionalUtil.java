package util;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class OptionalUtil {
    private OptionalUtil() throws IllegalAccessException {
        throw new IllegalAccessException(
                "Cannot instantiate static helper class.");
    }

    public static <T> Optional<T> or(
            final List<Supplier<Optional<? extends T>>> candidates) {
        requireNonNull(candidates);
        return candidates.stream()
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(t -> (T) t);
    }

    @SafeVarargs
    public static <T> Optional<T> or(
            final Supplier<Optional<? extends T>>... candidates) {
        return or(List.of(candidates));

    }

    public static <T> T or(final Supplier<? extends T> fallback,
            final List<Supplier<Optional<? extends T>>> candidates) {
        requireNonNull(candidates);
        requireNonNull(fallback);
        return or(candidates).orElseGet(fallback);
    }

    @SafeVarargs
    public static <T> T or(final Supplier<? extends T> fallback,
            final Supplier<Optional<? extends T>>... candidates) {
        return or(fallback, List.of(candidates));
    }
}
