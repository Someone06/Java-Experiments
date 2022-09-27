package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class MeasureDurationTest {

    private static final Integer RETURN_VALUE = 21;

    private final MeasureDuration measureDuration = new MeasureDuration();

    private void voidMethodNoThrow() {
    }

    private Integer intMethodNoThrow() {
        return RETURN_VALUE;
    }

    private void voidMethodThrows() throws Exception {
        throw new Exception("Test exception.");
    }

    private Integer intMethodThrows() throws Exception {
        throw new Exception("Test exception.");
    }

    @Test
    void test_time_no_return_no_throw() {
        assertDoesNotThrow(() -> measureDuration.time(this::voidMethodNoThrow));
    }

    @Test
    void test_time_int_return_no_throw() {
        assertEquals(
                RETURN_VALUE, measureDuration.time(this::intMethodNoThrow));
    }

    @Test
    void test_time_no_return_throws() {
        assertThrows(
                Exception.class,
                () -> measureDuration.timeThrows(this::voidMethodThrows)
                    );
    }

    @Test
    void test_time_int_return_throws() {
        assertThrows(
                Exception.class,
                () -> measureDuration.timeThrows(this::intMethodThrows)
                    );
    }
}
