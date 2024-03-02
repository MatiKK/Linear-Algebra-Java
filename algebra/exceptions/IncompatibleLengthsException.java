package exceptions;

/**
 * Thrown when attempting to perform a array operation on 
 * array with different lengths, when their length is required
 * to be the same (vector addition, vector subtraction, dot product, etc).
*/
public class IncompatibleLengthsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6669018978829134976L;

	public IncompatibleLengthsException() {
		super();
	}

	public IncompatibleLengthsException(String msg) {
		super(msg);
	}

	public IncompatibleLengthsException(int len1, int len2) {
		super(differentLengthMessage(len1, len2));
	}

	private static String differentLengthMessage(int len1, int len2) {
		return "Unsupported operation for different lengths: " + len1 +" and "+len2;
	}

}
