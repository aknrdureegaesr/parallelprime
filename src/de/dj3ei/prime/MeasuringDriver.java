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
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class MeasuringDriver implements Consumer<PrimeAlgorithm> {

    private int allPrimesHash;
    private boolean haveHash;
    
// With MAX = 300000000, on my laptop:
//    de.dj3ei.prime.NormalPrimeAlgorithm took 202.06 s.
//    de.dj3ei.prime.ParallelPrimeAlgorithm took 89.619 s.
//    de.dj3ei.prime.PrimitivePrimeAlgorithm took 473.522 s.
//    de.dj3ei.prime.NormalPrimeAlgorithm took 202.076 s.
//    de.dj3ei.prime.ParallelPrimeAlgorithm took 93.078 s.
//    de.dj3ei.prime.NormalPrimeAlgorithm took 159.026 s.
//    de.dj3ei.prime.ParallelPrimeAlgorithm took 84.053 s.
    
    private final static int MAX = 30000000;

    public static void main(String[] args) {
        MeasuringDriver md = new MeasuringDriver();

        @SuppressWarnings("unchecked")
        Class<? extends PrimeAlgorithm> algos[] = new Class[] {
                NormalPrimeAlgorithm.class, ParallelPrimeAlgorithm.class,
                PrimitivePrimeAlgorithm.class,
                NormalPrimeAlgorithm.class, ParallelPrimeAlgorithm.class,
                NormalPrimeAlgorithm.class, ParallelPrimeAlgorithm.class };
        Arrays.stream(algos).map(
                clazz -> {
                    try {
                        return clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        throw new RuntimeException("Could not instantiate " + clazz.getName(), ex);
                    }
                }).forEachOrdered(md);
    }

    private static class PrimeHasher implements IntConsumer {
        private volatile int hash;

        @Override
        public void accept(int value) {
            hash ^= value;
        }

        public int getHash() {
            return hash;
        }
    };

    @Override
    public void accept(PrimeAlgorithm alg) {
        PrimeHasher ph = new PrimeHasher();
        long start = System.currentTimeMillis();
        alg.findPrimes(MAX, ph);
        long done = System.currentTimeMillis();
        System.err.println(alg.getClass().getName() + " took " + 1e-3*(done - start) + " s.");
        synchronized (this) {
            if(haveHash) {
                if (allPrimesHash != ph.getHash()) {
                    throw new IllegalStateException("Prime algo "
                            + alg.getClass().getName()
                            + " produces hash value that different from all previous ones.");
                }
            } else {
                allPrimesHash = ph.getHash();
                haveHash = true;
            }
        }
    }
}
