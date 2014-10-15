package de.dj3ei.prime;

import java.util.Arrays;

public abstract class SmallPrimes {
    
    // The int boundary overflows at tablePeriod when all primes up to including 29 are listed.
    // Runtime does not strongly depend on how many primes we list here for Normal, but does for Parallel.
    protected static final int SMALL_PRIMES[] = new int[] {2, 3 , 5, 7, 11, 13};
    
    protected final int tablePeriod;
    protected final int table[];

    protected SmallPrimes() {
        tablePeriod =  Arrays.stream(SMALL_PRIMES).reduce(1, (x,y) -> x*y);
        int tableSize = Arrays.stream(SMALL_PRIMES).reduce(tablePeriod, (x,y) -> x * (y-1) / y);
        table = new int[tableSize];
        int nextFreeTableIndex = 0;
        for(int i = 0; i < tablePeriod; ++i) {
            final int i2 = i;
            if(Arrays.stream(SMALL_PRIMES).anyMatch(p -> 0 == i2 % p)) {
            } else {
                table[nextFreeTableIndex++] = i2;
            }
        }
        if(nextFreeTableIndex != tableSize) {
            throw new IllegalStateException("Have " + nextFreeTableIndex + ", expected " + tableSize);
        }
    }
}
