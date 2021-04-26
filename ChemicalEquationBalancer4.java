package com.saptakdas.chemistry.chemicalequation.version4;

import java.util.*;

/**
 * This OOP program can be used to solve almost any chemical equation using an improved full matrix/algebraic method.
 * @author Saptak Das
 */
public class ChemicalEquationBalancer4 {
    private static final Scanner sc=new Scanner(System.in);
    private final String backupReactantsString;
    private final String backupProductsString;
    private final Hashtable<Integer, Hashtable<String, Integer>> reactants;
    private final Hashtable<Integer, Hashtable<String, Integer>> products;
    private Matrix matrix;
    private Hashtable<Integer, LinkedList<Integer>> finalSolution;

    /**
     * Creation of a ChemicalEquationBalancer4 object that is solved after some basic validation.
     * @param args
     */
    public static void main(String[] args) {
        ChemicalEquationBalancer4 equation=new ChemicalEquationBalancer4();
        if(equation.matrix.matrix.length != (equation.matrix.matrix[0].length - 1)){
            System.out.println("Equation Matrix is of wrong size.");
            System.exit(0);
        }
        equation.solve();
    }

    /**
     * Driver Method of Solving Algorithm. Ties up use of all classes. First, matrix is solved using gaussian elimination algorithm. Then, reduced matrix is made into algebraic equations and solved for variables in terms of last variable(n-1 x n matrix). Last, the lcm of all the denominators are multiplied to give the simplified solution.
     */
    private void solve() {
        this.matrix.gaussianElimination();
        AlgebraEquation.setSize(this.matrix.matrix[0].length);
        for(int i=this.matrix.matrix.length-1; i>=0; i--) {
            //Iterating through reverse
            AlgebraEquation newAlgebraEquation = new AlgebraEquation(this.matrix.matrix[i]);
        }
        //Finding lcm of all answers. This is the final answers value.
        Integer lcm = 1;
        for(int i=0; i<AlgebraEquation.variables.length-1; i++)
            lcm = Fraction.lcm(lcm, AlgebraEquation.variables[i].denominator);
        //Multiplying lcm to all other answers.
        for(int i=0; i<AlgebraEquation.variables.length; i++) {
            if (AlgebraEquation.variables[i] == null) {
                AlgebraEquation.variables[AlgebraEquation.size - 1] = new Fraction(lcm, 1);
            }else {
                AlgebraEquation.variables[i] = Fraction.multiply(new Fraction(lcm, 1), AlgebraEquation.variables[i]);
            }
        }
        finalSolution.put(0, implementSubstitution(Arrays.copyOfRange(AlgebraEquation.variables, 0, reactants.size())));
        finalSolution.put(1, implementSubstitution(Arrays.copyOfRange(AlgebraEquation.variables, reactants.size(), AlgebraEquation.size)));
        //Final Formatting
        System.out.println("\nBalanced Equation: ");
        System.out.println(formatSolution(backupReactantsString, finalSolution.get(0))+" ---> "+formatSolution(backupProductsString, finalSolution.get(1)));
    }

