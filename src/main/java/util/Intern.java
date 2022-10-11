package util;

import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import static java.util.Objects.requireNonNull;

/**
 * A simple interning facility. By de-duplicating an interned element and
 * just yielding a handle to the element, we can compare two interned
 * elements by comparing their handles in 0(1). Interned elements cannot be
 * {@code null}.
 *
 * @param <T> The type of object to be interned.
 */
public final class Intern<T> {

    private static final Cleaner cleaner = Cleaner.create();
    private final HashMap<T, WeakReference<InternHandle<T>>> map
            = new HashMap<>();

    public InternHandle<T> intern(final T element) {
        requireNonNull(element);

        final var handleRef = map.get(element);
        if (handleRef != null) {
            final var handle = handleRef.get();
            return (handle == null) ? addElement(element) : handle;
        } else {
            return addElement(element);
        }
    }

    private InternHandle<T> addElement(final T element) {
        final var handle = new InternHandle<>(element);
        map.put(element, new WeakReference<>(handle));
        registerHandleForCleaning(handle);
        return handle;
    }

    private void registerHandleForCleaning(final InternHandle<T> handle) {
        final var element = handle.get();
        cleaner.register(handle, () -> map.remove(element));
    }

    public static final class InternHandle<T> {
        private final T element;

        private InternHandle(final T element) {
            this.element = requireNonNull(element);
        }

        public T get() {
            return element;
        }

        @Override
        public boolean equals(final Object other) {
            return this == other;
        }

        @Override
        public int hashCode() {
            return element.hashCode();
        }

        @Override
        public String toString() {
            return "InternHandle{" + element + '}';
        }
    }
}
