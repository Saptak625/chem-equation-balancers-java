package com.saptakdas.chemistry.chemicalequation.version3;

import java.util.*;

/**
 * This OOP program can be used to solve almost any chemical equation.
 * @author Saptak Das
 */
public class ChemicalEquationBalancer3 {
    private static Scanner sc=new Scanner(System.in);
    private String originalReactantsString;
    private String originalProductsString;
    private String backupReactantsString;
    private String backupProductsString;
    private Hashtable<Integer, Hashtable<String, Integer>> reactants;
    private Hashtable<Integer, Hashtable<String, Integer>> products;
    private LinkedHashSet<String> ELEMENTS;
    private LinkedHashSet<String> elements;
    private Algebra[] reactantCoefficients;
    private Algebra[] productCoefficients;
    private Hashtable<Integer, LinkedList<Integer>> finalSolution;

    public static void main(String[] args) {
        ChemicalEquationBalancer3 equation=new ChemicalEquationBalancer3();
        equation.solve();
    }

    /**
     * Constructor function initializes equation.
     */
    public ChemicalEquationBalancer3(){
        System.out.print("Enter either reactant side or whole equation separated by '=': ");
        String firstInput=sc.nextLine();
        if(firstInput.contains("=")){
            String[] arr =firstInput.split("=");
            this.originalReactantsString= removeUnnecessaryCharacters(arr[0].replace(" ",""));
            this.originalProductsString= removeUnnecessaryCharacters(arr[1].replace(" ",""));
        }else{
            System.out.print("Enter product side: ");
            String secondInput=sc.nextLine();
            this.originalReactantsString= removeUnnecessaryCharacters(firstInput.replace(" ",""));
            this.originalProductsString= removeUnnecessaryCharacters(secondInput.replace(" ",""));
        }
        this.backupReactantsString=originalReactantsString;
        this.backupProductsString=originalProductsString;
        //Process Input
        String[] replacementArr=this.polyatomicReplacement();
        reactants=parseString(replacementArr[0]);
        products=parseString(replacementArr[1]);
        LinkedHashSet<String> reactantsElements=getElements(originalReactantsString);
        LinkedHashSet<String> productsElements=getElements(originalProductsString);
        if(reactantsElements.equals(productsElements)){
            elements=reactantsElements;
            ELEMENTS=reactantsElements;
            reactantCoefficients=new Algebra[reactants.size()];
            productCoefficients=new Algebra[products.size()];
            finalSolution=new Hashtable<>();
        }else {
            System.out.println("Error: Same elements need to be on both sides of the equation.");
            System.exit(0);
        }
    }

    /**
     * Used to fill Algebra[]
     * @param arr Algebra[] to fill
     */
    private static void fillArrays(Algebra[] arr){
        for (int i = 0; i < arr.length; i++) {
            arr[i]=new Algebra();
        }
    }

    /**
     * Padding method.
     * @param arr Algebra[] to pad
     */
    private static void padAlgebra(Algebra[] arr){
        int size=0;
        for (Algebra a:arr) {
            size=a.size()>size?a.size():size;
        }
        for (Algebra a:arr) {
            for (int i = a.size(); i < size; i++) {
                a.addLast(new Fraction());
            }
        }
    }

