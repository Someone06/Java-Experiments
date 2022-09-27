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

public final class Timing {

    public void time(final Timeable method) {
        requireNonNull(method);
        start();

        try {
            method.run();
        } finally {
            stop();
        }
    }

    public <T> T time(final TimeableResult<T> method) {
        requireNonNull(method);
        start();

        try {
            return method.run();
        } finally {
            stop();
        }
    }

    public <E extends Throwable> void timeThrows(
            final TimeableMayThrow<E> method) throws E {
        requireNonNull(method);
        start();

        try {
            method.run();
        } finally {
            stop();
        }
    }

    public <T, E extends Throwable> T timeThrows(
            final TimeableResultMayThrow<T, E> method) throws E {
        requireNonNull(method);
        start();

        try {
            return method.run();
        } finally {
            stop();
        }
    }
    private final PrintWriter writer;
    private final Function<Duration, String> formatter;

    private Temporal startTime = null;
    private Duration lastTiming = null;

    public Timing() {
        this.writer = new PrintWriter(OutputStream.nullOutputStream());
        this.formatter = duration -> "";
    }

    public Timing(final Writer writer) {
        this.writer = new PrintWriter(requireNonNull(writer));
        this.formatter = Duration::toString;
    }

    public Timing(final OutputStream outputStream) {
        this.writer = new PrintWriter(requireNonNull(outputStream));
        this.formatter = Duration::toString;
    }

    public Timing(final Writer writer,
            final Function<Duration, String> formatter) {
        this.writer = new PrintWriter(requireNonNull(writer));
        this.formatter = requireNonNull(formatter);
    }

    public Timing(final OutputStream outputStream,
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

    private void stop() {
        final var endTime = Instant.now();
        final var startTime = this.startTime;
        this.startTime = null;
        lastTiming = Duration.between(startTime, endTime);
        writer.println(formatter.apply(lastTiming));
    }

    @FunctionalInterface
    public interface TimeableResult<T> {
        T run();
    }

    @FunctionalInterface
    public interface TimeableMayThrow<E extends Throwable> {
        void run() throws E;
    }

    private void start() {
        startTime = Instant.now();
    }

    @FunctionalInterface
    public interface TimeableResultMayThrow<T, E extends Throwable> {
        T run() throws E;
    }
}
