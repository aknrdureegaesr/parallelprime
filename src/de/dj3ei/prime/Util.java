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
