package algebra;

public final class MatrixOperations {

//	TODO sparse matrices?
//	private static Double ZERO = Double.valueOf(0.0d);

	/**
	 * @deprecated Too heavy process
	 */
	public static double[][] addArrays2d(double[][] arr1, double[][] arr2) {
		Utils.checkArrays2dSameDimension(arr1, arr2);
		int r, c;
		double[][] mat = new double[r = arr1.length][c = arr1.length];
		for (int i = 0; i < r; i++)
			for (int j = 0; j < c; j++) {
				mat[i][j] = Utils.shouldRound(arr1[i][j] + arr2[i][j]);
			}
		return mat;
	}

	/**
	 * @deprecated Too heavy process
	 */
	public static double[][] subtractArrays2d(double[][] arr1, double[][] arr2) {
		Utils.checkArrays2dSameDimension(arr1, arr2);
		int r, c;
		double[][] mat = new double[r = arr1.length][c = arr1.length];
		for (int i = 0; i < r; i++)
			for (int j = 0; j < c; j++) {
				mat[i][j] = Utils.shouldRound(arr1[i][j] - arr2[i][j]);
			}
		return mat;
	}

	/**
	 * @deprecated
	 */
	public static double[][] multiplyByScalarArray2d(double[][] arr, double alpha) {
		if (!Double.isFinite(alpha))
			throw new IllegalArgumentException("Invalid number as scalar: " + alpha);
		int r, c;
		double[][] matt = new double[r = arr.length][c = arr[0].length];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				matt[i][j] = arr[i][j] * alpha;
			}
		}
		return matt;
	}

	/**
	 * @deprecated Same speed but heavier
	 * Also the alhgorithm is wrong
	 */
	public static double[][] multiplyArrays2d(double[][] arr1, double[][] arr2) {
		Utils.checkArrays2dCorrectDimensionForMultiplication(arr1, arr2);
		int r1, c2, common;
		double[][] mat = new double[r1 = arr1.length][c2 = arr2[0].length];
		common = arr2.length;
		for (int i = 0; i < r1; i++) {
			for (int j = 0; j < c2; j++) {
				for (int ij = 0; ij < common; ij++) {
					mat[i][j] += Utils.shouldRound(arr1[i][ij] * arr2[ij][j]);
				}
			}
		}
		return mat;
	}

	public static RealMatrix addMatrices(RealMatrix mat1, RealMatrix mat2) {
//		return new RealMatrix(addArrays2d(mat1.data(), mat2.data()), false, false);
		Utils.checkMatricesSameDimension(mat1, mat2);
		RealMatrix m = (RealMatrix) mat1.clone();
		m.add(mat2);
		return m;
	}

	public static RealMatrix subtractMatrices(RealMatrix mat1, RealMatrix mat2) {
//		return new RealMatrix(subtractArrays2d(mat1.data(), mat2.data()), false, false);
		Utils.checkMatricesSameDimension(mat1, mat2);
		RealMatrix m = (RealMatrix) mat1.clone();
		m.subtract(mat2);
		return m;
	}

	public static RealMatrix multiplyMatrices(RealMatrix mat1, RealMatrix mat2) {
//		return new RealMatrix(multiplyArrays2d(mat1.data(), mat2.data()), false, false);
		Utils.checkMatricesSameDimension(mat1, mat2);
		RealMatrix m = (RealMatrix) mat1.clone();
		m.multiply(mat2);
		return m;
	}

	public static RealMatrix matrixScalarMultiplication(RealMatrix mat, double alpha) {
//		return new RealMatrix(multiplyByScalarArray2d(mat.data(), alpha), false, false);
		RealMatrix m = (RealMatrix) mat.clone();
		m.scalarMultiply(alpha);
		return m;
	}

	public static double[][] identityArray2d(int n){
		if (n < 1)
			throw new IllegalArgumentException();
		double[][] mat = new double[n][n];
		double[] row;
		for (int i = 0; i < n; i++) {
			row = new double[n];
			row[i] = 1;
			mat[i] = row;
		}
		return mat;
	}

	public static RealMatrix identityMatrix(int n) {
		return new RealMatrix(identityArray2d(n), false, false);
	}

}