    /**
     * Initializer for ChemicalEquationBalancer4 Class. Handles, delegates, and preprocesses all user input to be ready to solve.
     */
    public ChemicalEquationBalancer4(){
        System.out.print("Enter either reactant side or whole equation separated by '=': ");
        String firstInput=sc.nextLine();
        String originalProductsString;
        String originalReactantsString;
        if(firstInput.contains("=")){
            String[] arr =firstInput.split("=");
            originalReactantsString = removeUnnecessaryCharacters(arr[0].replace(" ",""));
            originalProductsString = removeUnnecessaryCharacters(arr[1].replace(" ",""));
        }else{
            System.out.print("Enter product side: ");
            String secondInput=sc.nextLine();
            originalReactantsString = removeUnnecessaryCharacters(firstInput.replace(" ",""));
            originalProductsString = removeUnnecessaryCharacters(secondInput.replace(" ",""));
        }
        this.backupReactantsString= originalReactantsString;
        this.backupProductsString= originalProductsString;
        //Process Input
        String[] replacementArr={originalReactantsString, originalProductsString};
        reactants=parseString(replacementArr[0]);
        products=parseString(replacementArr[1]);
        LinkedHashSet<String> reactantsElements=getElements(originalReactantsString);
        LinkedHashSet<String> productsElements=getElements(originalProductsString);
        if(reactantsElements.equals(productsElements)){
            //Making Matrix
            int[][] intMatrix = new int[reactantsElements.size()][reactants.size() + products.size()];
            int currentIndex = 0;
            for (String element: reactantsElements) {
                int currentRowIndex = 0;
                int[] intMatrixRow = new int[reactants.size() + products.size()];
                for(int i=0; i<reactants.size(); i++) {
                    Hashtable<String, Integer> results = reactants.get(i);
                    intMatrixRow[currentRowIndex] = (results.getOrDefault(element, 0));
                    currentRowIndex += 1;
                }
                for(int i=0; i<products.size(); i++) {
                    Hashtable<String, Integer> results = products.get(i);
                    intMatrixRow[currentRowIndex] = (-results.getOrDefault(element, 0));
                    currentRowIndex += 1;
                }
                intMatrix[currentIndex] = intMatrixRow;
                currentIndex += 1;
            }
            this.matrix = new Matrix(intMatrix);
            finalSolution=new Hashtable<>();

        }else {
            System.out.println("Error: Same elements need to be on both sides of the equation.");
            System.exit(0);
        }
    }

    /**
     * Converts Fractions into integers for final formatting for either reactant or product side.
     * @param arr Fraction Array of reactant or product coefficients
     * @return LinkedList of Integers
     */
    private static LinkedList<Integer> implementSubstitution(Fraction[] arr){
        LinkedList<Integer> finalCoefficients=new LinkedList<>();
        for (Fraction f: arr) {
            finalCoefficients.addLast(f.numerator);
        }
        return finalCoefficients;
    }

