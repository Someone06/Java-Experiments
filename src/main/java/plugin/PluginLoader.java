package plugin;

import dynamicClassLoader.LoadClassInPackage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class can be used to implement a plugin architecture, that is it can
 * be used to dynamically load classes and construct an object from that
 * class at runtime.
 * <p>
 * Plugins are concrete top-leve classes that reside in a specific package,
 * implement a given interface (or extend a given class) and have a * zero
 * argument constructor.
 */
public final class PluginLoader<T> {
    private final String packageName;
    private final Class<T> pluginInterface;


    public PluginLoader(final String packageName,
            final Class<T> pluginInterface) {
        this.packageName = Objects.requireNonNull(packageName);
        this.pluginInterface = Objects.requireNonNull(pluginInterface);
    }

    private static Optional<Constructor<?>> getZeroArgumentConstructor(
            final Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny();
    }

    private static Object construct(
            final Constructor<?> zeroArgumentConstructor) {
        /*
         * We know that the constructed class has a zero argument constructor
         * and that the class is concrete, so the only exception that could
         * be thrown here, is an exception thrown by the called constructor.
         */
        try {
            return zeroArgumentConstructor.newInstance();
        } catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (final InstantiationException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public Set<Supplier<T>> getConstructors() {
        /*
         * The unchecked cast is safe, because the constructor is obtained
         * from a class U, where a reference to an object of U can be assigned
         * to a reference to an object of type T.
         */
        return new LoadClassInPackage().loadClasses(packageName)
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .filter(pluginInterface::isAssignableFrom)
                .map(PluginLoader::getZeroArgumentConstructor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(constructor -> (Supplier<?>) (() -> construct(
                        constructor)))
                .map(constructor -> (Supplier<T>) constructor)
                .collect(Collectors.toSet());
    }
}