    /**
     * Main Solving Algorithm
     */
    public void solve(){
        fillArrays(reactantCoefficients);
        fillArrays(productCoefficients);
        //Step 1
        LinkedList<String> elementsUsed=new LinkedList<>();
        int counter=1;
        for(String element: elements) {
            Hashtable<String, LinkedList<Integer>> info=this.getOccurrencesAndStates(element);
            if(info.get("rui").size()==1 && info.get("pui").size()==1 && info.get("rfi").size()==0 && info.get("pfi").size()==0){
                Fraction reactantRatio=reactants.get(info.get("rui").get(0)).get(element)>= products.get(info.get("pui").get(0)).get(element) ? new Fraction(1, 1) : new Fraction(products.get(info.get("pui").get(0)).get(element), reactants.get(info.get("rui").get(0)).get(element));
                Fraction productRatio=reactants.get(info.get("rui").get(0)).get(element)>=products.get(info.get("pui").get(0)).get(element)? new Fraction(reactants.get(info.get("rui").get(0)).get(element), products.get(info.get("pui").get(0)).get(element)): new Fraction(1,1);
                if (counter <= 2) {
                    //Create padAlgebra method to create padded method.
                    reactantCoefficients[info.get("rui").get(0)].addLast(reactantRatio);
                    reactantCoefficients[info.get("rui").get(0)].active=true;
                    productCoefficients[info.get("pui").get(0)].addLast(productRatio);
                    productCoefficients[info.get("pui").get(0)].active=true;
                    this.updateAlgebraLength(counter);
                    elementsUsed.addLast(element);
                }else{
                    System.out.println("This equation uses more than two variables.");
                    System.exit(0);
                }
            }
        }
        padAlgebra(reactantCoefficients);
        padAlgebra(productCoefficients);
        this.removeElements(elementsUsed);
        //Step 2
        elementsUsed.clear();
        int sizeOfElements=elements.size();
        for(int i=0; i<sizeOfElements; i++) {
            for (String element : elements) {
                if (checkIfFinished(reactantCoefficients) && checkIfFinished(productCoefficients))
                    break;
                Hashtable<String, LinkedList<Integer>> info = this.getOccurrencesAndStates(element);
                if ((info.get("rui").size() == 1 && info.get("pui").size() == 0 && info.get("rfi").size() == 0 && info.get("pfi").size() == 1) || (info.get("rui").size() == 0 && info.get("pui").size() == 1 && info.get("rfi").size() == 1 && info.get("pfi").size() == 0)) {
                    boolean productFilled = info.get("rui").size() == 1 && info.get("pfi").size() == 1;
                    Algebra ratio = productFilled ? Algebra.multiply(Algebra.multiply(productCoefficients[info.get("pfi").get(0)], new Fraction(products.get(info.get("pfi").get(0)).get(element), 1)), new Fraction(1, reactants.get(info.get("rui").get(0)).get(element))) : Algebra.multiply(Algebra.multiply(reactantCoefficients[info.get("rfi").get(0)], new Fraction(reactants.get(info.get("rfi").get(0)).get(element), 1)), new Fraction(1, products.get(info.get("pui").get(0)).get(element)));
                    if (productFilled) {
                        reactantCoefficients[info.get("rui").get(0)]=ratio;
                        reactantCoefficients[info.get("rui").get(0)].active = true;
                    } else {
                        productCoefficients[info.get("pui").get(0)]=ratio;
                        productCoefficients[info.get("pui").get(0)].active = true;
                    }
                    elementsUsed.addLast(element);
                    this.removeElements(elementsUsed);
                    elementsUsed.clear();
                    break;
                }
            }
        }
        //Step 3
        elementsUsed.clear();
        sizeOfElements=elements.size();
        for(int j=0; j<sizeOfElements; j++) {
            for (String element : elements) {
                if (checkIfFinished(reactantCoefficients) && checkIfFinished(productCoefficients))
                    break;
                Hashtable<String, LinkedList<Integer>> info = this.getOccurrencesAndStates(element);
                if ((info.get("rui").size() == 1 && info.get("pui").size() == 0) || (info.get("rui").size() == 0 && info.get("pui").size() == 1)) {
                    boolean productFilled = info.get("rui").size() == 1 && info.get("pui").size() == 0;
                    Algebra reactantFilledQuantity=new Algebra();
                    Algebra productFilledQuantity=new Algebra();
                    fillAlgebra(reactantFilledQuantity, reactantCoefficients[0].size());
                    fillAlgebra(productFilledQuantity, reactantCoefficients[0].size());
                    for (int i: info.get("rfi"))
                        reactantFilledQuantity=Algebra.add(Algebra.multiply(reactantCoefficients[i], new Fraction(reactants.get(i).get(element),1)), reactantFilledQuantity);
                    for (int i: info.get("pfi"))
                        productFilledQuantity=Algebra.add(Algebra.multiply(productCoefficients[i], new Fraction(products.get(i).get(element),1)), productFilledQuantity);
                    Algebra ratio = productFilled ? Algebra.multiply(Algebra.add(productFilledQuantity, Algebra.negate(reactantFilledQuantity)), new Fraction(1, reactants.get(info.get("rui").get(0)).get(element))) : Algebra.multiply(Algebra.add(reactantFilledQuantity, Algebra.negate(productFilledQuantity)), new Fraction(1, products.get(info.get("pui").get(0)).get(element)));
                    if (productFilled) {
                        reactantCoefficients[info.get("rui").get(0)]=ratio;
                        reactantCoefficients[info.get("rui").get(0)].active = true;
                    } else {
                        productCoefficients[info.get("pui").get(0)]=ratio;
                        productCoefficients[info.get("pui").get(0)].active = true;
                    }
                    elementsUsed.addLast(element);
                    this.removeElements(elementsUsed);
                    elementsUsed.clear();
                    break;
                }
            }
        }
        //Solving final equation
        if(reactantCoefficients[0].size()==1){
            int lcd=1;
            for (Algebra a: reactantCoefficients) {
                lcd=Fraction.lcm(((Fraction) a.get(0)).getDenominator(), lcd);
            }
            for (Algebra a: productCoefficients) {
                lcd=Fraction.lcm(((Fraction) a.get(0)).getDenominator(), lcd);
            }
            Algebra.setVarValues(new int[]{lcd});
        }else{
            String currentElement=(String) elements.toArray()[0];
            Algebra sumOfReactants=new Algebra();
            fillAlgebra(sumOfReactants, reactantCoefficients[0].size());
            for (int i: this.getOccurrencesAndStates(currentElement).get("rfi")) {
                sumOfReactants=Algebra.add(sumOfReactants, Algebra.multiply(reactantCoefficients[i], new Fraction(reactants.get(i).get(currentElement), 1)));
            }
            Algebra sumOfProducts=new Algebra();
            fillAlgebra(sumOfProducts, productCoefficients[0].size());
            for (int i: this.getOccurrencesAndStates(currentElement).get("pfi")) {
                sumOfProducts=Algebra.add(sumOfProducts, Algebra.multiply(productCoefficients[i], new Fraction(products.get(i).get(currentElement),1)));
            }
            Algebra finalSummation=Algebra.add(sumOfReactants, Algebra.negate(sumOfProducts));
            Fraction sumOfA=((Fraction) finalSummation.get(0)).getNumerator()>0?(Fraction) finalSummation.get(0): Fraction.negate((Fraction) finalSummation.get(0));
            Fraction sumOfB=((Fraction) finalSummation.get(0)).getNumerator()>0?Fraction.negate((Fraction) finalSummation.get(1)): (Fraction) finalSummation.get(1);
            //Factoring
            int gcf=Fraction.gcd(sumOfA.getNumerator(), sumOfB.getNumerator());
            int lcd= Fraction.lcm(sumOfA.getDenominator(), sumOfB.getDenominator());
            Algebra.setVarValues(new int[] {Fraction.multiply(sumOfB, new Fraction(lcd, gcf)).getNumerator(), Fraction.multiply(sumOfA, new Fraction(lcd, gcf)).getNumerator()});
            //Final Check
            int finalLcd=checkSolution(reactantCoefficients);
            finalLcd=Fraction.lcm(finalLcd, checkSolution(productCoefficients));
            Algebra.setVarValues(new int[] {Algebra.getVarValues()[0]*finalLcd, Algebra.getVarValues()[1]*finalLcd});
        }
        finalSolution.put(0, implementSubstitution(reactantCoefficients));
        finalSolution.put(1, implementSubstitution(productCoefficients));
        //Final Formatting
        System.out.println("\nBalanced Equation: ");
        System.out.println(formatSolution(backupReactantsString, finalSolution.get(0))+" ---> "+formatSolution(backupProductsString, finalSolution.get(1)));
    }

