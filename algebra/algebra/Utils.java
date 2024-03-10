package algebra;

import matrices.*;

public final class Utils {

	public static void checkIndex(int index, int maxPermissionInclusive) {
		if (index < 0 || index > maxPermissionInclusive) {
			throw new IndexOutOfBoundsException();
		}
	}

	public static void checkArrays2dSameDimension(double[][] arr1, double[][] arr2) {
		if (arr1.length != arr2.length || arr1[0].length != arr2[0].length)
			throw new IllegalArgumentException();
	}

	public static void checkArrays2dCorrectDimensionForMultiplication(double[][] arr1, double[][] arr2) {
		if (arr1[0].length != arr2.length)
			throw new IllegalArgumentException();
	}

	public static void checkMatricesSameDimension(Matrix m1, Matrix m2) {
		if (m1.rowSize() != m2.rowSize() || m1.columnSize() != m2.columnSize())
			throw new IllegalArgumentException();
	}

	public static void checkMatricesCorrectDimensionForMultiplication(Matrix m1, Matrix m2) {
		if (m1.columnSize() != m2.rowSize())
			throw new IllegalArgumentException();
	}

	static double UMBRAL_ZERO = 1e-12;
	static double UMBRAL_TOLERANCE = 1e-9;

	public static double shouldRound(double n) {
		double aux = Math.abs(n);
		if (aux <= UMBRAL_ZERO)
			return 0;
		aux = aux - (int) aux;
		if (aux - UMBRAL_TOLERANCE <= 0 || aux + UMBRAL_TOLERANCE >= 1)
			return Math.round(n);
		return n;
	}

}
