package de.dj3ei.prime.simple;

import java.util.function.IntConsumer;

import de.dj3ei.prime.PrimeAlgorithm;

public class PrimitivePrimeAlgorithm implements PrimeAlgorithm {

    @Override
    public void findPrimes(int max, IntConsumer primesEater) {
        if(2 <= max)
            primesEater.accept(2);
        for(int primeCandidate = 3; primeCandidate <= max; primeCandidate += 2) {
            boolean maybePrime = true;
            for(int divCandidate = 3; maybePrime && divCandidate * divCandidate <= primeCandidate; divCandidate += 2) {
                maybePrime = 0 != primeCandidate % divCandidate;
            }
            if(maybePrime)
                primesEater.accept(primeCandidate);
        }
    }
}
