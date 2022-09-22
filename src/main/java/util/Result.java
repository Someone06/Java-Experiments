package util;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A {@code Result} can be used to return an {@code Ok} from a function,
 * signalling success, or return an {@code Err} signaling an error.
 * <p>
 * Both {@code Ok} and {@code Err} hold a non-null value.
 * <p>
 * The {@code Result} is modelled after the {@code Result} type in Rust, and can
 * be used as an alternative to exceptions, to model control flow more
 * explicitly.
 *
 * @param <T> The type of the value held by an {@code Ok}.
 * @param <E> The type of the value held by an {@code Err}.
 */
public abstract sealed class Result<T, E> permits Result.Err, Result.Ok {

    /**
     * Return {@code true}, if the {@code Result} is an {@code Ok}, else
     * returns {@code false}.
     *
     * @return Whether the {@code Result} is an {@code Ok}.
     */
    public abstract boolean isOk();

    /**
     * Return {@code true}, if the {@code Result} is an {@code Err}, else
     * returns {@code false}.
     *
     * @return Whether the {@code Result} is an {@code Err}.
     */
    public abstract boolean isErr();

    /**
     * Returns the value if the {@code Result} is an {@code Ok}, else throws
     * a {@code DifferentKindException}.
     *
     * @throws DifferentKindException If the {@code Result} is an {@code Err}.
     * @return The value if the {@code Result} is an {@code Ok}.
     */
    public abstract T getOk();

    /**
     * Returns the value if the {@code Result} is an {@code Err}, else throws
     * a {@code DifferentKindException}.
     *
     * @throws DifferentKindException If the {@code Result} is an {@code Ok}.
     * @return The value if the {@code Result} is an {@code Err}.
     */
    public abstract E getErr();

    /**
     * If the {@code Result} is an {@code Ok} returns the value of the
     * {@code Ok} otherwise returns the argument.
     *
     * @param otherwise The value returned in case the {@code Result} is an
     * {@code Err}.
     * @return The value of the {@code Ok} or the argument in case of an
     * {@code Err}.
     */
    public abstract T getOkOr(T otherwise);

    /**
     * If the {@code Result} is an {@code Err} returns the value of the
     * {@code Err} otherwise returns the argument.
     *
     * @param otherwise The value returned in case the {@code Result} is an
     * {@code Ok}.
     * @return The value of the {@code Err} or the argument in case of an
     * {@code Err}.
     */
    public abstract E getErrOr(E otherwise);

    /**
     * If the {@code Result} is an {@code Ok}, map the value of the {@code Ok}
     * using the given {@code function} to a new value.
     *
     * @param function The function used to map the value in case of an
     * {@code Ok}.
     * @param <U>  The type of the new value.
     * @throws NullPointerException if the function is {@code null}.
     * @throws NullPointerException if the new value obtained by applying the
     * function to the value of an {@code Ok} is {@code null}.
     * @return The mapped {@code Ok}, or the unchanged {@code Err}.
     */
    public abstract <U> Result<U, E> mapOk(Function<T, U> function);

    /**
     * If the {@code Result} is an {@code Err}, map the value of the {@code Err}
     * using the given {@code function} to a new value.
     *
     * @param function The function used to map the value in case of an
     * {@code Err}.
     * @param <R>  The type of the new value.
     * @throws NullPointerException if the function is {@code null}.
     * @throws NullPointerException if the new value obtained by applying the
     * function to the value of an {@code Err} is {@code null}.
     * @return The mapped {@code Err}, or the unchanged {@code Ok}.
     */
    public abstract <R> Result<T, R> mapErr(Function<E, R> function);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

    /**
     * The {@code Ok} type is a {@code Result} and signals that an operation was
     * successful.
     *
     * @param <T> The type of the value held by an {@code Ok}.
     * @param <E> The type of the value held by an {@code Err}.
     */
    public static final class Ok<T, E> extends Result<T, E> {
        private final T value;

        /**
         * {@inheritDoc}
         */
        public Ok(final T value) {
            this.value = requireNonNull(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isOk() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isErr() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T getOk() {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E getErr() {
            throw new DifferentKindException(
                    "Cannot get kind 'Err' from an 'Ok'.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T getOkOr(final T otherwise) {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E getErrOr(final E otherwise) {
            return otherwise;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Result<U, E> mapOk(final Function<T, U> function) {
            requireNonNull(function);
            return new Ok<>(function.apply(value));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <R> Result<T, R> mapErr(final Function<E, R> function) {
            requireNonNull(function);
            return new Ok<>(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof Ok<?, ?> ok) {
                return value.equals(ok.value);
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return value.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Ok{" + "value=" + value + '}';
        }
    }

    /**
     * The {@code Err} type is a {@code Result} and signals that an operation
     *  failed.
     *
     * @param <T> The type of the value held by an {@code Ok}.
     * @param <E> The type of the value held by an {@code Err}.
     */
    public static final class Err<T, E> extends Result<T, E> {
        private final E value;

        /**
         * {@inheritDoc}
         */
        public Err(final E value) {
            this.value = requireNonNull(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isOk() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isErr() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T getOk() {
            throw new DifferentKindException(
                    "Cannot get kind 'Ok' from an 'Err'.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E getErr() {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T getOkOr(final T otherwise) {
            return otherwise;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E getErrOr(final E otherwise) {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Result<U, E> mapOk(final Function<T, U> function) {
            requireNonNull(function);
            return new Err<>(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <R> Result<T, R> mapErr(final Function<E, R> function) {
            requireNonNull(function);
            return new Err<>(function.apply(value));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof Err<?, ?> err) {
                return value.equals(err.value);
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return value.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Err{" + "value=" + value + '}';
        }
    }

    /**
     * This exception signals that an operation tried to access an {@code Ok}
     * but got an {@code Err}, or conversely an operation tried to access an
     * {@code Err}, but got an {@code Ok}.
     */
    public static final class DifferentKindException
            extends IllegalStateException {

        private DifferentKindException(final String message) {
            super(message);
        }
    }
}
