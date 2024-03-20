package algebra;

import java.util.Arrays;
import exceptions.NonInvertibleMatrixException;
import exceptions.NonSquareMatrixException;
import matrices.Index2d;

public class RealMatrix extends doubleMatrices.AbstractRegularDoubleMatrix
		implements RealMatrixNumberArraysExpansion, Cloneable, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3267064948917050596L;

	private double[][] numbers;

	private int columnsLength;
	private int rowsLength;

	private int currentRowCapacity;
	private int currentColumnCapacity;

	private static int DEFAULT_ROW_CAPACITY = 5;
	private static int DEFAULT_COLUMN_CAPACITY = 5;

	public RealMatrix() {
		this(true);
	}

	public RealMatrix(boolean shouldStartWithCapacityDimension) {
		this(DEFAULT_ROW_CAPACITY, DEFAULT_COLUMN_CAPACITY, shouldStartWithCapacityDimension);
	}

	public RealMatrix(int n) {
		this(n, true);
	}

	public RealMatrix(int n, boolean shouldStartWithCapacityDimension) {
		this(n, n, shouldStartWithCapacityDimension);
	}

	public RealMatrix(int m, int n) {
		this(m, n, true);
	}

	public RealMatrix(int initialRowCapacity, int initialColumnCapacity, boolean shouldStartWithCapacityDimension) {
		if (initialRowCapacity < 0 || initialColumnCapacity < 0)
			throw new IllegalArgumentException();
		currentRowCapacity = initialRowCapacity;
		currentColumnCapacity = initialColumnCapacity;
		numbers = new double[currentRowCapacity][currentColumnCapacity];
		if (shouldStartWithCapacityDimension) {
			rowsLength = currentRowCapacity;
			columnsLength = currentColumnCapacity;
		}
	}

	public RealMatrix(double[][] data) {
		this(data, true, false);
	}

	RealMatrix(double[][] data, boolean shouldCheckIf2dArrayIsValid, boolean keepSameCapacity) {
		if (shouldCheckIf2dArrayIsValid) {
			checkIfItIsRegular(data);
		}
		assignMatrix(data, keepSameCapacity);
	}

	private void assignMatrix(double[][] data, boolean keepCurrentCapacity) {
		numbers = data.clone();
		int r = data.length, c = data[0].length;
		if (keepCurrentCapacity) {
			if (currentRowCapacity < r)
				throw new IllegalArgumentException();
			if (currentColumnCapacity < c)
				throw new IllegalArgumentException();

			growRowsCapacity(currentRowCapacity, false);
			growColumnsCapacity(currentColumnCapacity, false);

		} else {
			currentRowCapacity = r;
			currentColumnCapacity = c;
		}
		rowsLength = r;
		columnsLength = c;
	}

	private static void checkIfItIsRegular(double[][] data) {
		int size = data.length;
		if (size == 0) {
			throw new IllegalArgumentException("Empty matrix");
		}
		int expectedSize = data[0].length;
		if (expectedSize == 0) {
			throw new IllegalArgumentException("Empty row");
		}
		for (int i = 1; i < size; i++) {
			if (data[i].length != expectedSize) {
				throw new IllegalArgumentException("The 2d-array is not regular");
			}
		}
	}

	public static RealMatrix random(int s) {
		return random(s, s);
	}

	public static RealMatrix random(int s, int min, int max) {
		return random(s, s, min, max);
	}

	public static RealMatrix random(int r, int c) {
		return random(r, c, -10, 10);
	}

	public static RealMatrix random(int r, int c, int min, int max) {
		if (r <= 0 || c <= 0)
			throw new IllegalArgumentException("Illegal matrix dimension " + r + "x" + c);
		double[][] mat = new double[r][c];
		for (int i = 0; i < r; i++) {
			mat[i] = NumericArrays.randomDoubleArray(c, min, max);
		}
		return new RealMatrix(mat, false, false);
	}

	public double[][] rawData() {
		return numbers.clone();
	}

	public double[][] data() {
		int r = rowSize(), c = columnSize();
		double[][] data = new double[r][c];
		for (int i = 0; i < r; i++)
			data[i] = getRowUnsafe(i);
		return data;
	}

	public int rowSize() {
		return rowsLength;
	}

	public int columnSize() {
		return columnsLength;
	}

	public int totalSize() {
		// It is a regular matrix so there are no inconsistency in rows length
		return rowSize() * columnSize();
	}

	public boolean isSquare() {
		// 0x0 matrices won't be considerated square
		return !isEmpty() && rowSize() == columnSize();
	}

	@Override
	public Object clone() {
		try {
			RealMatrix m = (RealMatrix) super.clone();
			m.numbers = numbers.clone();
			return m;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RealMatrix)) {
			return false;
		}
		RealMatrix m = (RealMatrix) o;
		if (rowsLength != m.rowsLength || columnsLength != m.columnsLength) {
			return false;
		}
		for (int i = 0; i < rowsLength; i++) {
			for (int j = 0; j < columnsLength; j++) {
				if (numbers[i][j] != m.numbers[i][j])
					return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return data().hashCode();
	}

	public RealMatrix subMatrix(int indexRow, int indexColumn) {
		if (isEmpty() || rowSize() == 1 || columnSize() == 1) {
			throw new IllegalArgumentException();
		}

		checkIndexForNumber(indexRow,indexColumn);

		double[][] mat = new double[rowsLength - 1][columnsLength - 1];
		int a = -1, b;
		for (int i = 0; i < rowSize(); i++) {
			if (i == indexRow) {
				continue;
			}
			a++;
			b = -1;
			for (int j = 0; j < columnSize(); j++) {
				if (j == indexColumn) {
					continue;
				}
				b++;
				mat[a][b] = numbers[i][j];
			}
		}
		return new RealMatrix(mat, false, false);
	}

	public void print() {
		System.out.println(eachRowByLineBreak());
	}

	private String eachRowByLineBreak() {
		if (isEmpty())
			return "[]";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rowSize(); i++) {
			sb.append('[');
			for (int j = 0; j < columnSize(); j++) {
				double n = getNumberUnsafe(i, j);
				sb.append(n);
				if (j == columnSize() - 1) {
					break;
				}
				sb.append(", ");
			}
			sb.append("]\n");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	public Index2d indexOf(double number) {
		for (int i = 0; i < rowSize(); i++) {
			for (int j = 0; j < columnSize(); j++) {
				if (getNumberUnsafe(i, j) == number)
					return new Index2d(i, j);
			}
		}
		return null;
	}

	public Index2d lastIndexOf(double number) {
		for (int i = rowSize() - 1; i >= 0; i--) {
			for (int j = columnSize() - 1; j >= 0; j--) {
				if (getNumberUnsafe(i, j) == number)
					return new Index2d(i, j);
			}
		}
		return null;
	}

	public void addRow(double[] row) {
		// faster than addRow(rowSize(), row)
		int len = row.length;
		checkIfRowCanBeAdded(len);
		checkRowCapacity();
//		numbers[rowsLength] = Arrays.copyOf(row, currentColumnCapacity);
		for(int i = 0; i < len; i++)
			numbers[len][i] = row[i];
		rowsLength++;
	}

	public void addRow(int index, double[] row) {
		checkIndexForAddRow(index);
		checkIfRowCanBeAdded(row.length);
		addRowIgnoreRequirements(index, row);
	}

	private void addRowIgnoreRequirements(int index, double[] row) {
		checkRowCapacity();
		System.arraycopy(numbers, index, numbers, index + 1, rowSize() - index);
		numbers[index] = Arrays.copyOf(row, currentColumnCapacity);
		int len = row.length;
		for(int i = 0; i < len; i++)
			numbers[index][i] = row[i];
		rowsLength++;
		columnsLength = len;
	}

	private void checkRowCapacity() {
		if (currentRowCapacity == rowSize())
			growRowsCapacity();
	}

	public void addColumn(double[] column) {
		int len = column.length;
		checkIfColumnCanBeAdded(len);
		checkColumnCapacity();
		for(int i = 0; i < len; i++)
			numbers[i][len] = column[i];
		columnsLength++;
	}

	public void addColumn(int index, double[] column) {
		checkIndexForAddColumn(index);
		checkIfColumnCanBeAdded(column.length);
		addColumnIgnoreRequirements(index, column);
	}

	private void addColumnIgnoreRequirements(int index, double[] column) {
		checkColumnCapacity();
		for (int i = 0; i < rowSize(); i++) {
			double[] row = numbers[i];
			System.arraycopy(row, index, row, index + 1, columnSize() - index);
			numbers[i][index] = column[i];
		}
		columnsLength++;

	}

	private void checkColumnCapacity() {
		if (currentColumnCapacity == columnSize())
			growColumnsCapacity();
	}

	private void checkIfRowCanBeAdded(int vectorLength) {
		if (isEmpty()) {
			if (vectorLength > currentColumnCapacity)
			numbers = new double[currentRowCapacity]
					[currentColumnCapacity = columnsLength = vectorLength];
		} else {
			if (vectorLength != columnsLength)
				throw new IllegalArgumentException();
		}
	}

	private void checkIfColumnCanBeAdded(int vectorLength) {
		if (isEmpty()) {
			if (vectorLength > currentRowCapacity)
			numbers = new double[currentRowCapacity = rowsLength = vectorLength]
					[currentColumnCapacity];
		} else {
			if (vectorLength != rowsLength)
				throw new IllegalArgumentException();
		}
	}

	private static int MIN_ROWS_GROW = 1;
	private static int MIN_COLS_GROW = 1;

	private void growRowsCapacity() {
		growRowsCapacity(MIN_ROWS_GROW, true);
	}

	public void growRowsCapacity(int newLength, boolean addToCurrentCapacity) {
		if (addToCurrentCapacity) {
			newLength += currentRowCapacity;
		}
		numbers = Arrays.copyOf(numbers, currentRowCapacity = newLength);
		Arrays.fill(numbers, rowSize(), newLength, new double[currentColumnCapacity]);
	}

	private void growColumnsCapacity() {
		growColumnsCapacity(MIN_COLS_GROW, true);
	}

	private void growColumnsCapacity(int newLength, boolean addToCurrentCapacity) {
//		if (currentRowCapacity == 0) {
//			growRowsCapacity(1);
//		}
		if (addToCurrentCapacity) {
			newLength += currentColumnCapacity;
		}
		if (newLength <= currentColumnCapacity) return;
		for (int i = 0; i < currentRowCapacity; i++) {
			numbers[i] = Arrays.copyOf(numbers[i], newLength);
			Arrays.fill(numbers[i], columnSize(), newLength, 0);
		}
		currentColumnCapacity = newLength;
	}

	public void trimToSize() {
		trimColumnsToSize();
		trimRowsToSize();
	}

	public void trimColumnsToSize() {
		for (int i = 0; i < currentRowCapacity; i++) {
			numbers[i] = Arrays.copyOf(numbers[i], columnSize());
		}
		currentColumnCapacity = columnSize();
	}

	public void trimRowsToSize() {
		numbers = Arrays.copyOf(numbers, rowSize());
		currentRowCapacity = rowSize();
	}

	public double[] getRow(int index) {
		checkIndexForRow(index);
		return getRowUnsafe(index);
	}

	private double[] getRowUnsafe(int index) {
		return Arrays.copyOf(numbers[index], columnSize());
	}

	public double[] getColumn(int index) {
		checkIndexForColumn(index);
		int size;
		double[] column = new double[size = rowSize()];
		for (int i = 0; i < size; i++) {
			column[i] = numbers[i][index];
		}
		return column;
	}

	public double getNumber(int indexRow, int indexColumn) {
		checkIndexForNumber(indexRow, indexColumn);
		return getNumberUnsafe(indexRow, indexColumn);
	}

	protected double getNumberUnsafe(int indexRow, int indexColumn) {
		return numbers[indexRow][indexColumn];
	}

	public double[] setRow(int index, double[] newRow) {
		checkIndexForRow(index);
		checkIfRowCanBeAdded(newRow.length);
		return setRowUnsafe(index, newRow);
	}

	private double[] setRowUnsafe(int index, double[] newRow) {
		double[] r = getRowUnsafe(index);
		if (index == 0 && rowsLength == 1) {
			growColumnsCapacity(columnsLength = newRow.length, false);
			numbers[index] = Arrays.copyOf(newRow, currentColumnCapacity);
			return r;
		}
		setRowIgnoreRequirements(index, newRow);
		return r;
	}

	private void setRowIgnoreRequirements(int index, double[] newRow) {
		numbers[index] = Arrays.copyOf(newRow, currentColumnCapacity);
	}

	public double[] setColumn(int index, double[] newColumn) {
		checkIndexForColumn(index);
		checkIfColumnCanBeAdded(newColumn.length);
		return setColumnIgnoreRequirements(index, newColumn);
	}

	private double[] setColumnIgnoreRequirements(int index, double[] newColumn) {
		int size;
		double[] removedColumn = new double[size = rowSize()];
		for (int i = 0; i < size; i++) {
			removedColumn[i] = numbers[i][index];
			numbers[i][index] = newColumn[i];
		}
		return removedColumn;
	}

	public double setNumber(int indexRow, int indexColumn, double newNumber) {
		double removedNumber = getNumber(indexRow, indexColumn);
		setNumberUnsafe(indexRow, indexColumn, newNumber);
		return removedNumber;
	}

	protected void setNumberUnsafe(int indexRow, int indexColumn, double newNumber) {
		numbers[indexRow][indexColumn] = newNumber;
	}

	public double[] removeRow(int index) {
		checkIndexForRow(index);
		double[] removedRow = getRowUnsafe(index);
		System.arraycopy(numbers, index + 1, numbers, index, rowSize() - index - 1);
		rowsLength--;
		return removedRow;
	}

	public double[] removeColumn(int index) {
		double[] deletedColumn = getColumn(index);
		for (int i = 0; i < rowSize(); i++) {
			double[] row = numbers[i];
			System.arraycopy(row, index + 1, row, index, currentColumnCapacity - index - 1);
			row[currentColumnCapacity - 1] = 0;
//			numbers[i] = row;
		}
		columnsLength--;
		return deletedColumn;
	}

	public void swapRows(int indexRow1, int indexRow2) {
		checkIndexForRow(indexRow1);
		checkIndexForRow(indexRow2);
		if (indexRow1 == indexRow2)
			return; // May look if it is better to throw exception
		swapRowsUnsafe(indexRow1, indexRow2);
	}

	private void swapRowsUnsafe(int indexRow1, int indexRow2) {
		double[] temp = numbers[indexRow2];
		numbers[indexRow2] = numbers[indexRow1];
		numbers[indexRow1] = temp;
	}

	private void checkIndexForRow(int index) {
		checkIndex0(index, rowSize() - 1);
	}

	private void checkIndexForAddRow(int index) {
		checkIndex0(index, rowSize());
	}

	private void checkIndexForColumn(int index) {
		checkIndex0(index, columnSize() - 1);
	}

	private void checkIndexForAddColumn(int index) {
		checkIndex0(index, columnSize());
	}

	private void checkIndexForNumber(int indexR, int indexC) {
		checkIndexForRow(indexR);
		checkIndexForColumn(indexC);
	}

	private void checkIndex0(int index, int max) {
		Utils.checkIndex(index, max);
	}

	/*
	 * ------------------------------------------------------------
	 * --------------------- ALBEGRA MATHODS ----------------------
	 * ------------------------------------------------------------
	 */

	/**
	 * Orders the rows of the matrix based on the index of the pivot of each row.
	 *
	 * This method performs row ordering in the matrix based on the index of the
	 * pivot in each row. The pivot is the leftmost non-zero element in a row after
	 * previous rows have been processed. The rows are rearranged to bring rows with
	 * lower pivot indices to the top, which is a common step in Gaussian
	 * elimination.
	 *
	 * The method returns the total number of swaps performed during the row
	 * ordering process. This value is useful in calculating the determinant of the
	 * matrix.
	 *
	 * @return the total number of swaps performed during row ordering
	 */
	public int orderRows() {
		return LinearAlgebra.matrixOrderingRows(this);
	}

	/**
	 * Converts the matrix to its row echelon form.
	 *
	 * This method transforms the matrix into its row echelon form using Gaussian
	 * elimination. The process involves performing row operations to create a
	 * triangular matrix where the leading entry (pivot) in each row is to the right
	 * of the pivot in the row above.
	 *
	 */
	public void rowEchelonForm() {
		LinearAlgebra.matrixRowEchelonForm(this);
	}

	/**
	 * Calculates and returns the determinant of the matrix.
	 *
	 * This method computes the determinant of the matrix using a suitable
	 * algorithm, which is based on row operations such as Gaussian elimination. The
	 * determinant is a scalar value that represents the scaling factor by which the
	 * matrix's volume changes under linear transformations.
	 *
	 * @return the determinant of the matrix as a double value
	 * @throws IllegalArgumentException If the matrix is not square, i.e., the
	 *                                  number of rows is not equal to the number of
	 *                                  columns.
	 */
	public double determinant() {
		return LinearAlgebra.matrixDeterminant(this);
	}

	/**
	 * Returns the inverse of this matrix The cofactor matrix is formed by taking
	 * the determinant of each submatrix(i,j) and multiplying it by (-1)^(i + j),
	 * where i and j are the row and column indices of the element in the original
	 * matrix, and submatrix(i,j) is a minor matrix where the i-row and j-column are
	 * removed. The cofactor matrix is used in various mathematical operations, such
	 * as computing the adjugate matrix and the inverse of a matrix using the
	 * adjugate formula.
	 * 
	 * @return the inverse of this matrix
	 * @throws NonSquareMatrixException     if this matrix is not square
	 * @throws NonInvertibleMatrixException if this matrix is not invertible
	 */
	public RealMatrix cofactor() {
		return LinearAlgebra.matrixCofactor(this);
	}

	/**
	 * Returns the adjugate of this matrix. The adjugate of a square matrix is the
	 * transpose of its cofactor matrix. It is used in various mathematical
	 * operations, such as computing the inverse of a matrix using the adjugate
	 * formula and solving systems of linear equations.
	 * 
	 * @return The adjugate of this matrix
	 * @throws NonSquareMatrixException     if this matrix is not square
	 * @throws NonInvertibleMatrixException if this matrix is not invertible
	 */
	public RealMatrix adjugate() {
		return LinearAlgebra.matrixAdjugate(this);
	}

	/**
	 * Returns the inverse of this matrix, if it exists. The inverse of a matrix is
	 * another matrix such that when it's multiplied by the original matrix, the
	 * result is the identity matrix. In other words, if A is the original matrix
	 * and B is its inverse, then A * B = B * A = Identity Matrix. The inverse of a
	 * matrix is used in various mathematical and computational applications,
	 * including solving systems of linear equations, computing determinants, and
	 * performing transformations in computer graphics and simulations.
	 *
	 * @return the inverse of this matrix, if it exists
	 * @throws NonSquareMatrixException     if this matrix is not square
	 * @throws NonInvertibleMatrixException if this matrix is not invertible
	 */
	public RealMatrix inverse() throws NonInvertibleMatrixException {
		return LinearAlgebra.matrixInverseGaussianElimination(this);
//		return LinearAlgebra.matrixInverseAdjugateDefinition(this);
	}

	/**
	 * Returns the transpose of this matrix.
	 * 
	 * @return the transpose of this matrix
	 */
	public RealMatrix transpose() {
		int r = rowSize(), c = columnSize();
		double[][] mat = new double[c][r];
		for (int i = 0; i < c; i++) {
			for (int j = 0; j < r; j++) {
				mat[i][j] = numbers[j][i];
			}
		}
		return new RealMatrix(mat, false, false);
	}

	@Override
	public void addRow(int[] row) {
		addRow(rowSize(), row);
	}

	@Override
	public void addRow(byte[] row) {
		addRow(rowSize(), row);
	}

	@Override
	public void addRow(short[] row) {
		addRow(rowSize(), row);
	}

	@Override
	public void addRow(long[] row) {
		addRow(rowSize(), row);
	}

	@Override
	public void addRow(float[] row) {
		addRow(rowSize(), row);
	}

	@Override
	public <T extends Number> void addRow(T[] row) {
		addRow(rowSize(), row);
	}

	@Override
	public void addRow(int index, int[] row) {
		checkIndexForRow(index);
		checkIfColumnCanBeAdded(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, byte[] row) {
		checkIndexForRow(index);
		checkIfColumnCanBeAdded(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, short[] row) {
		checkIndexForRow(index);
		checkIfColumnCanBeAdded(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, long[] row) {
		checkIndexForRow(index);
		checkIfColumnCanBeAdded(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, float[] row) {
		checkIndexForRow(index);
		checkIfColumnCanBeAdded(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public <T extends Number> void addRow(int index, T[] row) {
		checkIndexForRow(index);
		checkIfColumnCanBeAdded(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addColumn(int[] column) {
		addColumn(columnSize(), column);
	}

	@Override
	public void addColumn(byte[] column) {
		addColumn(columnSize(), column);
	}

	@Override
	public void addColumn(short[] column) {
		addColumn(columnSize(), column);
	}

	@Override
	public void addColumn(long[] column) {
		addColumn(columnSize(), column);
	}

	@Override
	public void addColumn(float[] column) {
		addColumn(columnSize(), column);
	}

	@Override
	public <T extends Number> void addColumn(T[] column) {
		addColumn(columnSize(), column);
	}

	@Override
	public void addColumn(int index, int[] column) {
		checkIndexForColumn(index);
		checkIfColumnCanBeAdded(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, byte[] column) {
		checkIndexForColumn(index);
		checkIfColumnCanBeAdded(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, short[] column) {
		checkIndexForColumn(index);
		checkIfColumnCanBeAdded(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, long[] column) {
		checkIndexForColumn(index);
		checkIfColumnCanBeAdded(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, float[] column) {
		checkIndexForColumn(index);
		checkIfColumnCanBeAdded(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public <T extends Number> void addColumn(int index, T[] column) {
		checkIndexForColumn(index - 1);
		checkIfColumnCanBeAdded(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public double[] setRow(int indexRow, int[] newRow) {
		checkIndexForRow(indexRow);
		checkIfColumnCanBeAdded(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, byte[] newRow) {
		checkIndexForRow(indexRow);
		checkIfColumnCanBeAdded(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, short[] newRow) {
		checkIndexForRow(indexRow);
		checkIfColumnCanBeAdded(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, long[] newRow) {
		checkIndexForRow(indexRow);
		checkIfColumnCanBeAdded(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, float[] newRow) {
		checkIndexForRow(indexRow);
		checkIfColumnCanBeAdded(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public <T extends Number> double[] setRow(int indexRow, T[] newRow) {
		checkIndexForRow(indexRow);
		checkIfColumnCanBeAdded(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setColumn(int indexColumn, int[] newColumn) {
		checkIndexForColumn(indexColumn);
		checkIfColumnCanBeAdded(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, byte[] newColumn) {
		checkIndexForColumn(indexColumn);
		checkIfColumnCanBeAdded(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, short[] newColumn) {
		checkIndexForColumn(indexColumn);
		checkIfColumnCanBeAdded(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, long[] newColumn) {
		checkIndexForColumn(indexColumn);
		checkIfColumnCanBeAdded(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, float[] newColumn) {
		checkIndexForColumn(indexColumn);
		checkIfColumnCanBeAdded(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public <T extends Number> double[] setColumn(int indexColumn, T[] newColumn) {
		checkIndexForColumn(indexColumn);
		checkIfColumnCanBeAdded(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	private static double[] castToDoubleArray(int[] arr) {
		double[] res = new double[arr.length];
		for (int i = 0; i < arr.length; i++)
			res[i] = arr[i];
		return res;
	}

	private static double[] castToDoubleArray(byte[] arr) {
		double[] res = new double[arr.length];
		for (int i = 0; i < arr.length; i++)
			res[i] = arr[i];
		return res;
	}

	private static double[] castToDoubleArray(short[] arr) {
		double[] res = new double[arr.length];
		for (int i = 0; i < arr.length; i++)
			res[i] = arr[i];
		return res;
	}

	private static double[] castToDoubleArray(long[] arr) {
		double[] res = new double[arr.length];
		for (int i = 0; i < arr.length; i++)
			res[i] = arr[i];
		return res;
	}

	private static double[] castToDoubleArray(float[] arr) {
		double[] res = new double[arr.length];
		for (int i = 0; i < arr.length; i++)
			res[i] = arr[i];
		return res;
	}

	private static <T extends Number> double[] castToDoubleArray(T[] arr) {
		double[] res = new double[arr.length];
		for (int i = 0; i < arr.length; i++)
			res[i] = arr[i].doubleValue();
		return res;
	}

	public void add(RealMatrix m) {
//		slower and probably I think heavier
//		assignMatrix(MatrixOperations.addMatrices(this, m).data(), true);
 		Utils.checkMatricesSameDimension(this, m);
		for (int i = 0; i < rowSize(); i++)
			for(int j = 0; j < columnSize(); j++)
				numbers[i][j] += m.numbers[i][j];
	}

	public void subtract(RealMatrix m) {
//		assignMatrix(MatrixOperations.subtractMatrices(this, m).data(), true);
		Utils.checkMatricesSameDimension(this, m);
		for (int i = 0; i < rowSize(); i++)
			for(int j = 0; j < columnSize(); j++)
				numbers[i][j] -= m.numbers[i][j];
	}

	public void multiply(RealMatrix m) {
//		assignMatrix(MatrixOperations.multiplyMatrices(this, m).data(), false);
		Utils.checkMatricesCorrectDimensionForMultiplication(this, m);

		int r1 = rowSize(), 
			c2 = m.columnSize(),
			common = m.rowSize();

		growColumnsCapacity(columnsLength = c2, false);

		for (int i = 0; i < r1; i++) {
			double[] rowAux = numbers[i].clone();
			for (int j = 0; j < c2; j++) {
				numbers[i][j] = 0;
				for (int ij = 0; ij < common; ij++) {
					numbers[i][j] += Utils.shouldRound(rowAux[ij] * m.numbers[ij][j]);
				}
			}
		}
	}

	public void scalarMultiply(double alpha) {
//		assignMatrix(MatrixOperations.matrixScalarMultiplication(this, alpha).data(), true);
		for (int i = 0; i < rowSize(); i++)
			for(int j = 0; j < columnSize(); j++)
				numbers[i][j] *= alpha;
	}

}