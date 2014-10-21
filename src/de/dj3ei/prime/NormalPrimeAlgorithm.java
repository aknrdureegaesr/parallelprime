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
import java.util.function.IntConsumer;

public class NormalPrimeAlgorithm extends SmallPrimes implements PrimeAlgorithm {

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

    private void findHighPrimes(int max, IntConsumer primesEater) {
        if(max < SMALL_PRIMES[SMALL_PRIMES.length-1])
            return;
        int root = Util.intRoot(max);
        int boundForNumberOfPrimesUpToRoot = // pessimistic...
                Arrays.stream(SMALL_PRIMES).reduce(root, (r,p) -> (r + p - 1) / p * (p-1));
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
        Arrays.stream(SMALL_PRIMES).filter(p -> p <= max).forEachOrdered(primesEater);
        if(SMALL_PRIMES[SMALL_PRIMES.length - 1] <= max) {
            findHighPrimes(max, primesEater);
        }
    }
}