    /**
     * Checks solution and returns scalar to fix
     * @param arr Algebra[]
     * @return scalar value to fix
     */
    private static int checkSolution(Algebra[] arr){
        int lcd=1;
        for(Algebra a: arr){
            lcd=Fraction.lcm(a.substituteVariables().getDenominator(), lcd);
        }
        return lcd;
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
     * Substitutes class variables into integer coefficients
     * @param arr Algebra[]
     * @return LinkedList of integer coefficients
     */
    private static LinkedList<Integer> implementSubstitution(Algebra[] arr){
        LinkedList<Integer> finalCoefficients=new LinkedList<>();
        for (Algebra a: arr) {
            finalCoefficients.addLast(a.substituteVariables().getNumerator());
        }
        return finalCoefficients;
    }

    /**
     * @param a Algebra expression
     * @param length size to pad to
     */
    private static void fillAlgebra(Algebra a, int length){
        for (int i = 0; i < length; i++) {
            a.addLast(new Fraction());
        }
    }

    /**
     * Checks if solution is correct
     * @param arr Algebra[] to check
     * @return boolean value if solution contains no Fractional coefficients
     */
    private static boolean checkIfFinished(Algebra[] arr){
        for (Algebra a: arr) {
            if(!a.active)
                return false;
        }
        return true;
    }

    /**
     * Removes elements
     * @param elementsUsed LinkedList of elements used
     */
    private void removeElements(LinkedList<String> elementsUsed){
        for(String e: elementsUsed){
            elements.remove(e);
        }
    }

    /**
     * Update length of Algebra list
     * @param length Length to make Algebra expression
     */
    private void updateAlgebraLength(int length){
        Algebra[][] arr={reactantCoefficients, productCoefficients};
        for(Algebra[] arrayField: arr) {
            for (Algebra a: arrayField){
                for(int i=a.size(); i<length; i++)
                    a.addLast(new Fraction());
            }
        }
    }

    /**
     * Gets Hashtable representation of states and occurrences for given element
     * @param currentElement Element to find occurrences for
     * @return Hashtable of LinkedList with occurrences of currentElement in different states
     */
    private Hashtable<String, LinkedList<Integer>> getOccurrencesAndStates(String currentElement){
        LinkedList<Integer> reactantUnfilledIndex=new LinkedList<>();
        LinkedList<Integer> productUnfilledIndex=new LinkedList<>();
        LinkedList<Integer> reactantFilledIndex=new LinkedList<>();
        LinkedList<Integer> productFilledIndex=new LinkedList<>();
        for (int j = 0; j < reactants.size(); j++) {
            Hashtable<String, Integer> compoundTable = reactants.get(j);
            if (compoundTable.containsKey(currentElement)) {
                if(reactantCoefficients[j].active)
                    reactantFilledIndex.addLast(j);
                else
                    reactantUnfilledIndex.addLast(j);
            }
        }
        for (int j = 0; j < products.size(); j++) {
            Hashtable<String, Integer> compoundTable = products.get(j);
            if (compoundTable.containsKey(currentElement)) {
                if(productCoefficients[j].active)
                    productFilledIndex.addLast(j);
                else
                    productUnfilledIndex.addLast(j);
            }
        }
        //Packaging
        Hashtable<String, LinkedList<Integer>> h=new Hashtable<>();
        h.put("rui" , reactantUnfilledIndex);
        h.put("rfi", reactantFilledIndex);
        h.put("pui", productUnfilledIndex);
        h.put("pfi", productFilledIndex);
        return h;
    }

    /**
     * Resets all variables for solving again if needed
     */
    public void reset(){
        elements=ELEMENTS;
        reactantCoefficients=new Algebra[reactants.size()];
        productCoefficients=new Algebra[products.size()];
        finalSolution=new Hashtable<>();
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
     * Replaces common polyatomic ions with their own custom element.
     * @return String[] Packaged version of new reactant and product strings
     */
    private String[] polyatomicReplacement(){
        String[] replacementNames={"A", "D", "E", "G", "J", "L", "M", "Q", "R", "X"};
        String[] commonIons={"NH4", "C2H3O2", "HCO3","HSO4", "ClO", "ClO3", "ClO2", "OCN", "CN", "H2PO4", "OH", "NO3", "NO2", "ClO4", "MnO4", "SCN", "CO3", "CrO4", "Cr2O7", "HPO4", "SO4", "SO3", "S2O3", "BO3", "PO4"};
        int index=0;
        for(String ion: commonIons){
            if(originalReactantsString.contains(ion) && originalProductsString.contains(ion)){
                originalReactantsString=originalReactantsString.replace(ion, replacementNames[index]);
                originalProductsString=originalProductsString.replace(ion, replacementNames[index]);
                index++;
            }
        }
        return new String[] {originalReactantsString, originalProductsString};
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
                        if (!elements.contains(elementString)){
                            elements.add(elementString);
                        }
                        elementString="";
                    }
                }
                elementString = elementString.concat(Character.toString(character));
            }
            else if (Character.toString(character).equals("+")){
                if (!elements.contains(elementString)){
                    elements.add(elementString);
                }
                elementString="";
            }
        }
        if (!Character.toString(character).equals("")){
            if (!elements.contains(elementString)) {
                elements.add(elementString);
            }
        }
        return elements;
    }
}

