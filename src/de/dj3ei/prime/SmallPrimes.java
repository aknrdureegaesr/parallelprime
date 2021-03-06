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

public abstract class SmallPrimes {
    
    // The int boundary overflows at tablePeriod when all primes up to including 29 are listed.
    // Runtime does not strongly depend on how many primes we list here for Normal, but does for Parallel.
    protected static final int SMALL_PRIMES[] = new int[] {2, 3, 5, 7, 11, 13};
    
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
