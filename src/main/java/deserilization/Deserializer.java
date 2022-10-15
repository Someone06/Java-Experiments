package deserilization;

import plugin.PluginLoader;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public final class Deserializer {

    private static final String DESERIALIZERS_PACKAGE =
            Deserializer.class.getPackageName() + ".deserializers";

    private final static Set<Supplier<DeserializeClass<?>>>
            deserializerConstructors = new PluginLoader<>(
            DESERIALIZERS_PACKAGE,
            getDeserializerClassType()
    ).getConstructors();
    private final Map<Class<?>, DeserializeClass<?>> deserializers;

    public Deserializer() {
        try {
            deserializers = deserializerConstructors.stream()
                    .map(Supplier::get)
                    .collect(toMap(DeserializeClass::getDeserializedClass,
                                   identity()
                                  ));
        } catch (final IllegalStateException duplicateKey) {
            throw new MultipleDeserializersForClassException(duplicateKey);
        }
    }

    private static Class<DeserializeClass<?>> getDeserializerClassType() {
        /*
         * Getting the Class type of a generic type requires an unchecked
         * cast. It is safe, because DeserializeClass<?> gets type erased to
         * DeserializedClass by the compiler anyway.
         */
        return (Class<DeserializeClass<?>>) (Class<?>) DeserializeClass.class;
    }

    public <T> T deserialize(final Class<T> target, final String serialized) {
        requireNonNull(target);
        requireNonNull(serialized);

        return getDeserializer(target).map(d -> d.deserialize(serialized))
                .orElseThrow(() -> new NoDeserializerForClassException(target));
    }

    private <T> Optional<DeserializeClass<T>> getDeserializer(
            final Class<T> clazz) {
        /*
         * We populate the map by mapping each class to the type it deserializes
         * to, so this unchecked cast is safe.
         */
        return Optional.ofNullable(
                (DeserializeClass<T>) deserializers.get(clazz));
    }

    public interface DeserializeClass<T> {
        Class<T> getDeserializedClass();

        T deserialize(final String serialized);
    }

    public static final class MultipleDeserializersForClassException
            extends IllegalStateException {
        public MultipleDeserializersForClassException(final Throwable cause) {
            super(cause);
        }
    }

    public static final class NoDeserializerForClassException
            extends NoSuchElementException {
        public NoDeserializerForClassException(final Class<?> clazz) {
            super(String.format(
                    "Found no deserializer to deserialize to class '%s'.",
                    clazz
                               ));
        }
    }
}