/**
 * This class represents algebraic expressions.
 * @author Saptak Das
 */
class Algebra extends LinkedList{
    private static final String[] alphabet={"a", "b", "c", "d", "e"};
    private static int[] varValues;
    boolean active=false;

    public String toString(){
        StringBuilder s=new StringBuilder();
        for (int i = 0; i <this.size(); i++) {
            s.append(this.get(i));
            s.append(alphabet[i]);
            if(i<this.size()-1)
                s.append("+");
        }
        if(s.length()==0)
            return "Unknown";
        return s.toString();
    }

    /**
     * Addition of Algebra objects
     * @param exp1 First Algebra Expression
     * @param exp2 Second Algebra Expression
     * @return Addition of exp1 and exp2
     */
    public static Algebra add(Algebra exp1, Algebra exp2){
        Algebra a=new Algebra();
        for (int i = 0; i < exp1.size(); i++) {
            a.addLast(Fraction.add((Fraction) exp1.get(i), (Fraction) exp2.get(i)));
        }
        return a;
    }

    /**
     * Negation of Algebra object
     * @param exp1 Algebra expression
     * @return Negation of exp1
     */
    public static Algebra negate(Algebra exp1){
        Algebra a=new Algebra();
        for (Object f : exp1) {
            a.addLast(Fraction.negate((Fraction) f));
        }
        return a;
    }

