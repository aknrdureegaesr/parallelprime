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

import java.util.function.IntConsumer;

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
