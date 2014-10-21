# Prime number generation with Java 8

This is a little experiment: A program that generates prime numbers, using the parallelism features and other nice things available with Java 8.

## Misused as a benchmark

On my otherwise reasonably quiet "Sandy Bridge" laptop, when increasing the variable `MAX` in `MeasuringDriver` to 300000000, I got the following times:

de.dj3ei.prime.NormalPrimeAlgorithm took 202.06 s.    
de.dj3ei.prime.ParallelPrimeAlgorithm took 89.619 s.    
de.dj3ei.prime.PrimitivePrimeAlgorithm took 473.522 s.    
de.dj3ei.prime.NormalPrimeAlgorithm took 202.076 s.    
de.dj3ei.prime.ParallelPrimeAlgorithm took 93.078 s.    
de.dj3ei.prime.NormalPrimeAlgorithm took 159.026 s.    
de.dj3ei.prime.ParallelPrimeAlgorithm took 84.053 s.    

## Setup

I have used this as a project under Eclipse.  But you can probably get away with a few commands at your trusted shell prompt, like

    mkdir -p target
    javac -d target src/de/dj3ei/prime/*.java
    java -cp target de.dj3ei.prime.MeasuringDriver

## Why "dj3ei"?

I'm a [ham radio amateur](http://en.wikipedia.org/wiki/Amateur_radio) and DJ3EI is my official [government-assigned](http://ans.bundesnetzagentur.de/Amateurfunk/Rufzeichen.aspx) call sign.
