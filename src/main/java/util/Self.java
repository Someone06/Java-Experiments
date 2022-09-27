package util;

/**
 * A generic base class sometimes needs to refer to the type of subclass
 * extending the base class. Such a self type can be emulated in Java using
 * generics.
 * @param <T> The type of self.
 */
public interface Self<T extends Self<T>> {
    T self();
}
