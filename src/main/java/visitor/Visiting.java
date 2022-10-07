package visitor;

import util.OptionalUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Visitor pattern in Java using reflection.
 * Based on
 * <a href="https://www.infoworld.com/article/2077602/java-tip-98--reflect-on-the-visitor-design-pattern.html">this</a>.
 *
 */
public final class Visiting {

    private Visiting() throws IllegalAccessException {
        throw new IllegalAccessException(
                "Cannot instantiate static helper class.");
    }

    public interface Visitor {
        default void dispatch(final Object object) {
            final Method method = getMethod(object.getClass());

            try {
                method.invoke(this, object);
            } catch (final IllegalAccessException |
                           InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            if (object instanceof Visitable) {
                callAccept((Visitable) object);
            }


        }

        private void callAccept(final Visitable visitable) {
            visitable.accept(this);
        }

        private Optional<Method> findInClass(Class<?> clazz) {
            Method m = null;
            while (m == null && clazz != Object.class) {
                try {
                    m = getClass().getMethod("visit", clazz);
                } catch (final NoSuchMethodException ignored) {
                    clazz = clazz.getSuperclass();
                }
            }

            return Optional.ofNullable(m);
        }

        private Optional<Method> findInInterface(final Class<?> clazz) {
            // Note: Order of iteration matters.
            final Class<?>[] interfaces = clazz.getInterfaces();
            for (final Class<?> anInterface : interfaces) {
                try {
                    return Optional.of(
                            getClass().getMethod("visit", anInterface));
                } catch (final NoSuchMethodException ignored) {
                }
            }

            return Optional.empty();
        }

        private Method findFallbackMethod() {
            try {
                return this.getClass().getMethod("other", Object.class);
            } catch (final NoSuchMethodException noSuchMethodException) {
                throw new AssertionError("Unreachable", noSuchMethodException);
            }
        }

        private Method getMethod(final Class<?> clazz) {
            return OptionalUtil.or(this::findFallbackMethod,
                                   () -> findInClass(clazz),
                                   () -> findInInterface(clazz)
                                  );
        }

        void other(Object o);
    }

    public interface Visitable {
        void accept(Visitor visitor);
    }


}
