package util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class Timing {

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

    public <T> T time(final Supplier<T> method) {
        requireNonNull(method);
        start();
        final var value = method.get();
        stop();
        return value;
    }

    public void time(final Runnable method) {
        requireNonNull(method);
        start();
        method.run();
        stop();
    }

    private void start() {
        startTime = Instant.now();
    }

    private void stop() {
        final var endTime = Instant.now();
        lastTiming = Duration.between(startTime, endTime);
        startTime = null;
        writer.println(formatter.apply(lastTiming));
    }
}
