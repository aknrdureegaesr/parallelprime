package de.dj3ei.prime;

import java.util.function.IntConsumer;

public interface PrimeAlgorithm {
    void findPrimes(int max, IntConsumer primesEater);
}
