package util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Powerset<T> implements Iterable<Powerset.Subset<T>> {
    public static final int MAX_INPUT_SIZE = Long.SIZE - 1;
    private final List<T> elements;

    public Powerset(final Set<T> set) {
            if (set.size() >= MAX_INPUT_SIZE) {
                throw new IllegalArgumentException(
                        "The input size can be at most %d.".formatted(
                                MAX_INPUT_SIZE));
            }

            elements = List.copyOf(set);
    }

    @Override
    public Iterator<Subset<T>> iterator() {
        return new PowersetIterator<>(elements);
    }

    @Override
    public Spliterator<Subset<T>> spliterator() {
        return new PowersetSpliterator<>(elements);
    }

    private static final class PowersetSpliterator<T> implements Spliterator<Subset<T>> {
        private final List<T> elements;
        private int advanceBy = 0;
        private long counter = 0;

        private PowersetSpliterator(final List<T> elements,  long counter, final int advanceBy) {
            this.elements = elements;
            this.advanceBy = advanceBy;
            this.counter = counter;
        }

        public PowersetSpliterator(final List<T> elements) {
           this(elements, 0L, 0);
        }

        @Override
        public boolean tryAdvance(final Consumer<? super Subset<T>> consumer) {
            // TODO: Check for overflow
            final long adv = 1L << advanceBy;
            if(counter + adv < 1L << elements.size())
                return false;

            final var result = new Subset<>(elements, counter);
            consumer.accept(result);
            counter += adv;
            return true;
        }

        @Override
        public Spliterator<Subset<T>> trySplit() {
            if(estimateSize() <= 1)
                return null;

            final var other = new PowersetSpliterator<T>(elements, counter + (1L << advanceBy), advanceBy + 1);
            ++advanceBy;
            return other;
        }

        @Override
        public long estimateSize() {
            final long total = 1L << elements.size();
            final long remaining = total - counter;
            return remaining >>> advanceBy;
        }

        @Override
        public int characteristics() {
            return DISTINCT | IMMUTABLE | NONNULL | SIZED | SUBSIZED;
        }
    }

    private static final class PowersetIterator<T> implements Iterator<Subset<T>> {

        private final List<T> elements;
        private long counter = 0;

        private PowersetIterator(final List<T> elements) {
            this.elements = elements;
        }

        @Override
        public boolean hasNext() {
            return (counter >>> elements.size()) != 1L;
        }

        @Override
        public Powerset.Subset<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return new Powerset.Subset<>(elements, counter++);
        }

        public void reset() {
            counter = 0;
        }

    }

    public static final class Subset<T> implements Set<T> {

        private final List<T> elements;
        private final long bitset;

        private Subset(final List<T> elements, final long bitset) {
            this.elements = elements;
            this.bitset = bitset;
        }

        @Override
        public int size() {
            return Long.bitCount(bitset);
        }

        @Override
        public boolean isEmpty() {
            return bitset == 0;
        }

        @Override
        public boolean contains(final Object o) {
            if (o == null)
                return false;
            return stream().anyMatch(o::equals);
        }

        @Override
        public Iterator<T> iterator() {
            return new SubsetIterator<>(elements, bitset);
        }

        @Override
        public Object[] toArray() {
            return stream().toArray();
        }

        @Override
        public <U> U[] toArray(final U[] a) {
            return stream().toList().toArray(a);
        }

        @Override
        public boolean add(final T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(final Collection<?> collection) {
            return collection.stream().allMatch(this::contains);
        }

        @Override
        public boolean addAll(final Collection<? extends T> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(final Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(final Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(final Predicate<? super T> predicate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            return o instanceof final Set<?> that && this.size() == that.size()
                    && that.containsAll(this) && this.containsAll(that);
        }

        @Override
        public int hashCode() {
            return elements.hashCode() + 31 * Long.hashCode(bitset);
        }

        @Override
        public Stream<T> stream() {
            return IntStream.range(0, elements.size()).filter(this::isSet).mapToObj(elements::get);
        }

        @Override
        public Stream<T> parallelStream() {
            return IntStream.range(0, elements.size())
                    .parallel()
                    .filter(this::isSet)
                    .mapToObj(elements::get);
        }

        private boolean isSet(final int index) {
            return ((bitset >>> index) & 1L) == 1L;
        }

        private static final class SubsetIterator<T> implements Iterator<T> {
            private final List<T> elements;
            private final long bitset;
            private int counter = 0;

            private SubsetIterator(final List<T> elements, final long bitset) {
                this.elements = elements;
                this.bitset = bitset;
            }

            @Override
            public boolean hasNext() {
                return 1L << counter <= bitset;
            }

            @Override
            public T next() {
                while ((bitset & 1L << counter) == 0L) {
                    ++counter;
                }
                return elements.get(counter++);
            }
        }
    }
}