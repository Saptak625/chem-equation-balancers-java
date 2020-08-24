package com.saptakdas.chemistry.chemicalequation.version1;

import java.util.*;

/**
 * This class can be used to solve simple chemical equations.
 * @author Saptak Das
 */
public class Chemical_Equation_Balancer {
    public static void main(String[] args) {
        runSimple();
    }

    /**
     * Parses string to get Hashtable representation of a side of a chemical equation.
     * @param inputString String representation of a side of a chemical equation.
     * @return Hashtable representation of a chemical equation side
     */
    static Hashtable<Integer, Hashtable<String, Integer>> parseString(String inputString){
        Hashtable<Integer, Hashtable<String, Integer>> compoundTable=new Hashtable<>();
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
     * Run method for simple inspection method.
     */
    public static void runSimple() {
        String reactants_String = input("Enter Reactants Side: ");
        String products_String = input("Enter Products Side: ");
        Hashtable<Integer, Hashtable<String, Integer>> reactants = parseString(reactants_String);
        Hashtable<Integer, Hashtable<String, Integer>> products = parseString(products_String);
        //Checking if same elements are in lists
        LinkedList reactantsElements = getElements(reactants_String);
        Collections.sort(reactantsElements);
        LinkedList productsElements = getElements(products_String);
        Collections.sort(productsElements);
        LinkedList elements;
        boolean contain;
        for (Object reactantsElement : reactantsElements) {
            contain = productsElements.contains(reactantsElement);
            if (!contain) {
                System.out.println("Error: Same elements need to be on both sides of the equation.");
                System.exit(0);
            }
        }
        for (Object productsElement : productsElements) {
            contain = reactantsElements.contains(productsElement);
            if (!contain) {
                System.out.println("Error: Same elements need to be on both sides of the equation.");
                System.exit(0);
            }
        }
        elements=reactantsElements;
        //Simple Guessing and Fix Model
        solveSimple(reactants,products, elements, reactants_String, products_String);
    }

    /**
     * Inspection Solving Algorithm
     * @param reactants Hashtable representation of reactants
     * @param products Hashtable representation of products
     * @param elements LinkedList representation of elements
     * @param reactantsString String representation of reactants
     * @param productString String representation of products
     */
    private static void solveSimple(Hashtable<Integer, Hashtable<String, Integer>> reactants, Hashtable<Integer, Hashtable<String, Integer>> products, LinkedList elements, String reactantsString, String productString){
        //Creating two coefficient lists
        LinkedList<Double> reactantCoefficient= new LinkedList<>();
        LinkedList<Double> productCoefficient= new LinkedList<>();
        for (int i = 0; i < reactants.size(); i++){
            reactantCoefficient.add(null);
        }
        for (int i = 0; i < products.size(); i++){
            productCoefficient.add(null);
        }
        //Checking for one to one relationship
        Integer reactantsCounter;
        Integer productsCounter;
        int whileCounter=0;
        Integer reactantIndex=null;
        Integer productIndex=null;
        Integer reactantSubscript=null;
        Integer productSubscript=null;
        while (whileCounter<1) {
            for (int j = 0; j < elements.size(); j++) {
                String element = (String) elements.get(j);
                reactantsCounter = 0;
                productsCounter = 0;
                reactantIndex = null;
                productIndex = null;
                for (int i = 0; i < reactants.size(); i++) {
                    Hashtable<String, Integer> compound = reactants.get(i);
                    if (compound.containsKey(element) && reactantCoefficient.get(i) == null) {
                        reactantsCounter += 1;
                        reactantIndex = i;
                        reactantSubscript = compound.get(element);
                    }
                }
                for (int i = 0; i < products.size(); i++) {
                    Hashtable<String, Integer> compound = products.get(i);
                    if (compound.containsKey(element) && productCoefficient.get(i) == null) {
                        productsCounter += 1;
                        productIndex = i;
                        productSubscript = compound.get(element);
                    }
                }
                if (reactantsCounter == 1 && productsCounter == 1) {
                    if (productSubscript.equals(reactantSubscript)) {
                        reactantCoefficient.set(reactantIndex, (double) 1);
                        productCoefficient.set(productIndex, (double) 1);
                    } else if (productSubscript < reactantSubscript) {
                        productCoefficient.set(productIndex, (double) (reactantSubscript / productSubscript));
                        reactantCoefficient.set(reactantIndex, (double) 1);
                    } else {
                        productCoefficient.set(productIndex, (double) 1);
                        reactantCoefficient.set(reactantIndex, (double) (productSubscript / reactantSubscript));
                    }
                    elements.remove(j);
                    whileCounter=1;
                    break;
                }
            }
        }

        //Step 2: Find all Filled to Not Filled Relationships
        boolean reactantFilled;
        boolean productFilled;
        if (reactantCoefficient.contains(null) || productCoefficient.contains(null)) {
            for (int k = 0; k < elements.size(); k++) {
                for (int i = 0; i < elements.size(); i++) {
                    reactantsCounter = 0;
                    productsCounter = 0;
                    String element = (String) elements.get(i);
                    reactantFilled=false;
                    productFilled=false;
                    for (int j = 0; j < reactants.size(); j++) {
                        Hashtable<String, Integer> compound = reactants.get(j);
                        if (compound.containsKey(element)) {
                            reactantsCounter += 1;
                            reactantIndex = j;
                            reactantSubscript = compound.get(element);
                            if (reactantCoefficient.get(j) != null) {
                                reactantFilled = true;
                            }
                        }
                    }
                    for (int j = 0; j < products.size(); j++) {
                        Hashtable<String, Integer> compound = products.get(j);
                        if (compound.containsKey(element)) {
                            productsCounter += 1;
                            productIndex = j;
                            productSubscript = compound.get(element);
                            if (productCoefficient.get(j) != null) {
                                productFilled = true;
                            }
                        }
                    }
                    if (reactantsCounter == 1 && productsCounter == 1 && (reactantCoefficient.contains(null) || productCoefficient.contains(null))) {
                        if ((reactantFilled && !productFilled) || (!reactantFilled && productFilled)) {
                            if (reactantFilled) {
                                //get rCoeff
                                Double rCoeff = reactantCoefficient.get(reactantIndex);
                                productCoefficient.set(productIndex, ((rCoeff * reactantSubscript) / productSubscript));
                            } else if (productFilled) {
                                Double pCoeff = productCoefficient.get(productIndex);
                                reactantCoefficient.set(reactantIndex, ((pCoeff * productSubscript) / reactantSubscript));
                            }
                            elements.remove(i);
                        }
                    }
                }
            }
        }

        //Step 3: Complex Relationships
        LinkedList<Integer> filledReactantIndexes;
        LinkedList<Integer> filledProductIndexes;
        Integer unfilledIndex;
        boolean productUnfilled;
        if (reactantCoefficient.contains(null) || productCoefficient.contains(null)){
            for (int a = 0; a< elements.size()+1; a++) {
                for (int i = 0; i < elements.size(); i++) {
                    String element = (String) elements.get(i);
                    filledReactantIndexes = new LinkedList<>();
                    unfilledIndex = null;
                    reactantsCounter=0;
                    productsCounter=0;
                    productUnfilled=false;
                    filledProductIndexes= new LinkedList<>();
                    for (int j = 0; j < reactants.size(); j++) {
                        Hashtable<String, Integer> compound= reactants.get(j);
                        if (compound.containsKey(element)){
                            reactantsCounter+=1;
                            if (reactantCoefficient.get(j)!=null){
                                filledReactantIndexes.addLast(j);
                            }
                            else{
                                unfilledIndex=j;
                            }
                        }
                    }
                    for (int j = 0; j < products.size(); j++) {
                        Hashtable<String, Integer> compound= products.get(j);
                        if (compound.containsKey(element)){
                            productsCounter+=1;
                            if (productCoefficient.get(j)!=null){
                                filledProductIndexes.addLast(j);
                            }
                            else{
                                unfilledIndex=j;
                                productUnfilled=true;
                            }
                        }
                    }
                    if (((reactantsCounter+productsCounter-1)==(filledReactantIndexes.size()+filledProductIndexes.size())) && (reactantCoefficient.contains(null) || productCoefficient.contains(null))){
                        //This shows that n-1 of the instances are filled
                        Double reactantSum=(double) 0;
                        Integer index;
                        Integer subscript;
                        Hashtable<String, Integer> compound;
                        for (Integer filledReactantIndex : filledReactantIndexes) {
                            index = filledReactantIndex;
                            compound = reactants.get(index);
                            subscript = compound.get(element);
                            reactantSum = reactantSum + (subscript * reactantCoefficient.get(index));
                        }
                        Double productSum=(double) 0;
                        for (Integer filledProductIndex : filledProductIndexes) {
                            index = filledProductIndex;
                            compound = products.get(index);
                            subscript = compound.get(element);
                            productSum = productSum + (subscript * productCoefficient.get(index));
                        }
                        if (!productUnfilled){
                            //This means that unfilled value is in the reactant side
                            subscript= (reactants.get(unfilledIndex)).get(element);
                            reactantCoefficient.set(unfilledIndex,(productSum-reactantSum)/subscript);
                        }
                        else{
                            subscript= (products.get(unfilledIndex)).get(element);
                            productCoefficient.set(unfilledIndex, (reactantSum-productSum)/subscript);
                        }
                        elements.remove(i);
                    }
                }
            }
        }
        if (reactantCoefficient.contains(null) || productCoefficient.contains(null)) {
            System.out.println("This equation cannot be solved by simple solve because there is more than two variables needed to solve.");
            System.exit(0);
        }else{
            //Scale Values
            int i;
            for (i = 1; i < 1000; i++) {
                boolean check=true;
                LinkedList<Double> checkReactantCoefficient= new LinkedList<>(reactantCoefficient);
                LinkedList<Double> checkProductCoefficient= new LinkedList<>(productCoefficient);
                for(Double d: checkReactantCoefficient){
                    if(!((d*i)%1 == 0))
                        check=false;
                }
                for(Double d: checkProductCoefficient){
                    if(!((d*i)%1 == 0))
                        check=false;
                }
                if(check)
                    break;
            }
            LinkedList<Integer> finalReactantCoefficient=new LinkedList<>();
            LinkedList<Integer> finalProductCoefficient=new LinkedList<>();
            for (Double aDouble : reactantCoefficient) finalReactantCoefficient.addLast((int) (aDouble * i));
            for (Double aDouble : productCoefficient) finalProductCoefficient.addLast((int) (aDouble * i));
            //String formatting
            String[] reactantArr=(reactantsString.replaceAll("[^a-zA-Z0-9+]", "")).split("\\+");
            String[] productArr=(productString.replaceAll("[^a-zA-Z0-9+]", "")).split("\\+");
            StringBuilder formattedReactants=new StringBuilder();
            for (i = 0; i <reactants.size() ; i++)
                formattedReactants.append(finalReactantCoefficient.get(i)).append(reactantArr[i]).append(i < reactants.size() - 1 ? " + " : "");
            StringBuilder formattedProducts=new StringBuilder();
            for (i = 0; i <products.size() ; i++)
                formattedProducts.append(finalProductCoefficient.get(i)).append(productArr[i]).append(i < products.size() - 1 ? " + " : "");
            System.out.println("\n"+formattedReactants+" ---> "+formattedProducts);
        }
    }

    /**
     * Gets all elements used on a side an equation
     * @param inputString String representation of chemical equation side
     * @return LinkedList of all elements on side
     */
    static LinkedList getElements(String inputString){
        LinkedList<String> elements= new LinkedList<>();
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

    /**
     * Get Input.
     * @param text Input prompt.
     * @return String value of what was returned.
     */
    private static String input(String text) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(text);
        return scanner.nextLine();
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
        for (int i = 0; i < inputString.length(); i++) {
            char character = inputString.charAt(i);
            if (Character.isLetter(character)) {
                //Checks that this is a letter
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))){
                    //This is uppercase
                    if (!symbol.equals("")){
                        //Symbol is filled and needs to be dumped
                        try{
                            dictionary.put(symbol, Integer.valueOf(numString));
                        }catch (NumberFormatException exception) {
                            dictionary.put(symbol, 1);
                        }
                        symbol="";
                        numString="";
                    }
                    symbol = symbol.concat(String.valueOf(character));
                }
                else{
                    symbol = symbol.concat(String.valueOf(character));
                }
            } else if (Character.isDigit(character)) {
                //This is a number
                numString = numString.concat(String.valueOf(character));
            }
        }
        if (numString.equals("")){
            numString="1";
        }
        dictionary.put(symbol, Integer.valueOf(numString));
        return dictionary;
        }
}