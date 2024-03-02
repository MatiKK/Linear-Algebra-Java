package doubleMatrices;

import matrices.IterableMatrix;
import matrices.Index2d;

public interface DoubleIterableMatrix extends IterableMatrix, DoubleMatrix {

	Index2d indexOf(double number);

	Index2d lastIndexOf(double number);

	void addRow(double[] row);

	void addRow(int index, double[] row);

	void addColumn(double[] column);

	void addColumn(int index, double[] column);

	double[] getRow(int index);

	double[] getColumn(int index);

	double getNumber(int indexRow, int indexColumn);

	double[] setRow(int index, double[] row);

	double[] setColumn(int index, double[] column);

	double[] removeRow(int index);

	double[] removeColumn(int index);

}
