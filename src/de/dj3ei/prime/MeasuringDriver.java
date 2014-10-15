package de.dj3ei.prime;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class MeasuringDriver implements Consumer<PrimeAlgorithm> {

    private int allPrimesHash;
    private boolean haveHash;
    
    private final static int MAX = 30000000;

    public static void main(String[] args) {
        MeasuringDriver md = new MeasuringDriver();

        @SuppressWarnings("unchecked")
        Class<? extends PrimeAlgorithm> algos[] = new Class[]{PrimitivePrimeAlgorithm.class, NormalPrimeAlgorithm.class};
        Arrays.asList(algos).stream().map(
                clazz -> {
                    try {
                        return clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        throw new RuntimeException("Could not instantiate " + clazz.getName(), ex);
                    }
                }).forEachOrdered(md);
    }

    private static class PrimeHasher implements IntConsumer {
        private int hash;

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
