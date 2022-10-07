package visitor;

import util.OptionalUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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
        String visitMethodName = "visit";

        default void dispatch(final Object argument) {
            final var visit = findVisitMethod(argument.getClass());
            callVisitMethod(visit, argument);

            if (argument instanceof Visitable visitable) {
                visitable.accept(this);
            }
        }

        private void callVisitMethod(final Method visit,
                final Object argument) {
            try {
                visit.invoke(this, argument);
            } catch (final IllegalAccessException |
                           InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private Optional<Method> getVisitMethod(final Class<?> argument) {
            try {
                return Optional.of(
                        getClass().getMethod(visitMethodName, argument));
            } catch (final NoSuchMethodException ignored) {
                return Optional.empty();
            }
        }

        /**
         * Includes the passed class itself (if it is not
         * {@link java.lang.Object}), but excludes class
         * {@link java.lang.Object}.
         */
        private Stream<Class<?>> getClassAndSuperclasses(final Class<?> clazz) {
            return Stream.iterate(
                    clazz, c -> c != Object.class, Class::getSuperclass);
        }


        private Optional<Method> findInClass(Class<?> argument) {
            return getClassAndSuperclasses(argument).map(this::getVisitMethod)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        }

        private Optional<Method> findInInterface(final Class<?> argument) {
            // Note: Order matters.
            return Arrays.stream(argument.getInterfaces())
                    .map(this::getVisitMethod)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        }

        private Method findFallbackMethod() {
            try {
                return this.getClass().getMethod("other", Object.class);
            } catch (final NoSuchMethodException noSuchMethodException) {
                throw new AssertionError("Unreachable", noSuchMethodException);
            }
        }

        private Method findVisitMethod(final Class<?> argument) {
            return OptionalUtil.or(this::findFallbackMethod,
                                   () -> findInClass(argument),
                                   () -> findInInterface(argument)
                                  );
        }

        void other(Object o);
    }

    public interface Visitable {
        void accept(Visitor visitor);
    }
}
