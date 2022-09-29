package util;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


/**
 * This class can be used to lazily initialize a variable in a thread-safe
 * manner.
 *
 * @param <T> The type of the variable to be initialized.
 */
public final class LazyInitialize<T> {
    private final Supplier<? extends T> constructor;
    // See: https://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
    private volatile T instance = null;

    public LazyInitialize(final Supplier<? extends T> constructor) {
        this.constructor = requireNonNull(constructor);
    }

    public T get() {
        if (instance == null) {
            synchronized (LazyInitialize.class) {
                if (instance == null) {
                    instance = constructor.get();
                    if (instance == null) {
                        throw new NullPointerException(
                                "Constructor returned null.");
                    }
                }
            }
        }

        return instance;
    }
}
