package util;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Long.highestOneBit;

// Based on https://stackoverflow.com/a/29158917
public final class RandomPermutationGenerator
        implements PrimitiveIterator.OfLong {

    final long a = 1664525L;

    final long c = 1013904223L;

    final long N;

    final long seed;

    final long m;

    final long aModM;

    final long cModM;

    long next;

    boolean hasNext = true;

    public RandomPermutationGenerator(final long N, final long seed) {
        if (N <= 0L || N >>> 62 == 1L) {
            throw new IllegalArgumentException("Require 0 < N < 2^63");
        }

        if (seed >= N) {
            throw new IllegalArgumentException("Seed has to be < N");
        }

        this.N = N;
        this.m = highestOneBit(N) << 1;
        this.aModM = a % this.m;
        this.cModM = c % this.m;
        this.seed = seed;
        this.next = seed;
    }

    public RandomPermutationGenerator(final long N, final Random random) {
        this(N, random.nextInt((int) Math.min(N, Integer.MAX_VALUE)));
    }

    public RandomPermutationGenerator(final long N) {
        this(N, ThreadLocalRandom.current());
    }

    private long advance() {
        do next = (aModM * (next % m) % m + cModM) % m; while (next >= N);
        hasNext = next != seed;
        return next;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public long nextLong() {
        if (hasNext) return advance();
        throw new NoSuchElementException();
    }

    public void reset() {
        hasNext = true;
        next = seed;
    }

    @Override
    public String toString() {
        return "RandomPermutationGenerator{"
                + "N=%d, seed=%d, m=%d, next=%d, hasNext=%s}".formatted(
                N, seed, m, next, hasNext);
    }
}
