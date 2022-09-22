package util;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

/**
 * A static helper class for dealing with interrupts and methods that can be
 * interrupted.
 */
public final class InterruptOperations {

    /**
     * Private constructor for a static helper class. Do not use.
     * @throws IllegalAccessException If the constructor is called.
     */
    private InterruptOperations() throws IllegalAccessException {
        throw new IllegalAccessException(
                "Cannot instantiate static helper class.");
    }

    /**
     * Put a thread to sleep for a specific amount of time.
     *
     * @param duration The duration for which the thread is put to sleep.
     * @throws NullPointerException If the duration is {@code null}.
     * @throws IllegalArgumentException If the duration is negative.
     * @throws InterruptedExceptionUnchecked If the thread gets interrupted
     * while sleeping.
     */
    public static void sleep(final Duration duration) {
        if (requireNonNull(duration).isNegative()) {
            throw new IllegalArgumentException(
                    "Duration must be non-negative.");
        }

        try {
            Thread.sleep(duration.toMillis());
        } catch (final java.lang.InterruptedException exception) {
            throw new InterruptedExceptionUnchecked(
                    "Interrupted while sleeping.", exception);
        }
    }

    /**
     * Test whether the thread has been interrupted.
     * Clears the interrupt flag and throws an
     * {@code InterruptedExceptionUnchecked}
     *
     * @throws InterruptedExceptionUnchecked If the thread has been interrupted.
     */
    public static void throwOnInterrupt() {
        if (Thread.interrupted()) {
            throw new InterruptedExceptionUnchecked();
        }
    }

    /**
     * A custom unchecked exception that signals that a thread has been
     * interrupted.
     */
    public static final class InterruptedExceptionUnchecked
            extends RuntimeException {
        private InterruptedExceptionUnchecked() {
            throw new RuntimeException();
        }

        private InterruptedExceptionUnchecked(final String message,
                final Throwable cause) {
            super(requireNonNull(message), requireNonNull(cause));
        }
    }

}
