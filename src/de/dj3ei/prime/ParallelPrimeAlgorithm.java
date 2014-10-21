/*
 * Copyright © 2014 Dr. Andreas Krüger, dj3ei@famsik.de.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dj3ei.prime;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.StreamSupport;

public class ParallelPrimeAlgorithm extends SmallPrimes implements PrimeAlgorithm {
    
    private static class StoreInArray implements IntConsumer {
        int nextFree = 0;
        final private int arr[];
        public StoreInArray(int arr[]) {
            this.arr = arr;
        }
        @Override
        synchronized public void accept(int value) {
            arr[nextFree++] = value;
        }
        synchronized public int getNextFree() {
            return nextFree;
        }
    }

    private class HighPrimesSpliterator implements Spliterator.OfInt {

        final int tableOfHighPrimesUpToRoot[];
        int base;
        int i;
        final int max;

        /**
         * This will iterate through all primes starting from
         * base + table[i] or the largest element of tableOfHighPrimesUpToRoot (whichever is larger)
         * up to max.
         */
        public HighPrimesSpliterator(int max, int tableOfHighPrimesUpToRoot[], int i, int base) {
            this.max = max;
            this.tableOfHighPrimesUpToRoot = tableOfHighPrimesUpToRoot;
            this.base = base;
            this.i = i;
        }

        @Override
        public long estimateSize() {
            int result = max - base - table[i];
            return result;
        }

        /** Why do I need to specify this? This is the well known primitive type, after all... */
        @Override
        public Comparator<Integer> getComparator() {
            return (i,j) -> j - i;
        }

        @Override
        public int characteristics() {
            return IMMUTABLE | NONNULL | SORTED | ORDERED | DISTINCT;            
        }

        @Override
        public java.util.Spliterator.OfInt trySplit() {
            int newBase = base + ((max - base) / tablePeriod / 2) * tablePeriod;
            int newMax =  newBase + table[0] - 1;
            if(base + table[i] < newMax && newMax < max) {
                HighPrimesSpliterator result = new HighPrimesSpliterator(newMax, tableOfHighPrimesUpToRoot, i, base);
                base = newBase;
                i = 0;
                return result;
            } else {
                return null;
            }
        }

        @Override
        public boolean tryAdvance(IntConsumer action) {
            int primeCandidate = table[i] + base;
            boolean result = false;
            while (!result && primeCandidate <= max) {
                if (table.length <= ++i) {
                    i = 0;
                    base += tablePeriod;
                }
                boolean mayBePrime = true;
                for (int j = 0;
                     mayBePrime && j < tableOfHighPrimesUpToRoot.length;
                     ++j) {
                    mayBePrime =
                            !(0 == primeCandidate % tableOfHighPrimesUpToRoot[j]);
                }
                if (mayBePrime) {
                    action.accept(primeCandidate);
                    result = true;
                } else {
                    primeCandidate = base + table[i];
                }
            }
            return result;
        }


        @Override
        public void forEachRemaining(IntConsumer action) {
            allDone: while (base < max) {
                while (i < table.length) {
                    int primeCandidate = base + table[i];
                    if (primeCandidate <= max) {
                        boolean mayBePrime = true;
                        for (int j = 0;
                             mayBePrime && j < tableOfHighPrimesUpToRoot.length;
                             ++j) {
                            mayBePrime =
                                    !(0 == primeCandidate % tableOfHighPrimesUpToRoot[j]);
                        }
                        if (mayBePrime) {
                            action.accept(primeCandidate);
                        }
                    } else {
                        break allDone;
                    }
                    i++;
                }
                i = 0;
                base += tablePeriod;
            }
        }
    }

    private static class TConsumer implements IntConsumer {
        final IntConsumer a, b;
        public TConsumer(IntConsumer a, IntConsumer b) {
            if(null == a) throw new NullPointerException("a is null");
            if(null == b) throw new NullPointerException("b is null");
            this.a = a;
            this.b = b;
        }

        @Override
        public void accept(int value) {
            a.accept(value);
            b.accept(value);
        }
    }

    private HighPrimesSpliterator makeIterator(int max, IntConsumer primesEater) {
        if(max <= (1 + SMALL_PRIMES[SMALL_PRIMES.length - 1]) * (1 + SMALL_PRIMES[SMALL_PRIMES.length - 1])) {
            return new HighPrimesSpliterator(max, new int[0], 1, 0);
        }
        int root = Util.intRoot(max);
        int boundForNumberOfPrimesUpToRoot = // pessimistic...
                Arrays.stream(SMALL_PRIMES).reduce(root, (r,p) -> (r + p - 1) / p * (p-1));
        int tableOfHighPrimesUpToRoot[] = new int[boundForNumberOfPrimesUpToRoot];
        StoreInArray sia = new StoreInArray(tableOfHighPrimesUpToRoot);
        TConsumer t = new TConsumer(sia, primesEater);
        StreamSupport.intStream(makeIterator(root, t), false).forEachOrdered(t);
        HighPrimesSpliterator result = new HighPrimesSpliterator(max, Arrays.copyOf(tableOfHighPrimesUpToRoot, sia.getNextFree()), 1, 0);
        return result;
    }

    @Override
    public void findPrimes(int max, IntConsumer primesEater) {
        Arrays.stream(SMALL_PRIMES).filter(p -> p <= max).forEachOrdered(primesEater);
        if(SMALL_PRIMES[SMALL_PRIMES.length - 1] <= max) {
            synchronized(primesEater) {
                StreamSupport.intStream(makeIterator(max, primesEater), true).forEachOrdered(primesEater);
            }
        }
    }
}
