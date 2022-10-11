package util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

public final class ListUtil {

    private ListUtil() throws IllegalAccessException {
        throw new IllegalAccessException(
                "Cannot instantiate static helper class.");
    }

    /**
     *  Applies a mapping function to every element in the given list in
     *  parallel, such that {@code result.get(i) == mapper.apply(list.get(i))}.
     *  The returned list is unmodifiable. To obtain a modifiable list wrap
     *  the returned list in an {@code ArrayList<>}:
     *  {@code new ArrayList<>(returnedList)}.
     * <p>
     *  The mapping function needs to be pure, meaning it is stateless. Doing
     * {@code mapper.apply(element)} has to return the same result no matter
     * to which elements the mapper has been previously applied, or to which
     * elements the mapper is concurrently applied.
     * <p>
     * Why not just do {@code list.stream().map(mapper).toList()}? Because I
     * found out about that function only after writing this code. It was
     * added in Java 16. Moreover, using {@code .toList()} requires at least
     * one more copy of the array backing the list, then this method.
     *
     * @param list The list containing the elements to be mapped.
     * @param mapper The mapping function to be applied to every element of
     *              the list. Is allowed to return {@code null} when applied.
     * @throws NullPointerException If any argument is {@code null}.
     * @return An unmodifiable list, containing the mapped elements.
     * @param <T> The type of the objects to which the mapping function
     *           needs to be applied.
     * @param <R> The type of  the objects return by the mapping function.
     */
    public static <T, R> List<R> parallelMapUnmodifiable(final List<T> list,
            final Function<T, R> mapper) {
        /*
         * Safety: The cast '(R[]) new Object[size]' is safe, because
         *     1) We only write E values into the array and
         *     2) We only pass the array to 'Arrays.asList()' which in turn
         *        creates a new 'List<R>' and sets the passed array as the
         *        array holding all the elements, which is of type 'Object[]'.
         */
        requireNonNull(list);
        requireNonNull(mapper);
        final var size = list.size();
        final var result = (R[]) new Object[size];
        IntStream.range(0, size).parallel().forEach(index -> {
            final var element = list.get(index);
            final var value = mapper.apply(element);
            result[index] = value;
        });

        /*
         * 'Arrays.asList()' returns a 'List<R>' which is backed by the
         * passed array. However, the List is not resizeable. Because that is
         *  not really a concept in the Java Collections framework, we turn
         * it into an unmodifiable List instead.
         *
         * Note 1: Do not replace this with 'List.of(result)', because 'List.of
         * ()' does not allow 'null' values, while this method does.
         *
         * Note 2: We could wrap the result in a 'new ArrayList<>()' to get a
         * modifiable and re-sizeable List, but that requires copying the
         * elements of the List twice (once to call '.toArray()' on the List
         * returned by 'Arrays.asList()' and the second time to copy the
         * elements of the array into the new ArrayList<>). That is
         * potentially expensive and the users can easily do it themselves if
         * needed, so we do not do it here.
         */
        return Collections.unmodifiableList(Arrays.asList(result));
    }
}
