# Matrices-and-Linear-Algebra

This project is intended to serve linear algebra methods, focusing on operations with vectors and matrices. It offers an easy-to-use interface to perform various operations on vectors and matrices.

## Example Usage

```java
package algebra;

public class ExampleLinearAlgebra {

    public static void main(String[] args){

    	int[] v1 = {1, 2, 3};
    	int[] v2 = {1, 2, 3};
    	int[] vResult = LinearAlgebra.vectorAddition(v1, v2);
    	vResult = LinearAlgebra.vectorSubtraction(v1, v2);
    	vResult = LinearAlgebra.vectorScalarMultiplication(vResult, 12);
    	double dotProd = LinearAlgebra.vectorsDotProduct(v1, v2);
    	int[] orthogonal = LinearAlgebra.perpendicularVector(vResult);
    	int[] crossProd = LinearAlgebra.crossProduct(v1, v2);
    	boolean arePerpendicular = 
    		LinearAlgebra.vectorsArePerpendicular(vResult,crossProd);
    	
        RealMatrix m1, m2, result;

//                  Initial matrix capacity
        m1 = new RealMatrix(3, 3);
        m2 = RealMatrix.random(4, 4);

//      adding Rows to the matrix
        m1.addRow(new Number[]{1, 2, 3});
        m1.addRow(new double[]{4, 6, 6});
        m1.addRow(v1);
        m1.addRow(1, v2);
        m1.addColumn(1, array);

//      matrix operations
        m1.add(m2);
        RealMatrix m3 = MatrixOperations.subtractMatrices(m1,m2);
        result = LinearAlgebra.matrixMultiplication(m1, m2);
        result = LinearAlgebra.matrixScalarMultiplication(result, Math.PI);
        double det = result.determinant();
        result = result.inverse();
    }
}
```

## Advise

It is not suggested to use this project for critical work if efficiency and optimization is what you need, better and more optimized codes of your needings can be found. This project only has the intention to show the creator's passion for programming and math.
