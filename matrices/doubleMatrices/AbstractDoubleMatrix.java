package doubleMatrices;

import java.util.Arrays;
import algebra.Utils;

public abstract class AbstractDoubleMatrix implements DoubleIterableMatrix {

	public void addRow(double[] row) {
		addRow(rowSize(), row);
	}

	public void addColumn(double[] column) {
		addColumn(columnSize(), column);
	}

	public void addRow(int index, double[] row) {
		Utils.checkIndex(index, rowSize());
	}

	public void addColumn(int index, double[] column) {
		Utils.checkIndex(index, columnSize());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractDoubleMatrix))
			return false;
		AbstractDoubleMatrix m = (AbstractDoubleMatrix) o;
		if (rowSize() != m.rowSize() || columnSize() != m.columnSize())
			return false;
		for (int i = 0; i < rowSize(); i++) {
			/*
			 * Arrays.equals() can't compare 0 and -0, (0-0d == 0.0d) == false It is better
			 * to override this implementation
			 */
			if (!Arrays.equals(getRow(i), m.getRow(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < rowSize(); i++) {
			sb.append(Arrays.toString(getRow(i)));
			if (i != rowSize() - 1) {
				sb.append(", ");
			}
		}
		sb.append(']');
		return sb.toString();
	}

}
