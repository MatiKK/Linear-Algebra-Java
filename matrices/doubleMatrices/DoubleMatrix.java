package doubleMatrices;

import matrices.Matrix;

public interface DoubleMatrix extends Matrix {

	void addRow(double[] row);

	void addColumn(double[] column);

}