    /**
     * Product of Algebra object with scalar Fraction
     * @param exp1 Algebra Expression
     * @param f Scalar Fraction Value
     * @return Product of exp1 and f
     */
    public static Algebra multiply(Algebra exp1, Fraction f){
        Algebra a=new Algebra();
        for (Object b : exp1) {
            a.addLast(Fraction.multiply((Fraction) b, f));
        }
        return a;
    }

    /**
     * Sets variable values after Algebra expression is solved.
     * @param values Integer array of variable values
     */
    public static void setVarValues(int[] values){
        varValues=values;
    }

    /**
     * @return Get variable values for expression
     */
    public static int[] getVarValues(){
        return varValues;
    }

    /**
     * Substitutes values in varValues into Algebra expression
     * @return Fraction value after substitution
     */
    public Fraction substituteVariables(){
        Fraction sum=new Fraction();
        for (int i = 0; i < this.size(); i++) {
            sum=Fraction.add(Fraction.multiply( (Fraction) this.get(i), new Fraction(varValues[i], 1)), sum);
        }
        return sum;
    }
}

/**
 * This class represents basic fractions.
 * @author Saptak Das
 */
class Fraction{
    private int numerator;
    private int denominator;

    Fraction() {
        this.numerator=0;
        this.denominator=1;
    }

    Fraction(int numerator, int denominator){
        int gcf=gcd(numerator, denominator);
        this.numerator=numerator/gcf;
        this.denominator=denominator/gcf;
    }

    /**
     * Used to get GCF of two numbers
     * @param a first number
     * @param b second number
     * @return GCF of a and b
     */
    static int gcd(int a, int b) {
        if (b==0) return a;
        return gcd(b,a%b);
    }

    /**
     * Used to get LCM of two numbers
     * @param a first number
     * @param b second number
     * @return LCM of a and b
     */
    static int lcm(int a, int b) {
        return (a*b)/gcd(a, b);
    }

    int getNumerator() {
        return numerator;
    }

    int getDenominator() {
        return denominator;
    }

    @Override
    public String toString() {
        return numerator+"/"+denominator;
    }

    /**
     * @param firstFraction First Fraction
     * @param secondFraction Second Fraction
     * @return Product of fractions
     */
    static Fraction multiply(Fraction firstFraction, Fraction secondFraction){
        return new Fraction(firstFraction.getNumerator()*secondFraction.getNumerator(), firstFraction.getDenominator()*secondFraction.getDenominator());
    }

    /**
     * @param firstFraction First Fraction
     * @param secondFraction Second Fraction
     * @return Addition of fractions
     */
    static Fraction add(Fraction firstFraction, Fraction secondFraction){
        if(firstFraction.getNumerator()!=0 || secondFraction.getNumerator()!=0) {
            int additionLCM = lcm(firstFraction.getDenominator(), secondFraction.getDenominator());
            int scaledFirstNumerator=firstFraction.getNumerator()*additionLCM/firstFraction.getDenominator();
            int scaledSecondNumerator=secondFraction.getNumerator()*additionLCM/secondFraction.getDenominator();
            return new Fraction(scaledFirstNumerator+scaledSecondNumerator, additionLCM);
        }else if(!(firstFraction.getNumerator() == 0)){
            return new Fraction(secondFraction.getNumerator(), secondFraction.getDenominator());
        }else{
            return new Fraction(firstFraction.getNumerator(), firstFraction.getDenominator());
        }
    }

    /**
     * @param fraction Fraction to be negated
     * @return Negated value of fraction
     */
    static Fraction negate(Fraction fraction){
        return new Fraction(-fraction.getNumerator(), fraction.getDenominator());
    }
}
