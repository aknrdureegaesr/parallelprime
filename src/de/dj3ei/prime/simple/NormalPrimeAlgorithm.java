package de.dj3ei.prime.simple;

import java.util.function.IntConsumer;

import de.dj3ei.prime.PrimeAlgorithm;

public class NormalPrimeAlgorithm implements PrimeAlgorithm {
    
    public static final int tablePeriod = 2 * 3 * 5 * 7 * 11 * 13;
    public static final int table[];
    static {
        // Build a table of all numbers not divisible by 2, 3, 5, 7, 11, 13
        int tableSize = tablePeriod / 2 / 3 * 2 / 5 * 4 / 7 * 6 / 11 * 10 / 13 * 12;
        table = new int[tableSize];
        int nextFreeTableIndex = 0;
        for(int i = 0; i < tablePeriod; ++i) {
            if(0 == i % 2 || 0 == i % 3 || 0 == i % 5 || 0 == i % 7 || 0 == i % 11 || 0 == i % 13) {
                
            } else {
                table[nextFreeTableIndex++] = i;
            }
        }
        if(nextFreeTableIndex != tableSize) {
            throw new IllegalStateException("Have " + nextFreeTableIndex + ", expected " + tableSize);
        }
    }

    int intRoot(int a) {
        int x = a/2;
        int emergencyBrake = 40;
        while(! (x * x <= a && a < (x + 1) * (x + 1))) {
            x = (x + a/x)/2;
            if(--emergencyBrake <= 0)
                throw new IllegalStateException("Heron does not work for " + a);
        }
        return x;
    }

    private static class StoreInArray implements IntConsumer {
        int nextFree = 0;
        final private int arr[];
        public StoreInArray(int arr[]) {
            this.arr = arr;
        }
        @Override
        public void accept(int value) {
            arr[nextFree++] = value;
        }
        public int getNextFree() {
            return nextFree;
        }
    }

    public void findHighPrimes(int max, IntConsumer primesEater) {
        if(max < 17)
            return;
        int root = intRoot(max);
        int boundForNumberOfPrimesUpToRoot =
                (((((((root + 1) / 2) + 2) / 3 * 2 + 4) / 5 * 4 + 6) / 7 * 6 + 10) / 11 * 10 + 12) / 13 * 12;
        int tableOfHighPrimesUpToRoot[] = new int[boundForNumberOfPrimesUpToRoot];
        StoreInArray sia = new StoreInArray(tableOfHighPrimesUpToRoot);
        findHighPrimes(root, p -> {sia.accept(p); primesEater.accept(p);});
        int fillLengthOfHighPrimesTable = sia.getNextFree();
        if(0 < fillLengthOfHighPrimesTable) {
            for(int base = 0; base < max; base += tablePeriod) {
                for(int i = (0 == base ? 1 : 0); i < table.length; ++i) {
                    int primeCandidate = base + table[i];
                    if(max < primeCandidate) {
                        return;
                    }
                    boolean maybePrime = true;
                    int j = 0;
                    int divisorCandidate = tableOfHighPrimesUpToRoot[j];
                    while(maybePrime) {
                        maybePrime = 0 != primeCandidate % divisorCandidate;
                        if(maybePrime && ++j < fillLengthOfHighPrimesTable) {
                            divisorCandidate = tableOfHighPrimesUpToRoot[j];
                        } else {
                            break;
                        }
                    }
                    if(maybePrime)
                        primesEater.accept(primeCandidate);
                }
            }
        } else {
            for(int base = 0; base < max; base += tablePeriod) {
                for(int i = (0 == base ? 1 : 0); i < table.length; ++i) {
                    int primeCandidate = base + table[i];
                    if(max < primeCandidate) {
                        return;
                    }
                    primesEater.accept(primeCandidate);
                }
            }
        }
    }

    @Override
    public void findPrimes(int max, IntConsumer primesEater) {
        if(2 <= max) {
            primesEater.accept(2);
        }
        if (3 <= max) {
            primesEater.accept(3);
        }
        if (5 <= max) {
            primesEater.accept(5);            
        }
        if (7 <= max) {
            primesEater.accept(7);
        }
        if (11 <= max) {
            primesEater.accept(11);
        }
        if (13 <= max) {
            primesEater.accept(13);
        }
        if(17 <= max) {
            findHighPrimes(max, primesEater);
        }
    }
}