    /**
     * Gets all elements used on a side an equation
     * @param inputString String representation of chemical equation side
     * @return LinkedList of all elements on side
     */
    private static LinkedHashSet<String> getElements(String inputString){
        LinkedHashSet<String> elements=new LinkedHashSet<>();
        String elementString="";
        char character = 0;
        for (int i=0; i<inputString.length(); i++){
            character=inputString.charAt(i);
            if (Character.isLetter(character)){
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))){
                    if (!elementString.equals("")){
                        elements.add(elementString);
                        elementString="";
                    }
                }
                elementString = elementString.concat(Character.toString(character));
            }
            else if (Character.toString(character).equals("+")){
                elements.add(elementString);
                elementString="";
            }
        }
        if (!Character.toString(character).equals("")){
            elements.add(elementString);
        }
        return elements;
    }

    /**
     * Parses string to get Hashtable representation of a side of a chemical equation.
     * @param inputString String representation of a side of a chemical equation.
     * @return Hashtable representation of a chemical equation side
     */
    private static Hashtable<Integer, Hashtable<String, Integer>> parseString(String inputString){
        Hashtable<Integer, Hashtable<String, Integer>> compoundTable= new Hashtable<>();
        String storeString = "";
        Integer index=0;
        for (int j=0; j<inputString.length(); j++) {
            if (Character.toString(inputString.charAt(j)).equals("+")) {
                compoundTable.put(index, parseCompound(storeString));
                storeString="";
                index=index+1;

            }
            else {
                storeString=storeString.concat(Character.toString(inputString.charAt(j)));
            }
        }
        compoundTable.put(index, parseCompound(storeString));
        return compoundTable;
    }

    /**
     * Removes all characters that are not letters, numbers, parentheses(), and plus signs("+")
     * @param currentString Unfiltered string
     * @return Filtered String
     */
    private static String removeUnnecessaryCharacters(String currentString){
        return currentString.replaceAll("[^a-zA-Z0-9()+]", "");
    }

    /**
     * String formatting of solution
     * @param originalString String received from user.
     * @param solutions LinkedList of solutions
     * @return Formatted String
     */
    private static String formatSolution(String originalString, LinkedList<Integer> solutions){
        String[] arr=originalString.split("\\+");
        StringBuilder s= new StringBuilder();
        for (int i = 0; i < solutions.size(); i++) {
            s.append(solutions.get(i));
            s.append(arr[i]);
            if(i<solutions.size()-1)
                s.append(" + ");
        }
        return s.toString();
    }

    /**
     * Parses each compound to get Hashtable of elements and quantities in compound
     * @param inputString String representation of compound
     * @return Hashtable representation of compound
     */
    private static Hashtable<String, Integer> parseCompound(String inputString) {
        Hashtable<String, Integer> dictionary = new Hashtable<>();
        String symbol = "";
        String numString = "";
        StringBuilder paranthesesStoreString= new StringBuilder();
        boolean parenthesesOn=false;
        boolean parenthesesEnd=false;
        String parenthesesScaler = "";
        for (int i = 0; i < inputString.length(); i++) {
            char character = inputString.charAt(i);
            if (Character.isLetter(character)) {
                //Checks that this is a letter
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))){
                    if(!parenthesesOn && !parenthesesEnd) {
                        //This is uppercase
                        if (!symbol.equals("")) {
                            //Symbol is filled and needs to be dumped
                            if (!dictionary.containsKey(symbol)) {
                                try {
                                    dictionary.put(symbol, Integer.valueOf(numString));
                                } catch (NumberFormatException exception) {
                                    dictionary.put(symbol, 1);
                                }
                            } else {
                                try {
                                    dictionary.put(symbol, Integer.valueOf(numString) + dictionary.get(symbol));
                                } catch (NumberFormatException exception) {
                                    dictionary.put(symbol, 1 + dictionary.get(symbol));
                                }
                            }
                            symbol = "";
                            numString = "";
                        }
                        symbol = symbol.concat(String.valueOf(character));
                    }else if(parenthesesOn && !parenthesesEnd){
                        paranthesesStoreString.append(character);
                    }else if(parenthesesEnd){
                        Hashtable<String, Integer> parenthesesParse=parseCompound(paranthesesStoreString.toString());
                        if(parenthesesScaler.equals(""))
                            parenthesesScaler="1";
                        for(String key: parenthesesParse.keySet()){
                            if (!dictionary.containsKey(key)) {
                                dictionary.put(key, parenthesesParse.get(key)*Integer.valueOf(parenthesesScaler));
                            } else {
                                dictionary.put(key, parenthesesParse.get(key)*Integer.valueOf(parenthesesScaler) + dictionary.get(key));
                            }
                        }
                        paranthesesStoreString = new StringBuilder();
                        parenthesesEnd=false;
                        parenthesesScaler="";
                        symbol = symbol.concat(String.valueOf(character));
                    }
                }
                else{
                    if(!parenthesesOn)
                        symbol = symbol.concat(String.valueOf(character));
                    else
                        paranthesesStoreString.append(character);
                }
            } else if (Character.isDigit(character)) {
                //This is a number
                if(!parenthesesOn && !parenthesesEnd) {
                    numString = numString.concat(String.valueOf(character));
                }else if(parenthesesEnd && !parenthesesOn){
                    parenthesesScaler+=character;
                }else if(parenthesesOn && !parenthesesEnd){
                    paranthesesStoreString.append(character);
                }
            }else if(character=='('){
                //Start Statement
                if(!parenthesesEnd) {
                    parenthesesOn = true;
                    if (!dictionary.containsKey(symbol)) {
                        try {
                            dictionary.put(symbol, Integer.valueOf(numString));
                        } catch (NumberFormatException exception) {
                            dictionary.put(symbol, 1);
                        }
                    } else {
                        try {
                            dictionary.put(symbol, Integer.valueOf(numString) + dictionary.get(symbol));
                        } catch (NumberFormatException exception) {
                            dictionary.put(symbol, 1 + dictionary.get(symbol));
                        }
                    }
                    symbol = "";
                    numString = "";
                }else{
                    Hashtable<String, Integer> parenthesesParse=parseCompound(paranthesesStoreString.toString());
                    if(parenthesesScaler.equals(""))
                        parenthesesScaler="1";
                    for(String key: parenthesesParse.keySet()){
                        if (!dictionary.containsKey(key)) {
                            dictionary.put(key, parenthesesParse.get(key)*Integer.valueOf(parenthesesScaler));
                        } else {
                            dictionary.put(key, parenthesesParse.get(key)*Integer.valueOf(parenthesesScaler) + dictionary.get(key));
                        }
                    }
                    paranthesesStoreString = new StringBuilder();
                    parenthesesOn=true;
                    parenthesesEnd=false;
                    parenthesesScaler="";
                }
            }else if(character==')'){
                //End statement
                parenthesesEnd=true;
                parenthesesOn=false;
            }
        }
        if(!parenthesesEnd) {
            if (numString.equals("")) {
                numString = "1";
            }
            if (!dictionary.containsKey(symbol)) {
                dictionary.put(symbol, Integer.valueOf(numString));
            } else {
                dictionary.put(symbol, Integer.valueOf(numString) + dictionary.get(symbol));
            }
        }else{
            Hashtable<String, Integer> parenthesesParse=parseCompound(paranthesesStoreString.toString());
            if(parenthesesScaler.equals(""))
                parenthesesScaler="1";
            for(String key: parenthesesParse.keySet()){
                if (!dictionary.containsKey(key)) {
                    dictionary.put(key, parenthesesParse.get(key)*Integer.valueOf(parenthesesScaler));
                } else {
                    dictionary.put(key, parenthesesParse.get(key)*Integer.valueOf(parenthesesScaler) + dictionary.get(key));
                }
            }
        }
        return dictionary;
    }
}

