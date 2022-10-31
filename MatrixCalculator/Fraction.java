package com.saptakdas.misc.MatrixCalculator;

/**
 * Custom Fraction class with all basic operations(adding, multiplying, negating, simplifying, scaling) performed during gaussian elimination. Fractions are made to be immutable in this class. Note all Fractions are simplified after any operation.
 */
public class Fraction {
    public int numerator;
    public int denominator;

    /**
     * Basic Initialization of a Fraction
     * @param numerator Numerator of Fraction
     * @param denominator Denominator of Fraction
     */
    public Fraction(int numerator, int denominator){
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Scaling of a Fraction that can be used to make a common denominator before adding
     * @param newDenominator New Denominator to scale to
     * @return New Scaled Fraction
     */
    public Fraction scaleDenominator(Integer newDenominator) {
        numerator *= Double.valueOf(newDenominator)/ (double) this.denominator;
        denominator = newDenominator;
        return new Fraction(numerator, denominator);
    }

    /**
     * Simple Function that adds two Fractions
     * @param f1 Fraction 1
     * @param f2 Fraction 2
     * @return Fraction Sum
     */
    public static Fraction add(Fraction f1, Fraction f2) {
        //Find LCM of denominator
        int lcm = Fraction.lcm(f1.denominator, f2.denominator);
        Fraction scaledF1 = f1.scaleDenominator(lcm);
        Fraction scaledF2 = f2.scaleDenominator(lcm);
        return Fraction.simplify(new Fraction(scaledF1.numerator + scaledF2.numerator, lcm));
    }

    /**
     * Simple function that negates a Fraction
     * @param f Fraction to be negated
     * @return Negated Fraction
     */
    public static Fraction negate(Fraction f) {
        return new Fraction(-f.numerator, f.denominator);
    }

    /**
     * @param f1 Fraction 1 (For a simple integer scaling, create a new Fraction with denominator of 1. In other words, represent all scalars as Fractions.)
     * @param f2 Fraction 2
     * @return Product of Fractions
     */
    public static Fraction multiply(Fraction f1, Fraction f2){
        int numerator = f1.numerator * f2.numerator;
        int denominator = f1.denominator * f2.denominator;
        return Fraction.simplify(new Fraction(numerator, denominator));
    }

    /**
     * Simple Function to simplify and standardize(0/5 -> 0/1) Fractions
     * @param f Fraction to be simplified
     * @return Simplified Fraction
     */
    public static Fraction simplify(Fraction f){
        if(f.denominator < 0){
            f.numerator *= -1;
            f.denominator *= -1;
        }
        if(f.numerator == 0) {
            return new Fraction(0, 1);
        }else{
            int gcd = Fraction.gcd(f.numerator, f.denominator);
            return new Fraction(f.numerator/gcd, f.denominator/gcd);
        }
    }

    /**
     * Simple Euclidean GCD algorithm
     * @param number1 Number 1
     * @param number2 Number 2
     * @return GCD of numbers
     */
    private static int gcd(int number1, int number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            int absNumber1 = Math.abs(number1);
            int absNumber2 = Math.abs(number2);
            int biggerValue = Math.max(absNumber1, absNumber2);
            int smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    /**
     * Comparison of 2 Fractions
     * @param f Fraction to be compared
     * @return Boolean of Equality of 2 Fractions
     */
    public boolean equals(Fraction f) {
        return f.numerator == this.numerator && f.denominator == this.denominator;
    }

    /**
     * Simple LCM algorithm based on Euclidean GCD algorithm
     * @param number1 Number 1
     * @param number2 Number 2
     * @return LCM of numbers
     */
    public static int lcm(int number1, int number2) {
        return (Math.abs(number1 * number2) / Fraction.gcd(number1, number2));
    }

    /**
     * @return String Representation of a Fraction
     */
    @Override
    public String toString() {
        return this.numerator +"/"+this.denominator;
    }
}
