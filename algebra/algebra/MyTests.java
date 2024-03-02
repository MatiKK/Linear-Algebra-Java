package algebra;

@SuppressWarnings("unused")
class MyTests {

	public static void main(String[] args) {

		RealMatrix m1;
		double[][] data = {
				{-1, 2, 3},
				{2, 3, 4},
				{3, 4, 5}
		};

		int s = 100;
		m1 = RealMatrix.random(s);
//		m1 = new RealMatrix(data);

		double det;
		long a, b, c;
		a = System.nanoTime();
		det = m1.determinant();
		b = System.nanoTime();
		c = b - a;

		translateTime(c);
	}

	private static void translateTime(long c) {
		System.out.println("Operation computed in " + c + " nanoseconds -> " + (double) c / 1000000000d + " seconds");
	}

}