/**
 * Custom Fraction class with all basic operations(adding, multiplying, negating, simplifying, scaling) performed during gaussian elimination. Fractions are made to be immutable in this class. Note all Fractions are simplified after any operation.
 */
class Fraction {
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

/**
 * Simple Matrix class with 2d array representation. Implements all primary matrix operations(row swapping, row addition, row multiplication). Also, includes implementation of a custom gaussian elimination algorithm. Note all terms inside of Matrix are represented as Fractions. Note that row indexes start from 1, NOT 0. Note that matrix is not augmented(other side is assumed to be all 0's).
 */
class Matrix {
    public Fraction[][] matrix;

    /**
     * Matrix Initializer that converts int array into fraction array
     * @param matrix 2d int array representation of matrix
     */
    public Matrix(int[][] matrix){
        this.matrix = new Fraction[matrix.length][matrix[0].length];
        for(int j=0; j<matrix.length; j++) {
            int[] row = matrix[j];
            Fraction[] fractionRow = new Fraction[row.length];
            for(int i=0; i<fractionRow.length; i++) {
                fractionRow[i] = new Fraction(row[i], 1);
            }
            this.matrix[j] = fractionRow;
        }
    }

    /**
     * Simple Function that swaps rows
     * @param row1 Row Index 1
     * @param row2 Row Index 2
     */
    public void rowSwap(int row1, int row2){
        Fraction[] temp = this.matrix[row1-1];
        this.matrix[row1-1] = this.matrix[row2-1];
        this.matrix[row2-1] = temp;
    }

    /**
     * Simple Function that adds 2 rows
     * @param row1 Row Index 1
     * @param row2 Row Index 2 (Result is stored in this row)
     */
    public void rowAddition(int row1, int row2){
        Fraction[] addedRow = new Fraction[this.matrix[row1-1].length];
        for(int i=0; i<this.matrix[row1-1].length; i++){
            addedRow[i] = Fraction.add(this.matrix[row1-1][i], this.matrix[row2-1][i]);
        }
        this.matrix[row2-1] = addedRow;
    }

    /**
     * Simple Function that multiplies 2 rows
     * @param scalar Fraction scalar
     * @param row Row Index
     */
    public void rowMultiplication(Fraction scalar, int row){
        for(int i=0; i<this.matrix[row-1].length; i++){
            Fraction f = this.matrix[row-1][i];
            this.matrix[row-1][i] = Fraction.multiply(f, scalar);
        }
    }

