package util;

public final class GetCaller {
    private static final int GET_CALLER_NAME_STACK_TRACE_INDEX = 4;

    private GetCaller() throws IllegalAccessException {
        throw new IllegalAccessException(
                "Cannot instantiate static helper class.");
    }

    /**
     * Get the name of the caller of the current function.
     * <p>
     * Offset is 0 to get the caller of the caller of this function.
     * Is n for the n-th caller of the caller of this function.
     *
     * @param offset Get the n-th caller of the caller of this function.
     * @throws NoSuchCaller If there is no caller with the given offset.
     * @throws IllegalArgumentException If the offset is less than zero.
     * @return The name of the specified caller.
     */
    public static String getCallerName(final int offset) {
        if (offset >= 0) {
            final var stackTrace = Thread.currentThread().getStackTrace();
            final var index = GET_CALLER_NAME_STACK_TRACE_INDEX + offset;
            if (index < stackTrace.length) {
                return stackTrace[index].getMethodName();
            } else {
                throw new NoSuchCaller("Offset " + offset + " is too large.");
            }
        } else {
            throw new IllegalArgumentException("Offset has to be >= 0.");
        }
    }

    /**
     * Get the caller of the current function.
     *
     * @throws NoSuchCaller If this method is called from {@code main()}.
     * @return The name of the method that called the method that called this
     * method.
     */
    public static String getCallerName() {
        return getCallerName(0);
    }

    public static final class NoSuchCaller extends IndexOutOfBoundsException {
        private NoSuchCaller(final String message) {
            super(message);
        }
    }
}
