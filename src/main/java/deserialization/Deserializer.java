package deserialization;

import dynamicClassLoader.LoadClassInPackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Deserializer {
    private static final String PACKAGE = Deserializer.class.getPackageName()
            + ".deserializers";
    private static final Set<Supplier<DeserializeClass<?>>> constructors
            = getConstructors();

    private static Set<Supplier<DeserializeClass<?>>> getConstructors() {
        return new LoadClassInPackage().loadClasses(PACKAGE)
                .filter(Class::isLocalClass)
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .filter(DeserializeClass.class::isAssignableFrom)
                .map(Class::getConstructors)
                .map(constructors -> Arrays.stream(constructors)
                        .filter(constructor -> constructor.getParameterCount()
                                == 0)
                        .findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(constructor -> (Supplier<?>) (() -> {
                    try {
                        return constructor.newInstance();
                    } catch (final InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException |
                             IllegalAccessException e) {
                        throw new AssertionError(e);
                    }
                }))
                .map(constructor -> (Supplier<DeserializeClass<?>>) constructor)
                .collect(Collectors.toSet());
    }
}
