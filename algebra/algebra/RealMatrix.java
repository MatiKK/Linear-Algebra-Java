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

	private static int DEFAULT_ROW_CAPACITY = 3;
	private static int DEFAULT_COLUMN_CAPACITY = 3;

	public RealMatrix() {
		this(DEFAULT_ROW_CAPACITY, DEFAULT_COLUMN_CAPACITY);
	}

	public RealMatrix(int n) {
		this(n, n);
	}

	public RealMatrix(int initialRowCapacity, int initialColumnCapacity) {
		currentRowCapacity = initialRowCapacity;
		currentColumnCapacity = initialColumnCapacity;
		numbers = new double[currentRowCapacity][currentColumnCapacity];
	}

	public RealMatrix(double[][] mat) {
		this(mat, true, false);
	}

	RealMatrix(double[][] mat, boolean shouldCheckIf2dArrayIsValid, boolean keepSameCapacity) {
		if (shouldCheckIf2dArrayIsValid) {
			checkIfItIsRegular(mat);
		}
		assignMatrix(mat, keepSameCapacity);
	}

	private void assignMatrix(double[][] mat, boolean keepCurrentCapacity) {
		numbers = mat.clone();
		int r = mat.length, c = mat[0].length;
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

	private static void checkIfItIsRegular(double[][] mat) {
		int size = mat.length;
		if (size == 0) {
			throw new IllegalArgumentException("Empty matrix");
		}
		int expectedSize = mat[0].length;
		if (expectedSize == 0) {
			throw new IllegalArgumentException("Empty row");
		}
		for (int i = 1; i < size; i++) {
			if (mat[i].length != expectedSize) {
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
		return data().hashCode() * 127 / 32;
	}

	public matrices.Matrix subMatrix(int indexRow, int indexColumn) {
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
// 		This was a Test

//		System.out.print('[');
//		for (double[] row: numbers)
//			System.out.println(Arrays.toString(row));
//		System.out.println(']');

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

	public void addRow(int index, double[] row) {
		super.addRow(index, row);
		checkIfRowCanBeAdded(row);
		addRowIgnoreRequirements(index, row);
	}

	private void addRowIgnoreRequirements(int index, double[] row) {
		if (currentRowCapacity == rowSize())
			growRowsCapacity();
		System.arraycopy(numbers, index, numbers, index + 1, rowSize() - index);
		numbers[index] = Arrays.copyOf(row, currentColumnCapacity);
		rowsLength++;
		columnsLength = row.length;
	}

	public void addColumn(int index, double[] column) {
		super.addColumn(index, column);
		checkIfColumnCanBeAdded(column);
		addColumnIgnoreRequirements(index, column);
	}

	private void addColumnIgnoreRequirements(int index, double[] column) {
		checkColumnCompability(column.length);
		if (currentColumnCapacity == columnSize())
			growColumnsCapacity();
		rowsLength = column.length;
		for (int i = 0; i < rowSize(); i++) {
			double[] row = numbers[i];
			System.arraycopy(row, index, row, index + 1, columnSize() - index);
			numbers[i][index] = column[i];
		}
		columnsLength++;

	}

	protected void checkIfRowCanBeAdded(double[] row) {
		checkRowCompability(row.length);
		if (isEmpty() && row.length > currentColumnCapacity) {
			numbers = new double[currentRowCapacity][currentColumnCapacity = columnsLength = row.length];
		}
	}

	protected void checkIfColumnCanBeAdded(double[] column) {
		checkColumnCompability(column.length);
		if (isEmpty() && column.length > currentRowCapacity) {
			numbers = new double[currentRowCapacity = rowsLength = column.length][currentColumnCapacity];
		}
	}

	private static int MIN_ROWS_GROW = 1;
	private static int MIN_COLS_GROW = 1;

	private void growRowsCapacity() {
		growRowsCapacity(MIN_ROWS_GROW, true);
	}

	private void growRowsCapacity(int newLength, boolean addToCurrentCapacity) {
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
		for (int i = 0; i < currentColumnCapacity; i++) {
			numbers[i] = Arrays.copyOf(numbers[i], columnSize());
		}
		currentColumnCapacity = columnSize();
	}

	public void trimRowsToSize() {
		numbers = Arrays.copyOf(numbers, rowSize());
		currentRowCapacity = rowSize();
	}

	public double[] getRow(int index) {
		checkIndexToAddOrGetRow(index);
		return getRowUnsafe(index);
	}

	private double[] getRowUnsafe(int index) {
		return Arrays.copyOf(numbers[index], columnSize());
	}

	public double[] getColumn(int index) {
		checkIndexToAddOrGetColumn(index);
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
		checkIndexToSetRow(index);
		checkRowCompability(newRow.length);
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
		checkIndexToSetColumn(index);
		checkColumnCompability(newColumn.length);
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
		checkIndexToAddOrGetRow(index);
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
		checkIndexToAddOrGetRow(indexRow1);
		checkIndexToAddOrGetRow(indexRow2);
		if (indexRow1 == indexRow2)
			return; // May look if it is better to throw exception
		swapRowsUnsafe(indexRow1, indexRow2);
	}

	private void swapRowsUnsafe(int indexRow1, int indexRow2) {
		double[] temp = numbers[indexRow2];
		numbers[indexRow2] = numbers[indexRow1];
		numbers[indexRow1] = temp;
	}

	private void checkIndexToAddOrGetRow(int index) {
		Utils.checkIndex(index, rowSize());
	}

	private void checkIndexToSetRow(int index) {
		Utils.checkIndex(index, rowSize() - 1);
	}

	private void checkIndexToAddOrGetColumn(int index) {
		Utils.checkIndex(index, columnSize());
	}

	private void checkIndexToSetColumn(int index) {
		Utils.checkIndex(index, rowSize() - 1);
	}

	private void checkIndexForNumber(int indexR, int indexC) {
		checkIndexToAddOrGetRow(indexR);
		checkIndexToAddOrGetColumn(indexC);
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
		return new RealMatrix(mat, false, true);
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
		checkIndexToAddOrGetRow(index);
		checkRowCompability(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, byte[] row) {
		checkIndexToAddOrGetRow(index);
		checkRowCompability(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, short[] row) {
		checkIndexToAddOrGetRow(index);
		checkRowCompability(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, long[] row) {
		checkIndexToAddOrGetRow(index);
		checkRowCompability(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public void addRow(int index, float[] row) {
		checkIndexToAddOrGetRow(index);
		checkRowCompability(row.length);
		addRowIgnoreRequirements(index, castToDoubleArray(row));
	}

	@Override
	public <T extends Number> void addRow(int index, T[] row) {
		checkIndexToAddOrGetRow(index);
		checkRowCompability(row.length);
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
		checkIndexToAddOrGetColumn(index);
		checkColumnCompability(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, byte[] column) {
		checkIndexToAddOrGetColumn(index);
		checkColumnCompability(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, short[] column) {
		checkIndexToAddOrGetColumn(index);
		checkColumnCompability(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, long[] column) {
		checkIndexToAddOrGetColumn(index);
		checkColumnCompability(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public void addColumn(int index, float[] column) {
		checkIndexToAddOrGetColumn(index);
		checkColumnCompability(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public <T extends Number> void addColumn(int index, T[] column) {
		checkIndexToAddOrGetColumn(index);
		checkColumnCompability(column.length);
		addColumnIgnoreRequirements(index, castToDoubleArray(column));
	}

	@Override
	public double[] setRow(int indexRow, int[] newRow) {
		checkIndexToSetRow(indexRow);
		checkRowCompability(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, byte[] newRow) {
		checkIndexToSetRow(indexRow);
		checkRowCompability(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, short[] newRow) {
		checkIndexToSetRow(indexRow);
		checkRowCompability(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, long[] newRow) {
		checkIndexToSetRow(indexRow);
		checkRowCompability(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setRow(int indexRow, float[] newRow) {
		checkIndexToSetRow(indexRow);
		checkRowCompability(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public <T extends Number> double[] setRow(int indexRow, T[] newRow) {
		checkIndexToSetRow(indexRow);
		checkRowCompability(newRow.length);
		return setRowUnsafe(indexRow, castToDoubleArray(newRow));
	}

	@Override
	public double[] setColumn(int indexColumn, int[] newColumn) {
		checkIndexToSetColumn(indexColumn);
		checkColumnCompability(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, byte[] newColumn) {
		checkIndexToSetColumn(indexColumn);
		checkColumnCompability(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, short[] newColumn) {
		checkIndexToSetColumn(indexColumn);
		checkColumnCompability(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, long[] newColumn) {
		checkIndexToSetColumn(indexColumn);
		checkColumnCompability(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public double[] setColumn(int indexColumn, float[] newColumn) {
		checkIndexToSetColumn(indexColumn);
		checkColumnCompability(newColumn.length);
		return setColumnIgnoreRequirements(indexColumn, castToDoubleArray(newColumn));
	}

	@Override
	public <T extends Number> double[] setColumn(int indexColumn, T[] newColumn) {
		checkIndexToSetColumn(indexColumn);
		checkColumnCompability(newColumn.length);
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
		assignMatrix(MatrixOperations.addMatrices(this, m).data(), true);
	}

	public void subtract(RealMatrix m) {
		assignMatrix(MatrixOperations.subtractMatrices(this, m).data(), true);
	}

	public void multiply(RealMatrix m) {
		assignMatrix(MatrixOperations.multiplyMatrices(this, m).data(), false);

	}

	public void scalarMultiply(double alpha) {
		assignMatrix(MatrixOperations.matrixScalarMultiplication(this, alpha).data(), true);
	}

}