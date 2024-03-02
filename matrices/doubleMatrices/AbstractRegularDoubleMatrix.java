package doubleMatrices;

import matrices.RegularMatrix;

public abstract class AbstractRegularDoubleMatrix extends AbstractDoubleMatrix implements RegularMatrix {

	@Override
	public void addRow(int index, double[] row) {
		super.addRow(index, row);
		checkRowCompability(row.length);
	}

	public void addColumn(int index, double[] column) {
		super.addColumn(index, column);
		checkColumnCompability(column.length);
	}

	protected void checkRowCompability(int rowLength) {
		if (!isEmpty() && !correctLengthRow(rowLength))
			throw new IllegalArgumentException();
	}

	protected void checkColumnCompability(int columnLength) {
		if (!isEmpty() && !correctLengthColumn(columnLength))
			throw new IllegalArgumentException();
	}

	private boolean correctLengthRow(int rowLength) {
		return rowLength == columnSize();
	}

	private boolean correctLengthColumn(int columnLength) {
		return columnLength == rowSize();
	}

}