    /**
     * Custom Implementation of a Gaussian Elimination Algorithm. Note matrix does not need to be square.
     */
    public void gaussianElimination() {
        for(int column=0; column<this.matrix.length; column++){
            //Check if current column is 1.
            if(!this.matrix[column][column].equals(new Fraction(1, 1))){
                //Find any existing ones
                Integer oneIndex = null;
                Integer otherIndex = null;
                for(int i=column; i<this.matrix.length; i++) {
                    if(this.matrix[i][column].equals(new Fraction(1,1))){
                        if(oneIndex == null) {
                            oneIndex=i;
                        }
                    }else if(!this.matrix[i][column].equals(new Fraction(0,1))) {
                        if(otherIndex == null) {
                            otherIndex=i;
                        }
                    }
                }
                if(oneIndex != null) {
                    this.rowSwap(column + 1, oneIndex + 1);
                }else if(otherIndex != null) {
                    this.rowSwap(column + 1, otherIndex + 1);
                    this.rowMultiplication(new Fraction(this.matrix[column][column].denominator, this.matrix[column][column].numerator), column + 1);
                }else {
                    System.out.println(column);
                    System.out.println();
                    System.out.println("All Zeroes found!");
                    System.exit(0);
                }
            }
            //Substitute all zeroes below.
            for(int i=column+1; i<this.matrix.length; i++) {
                if(!this.matrix[i][column].equals(new Fraction(0, 1))) {
                    Fraction oldScalar = this.matrix[i][column];
                    this.rowMultiplication(Fraction.negate(oldScalar), column+1);
                    this.rowAddition(column+1, i+1);
                    this.rowMultiplication(Fraction.negate(new Fraction(oldScalar.denominator, oldScalar.numerator)), column+1);
                }
            }
        }
    }

    /**
     * @return String Representation of Matrix
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for(Fraction[] row: this.matrix) {
            string.append("[");
            for(int i=0; i<row.length; i++){
                string.append(row[i].toString());
                if(i<(row.length-1)){
                    string.append(", ");
                }
            }
            string.append("]\n");
        }
        return string.toString();
    }
}

/**
 * Simple Algebra class to solve equations from simplified matrix from gaussian elimination. Note that AlgebraEquation.setSize(Integer size) must be used before any AlgebraEquation instances can be made.
 */
class AlgebraEquation {
    public static Fraction[] variables;
    public static Integer size;
    public Fraction[] equation;

    /**
     * @param size Number of Variables in Matrix System
     */
    public static void setSize(Integer size){
        AlgebraEquation.size = size;
        AlgebraEquation.variables = new Fraction[size];
    }

    /**
     * AlgebraEquation Initializer in the form of a Fraction array. Equation solving is automatic. Answers to each variable are saved in AlgebraEquation.variables for future solving and final results.
     * @param equationSide Fraction Array of variable coefficients(matrix row). Note since matrix is not augmented, other side values are assumed to be 0.
     */
    public AlgebraEquation(Fraction[] equationSide) {
        this.equation = equationSide;
        this.solve();
    }

    /**
     * Simple solving algorithm to represent all variables in terms of last variable. Note this needs to substitute any unknown variables. As a result, AlgebraEquations need to be made from the bottom to top of matrix(given that solving method is like Reduced Row Echelon or Gaussian Elimination where the diagonal is from top left to bottom right).
     */
    public void solve() {
        //Making all other variables into final variable form.
        Integer firstIndex = null;
        for(int i=0; i<this.equation.length-1; i++) {
            if(!this.equation[i].equals(new Fraction(0, 1))) {
                //Fraction is non zero
                if (this.equation[i].equals(new Fraction(1, 1)) && firstIndex == null) {
                    //Fraction is nonzero, but on one diagonal. Doesn't need to be replaced.
                    firstIndex = i;
                } else {
                    //Needs to replaced. Check value in class for value.
                    Fraction variableReplacement = AlgebraEquation.variables[i];
                    this.equation[AlgebraEquation.size-1] = Fraction.add(this.equation[AlgebraEquation.size-1], Fraction.multiply(variableReplacement, this.equation[i]));
                }
            }
        }
        AlgebraEquation.variables[firstIndex] = Fraction.negate(this.equation[AlgebraEquation.size-1]);
    }
}