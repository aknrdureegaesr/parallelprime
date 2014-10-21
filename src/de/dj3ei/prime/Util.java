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

public class Util {

    static int intRoot(int a) {
        int x = a/2;
        int emergencyBrake = 40;
        while(! (x * x <= a && a < (x + 1) * (x + 1))) {
            x = (x + a/x)/2;
            if(--emergencyBrake <= 0)
                throw new IllegalStateException("Heron does not work for " + a);
        }
        return x;
    }

}
