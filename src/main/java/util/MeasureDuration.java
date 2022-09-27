package util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * This class can be used to measure the time certain operations take.
 */
public final class MeasureDuration {

    public void time(final Timeable method) {
        requireNonNull(method);

        try (final var ignored = new MeasureDurationInner()) {
            method.run();
        }
    }

    public <T> T time(final TimeableResult<T> method) {
        requireNonNull(method);

        try (final var ignored = new MeasureDurationInner()) {
            return method.run();
        }
    }

    public <E extends Throwable> void timeThrows(
            final TimeableMayThrow<E> method) throws E {
        requireNonNull(method);

        try (final var ignored = new MeasureDurationInner()) {
            method.run();
        }
    }

    public <T, E extends Throwable> T timeThrows(
            final TimeableResultMayThrow<T, E> method) throws E {
        requireNonNull(method);

        try (final var ignored = new MeasureDurationInner()) {
            return method.run();
        }
    }
    private final PrintWriter writer;

    private final Function<Duration, String> formatter;

    private Duration lastTiming = null;

    public MeasureDuration() {
        this.writer = new PrintWriter(OutputStream.nullOutputStream());
        this.formatter = duration -> "";
    }

    public MeasureDuration(final Writer writer) {
        this.writer = new PrintWriter(requireNonNull(writer));
        this.formatter = Duration::toString;
    }

    public MeasureDuration(final OutputStream outputStream) {
        this.writer = new PrintWriter(requireNonNull(outputStream));
        this.formatter = Duration::toString;
    }

    public MeasureDuration(final Writer writer,
            final Function<Duration, String> formatter) {
        this.writer = new PrintWriter(requireNonNull(writer));
        this.formatter = requireNonNull(formatter);
    }

    public MeasureDuration(final OutputStream outputStream,
            final Function<Duration, String> formatter) {
        this.writer = new PrintWriter(requireNonNull(outputStream));
        this.formatter = requireNonNull(formatter);
    }

    public Optional<Duration> getLastTiming() {
        return Optional.ofNullable(lastTiming);
    }

    @FunctionalInterface
    public interface Timeable {
        void run();
    }

    @FunctionalInterface
    public interface TimeableResult<T> {
        T run();
    }

    @FunctionalInterface
    public interface TimeableMayThrow<E extends Throwable> {
        void run() throws E;
    }

    @FunctionalInterface
    public interface TimeableResultMayThrow<T, E extends Throwable> {
        T run() throws E;
    }

    private final class MeasureDurationInner implements AutoCloseable {
        private final Temporal startTime;

        private MeasureDurationInner() {
            startTime = Instant.now();
        }

        @Override
        public void close() {
            final var endTime = Instant.now();
            lastTiming = Duration.between(startTime, endTime);
            writer.println(formatter.apply(lastTiming));
        }
    }
}
