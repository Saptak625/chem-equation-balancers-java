# chem-equation-balancers
This repository has four versions of chemical equation balancers.

## First Version 
The first version uses a very simple inspection method that can solve simple to moderate difficulty equations. This version does not support any parentheses.
Files: Chemical_Equation_Balancer.java

## Second Version
The second version uses a procedural version of the simplified algebraic method (https://jaminsantiago.files.wordpress.com/2013/04/balancing-chemical-equations-easy-algebraic-method.pdf) along with polyatomic replacement that solve almost any chemical equation. This version supports parentheses().
Files: ChemicalEquationBalancer2.java

## Third Version
The third version is an object oriented equalivent of the second version. 
Files: ChemicalEquationBalancer3.java

## Fourth Version
The fourth version takes a whole new approach compared to the others. Instead of using the simplified algebraic method(where variable space has to be accounted inside code), this uses a complete algebraic/matrix solving method. As the more number of variables seems to suggest that a stronger balancing algorithm(in-depth experiment: (https://github.com/Saptak625/chem-node-charts)), this method maximizes it, while also keeping internal complexities of some matrix solving to a minimum. The matrix solving method used is a custom gaussian elimination with back substitution. My hypothesis of being able to solve all chemical equations hasn't been tested yet. Any suggestions, ideas, or resources would be greatly appreciated! Files: ChemicalEquationBalancer4.java
