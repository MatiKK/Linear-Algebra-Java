package matrices;

public interface Matrix {

	int rowSize();

	int columnSize();

	int totalSize();

	default boolean isEmpty() {
		return totalSize() == 0;
	}

	int hashCode();

	boolean equals(Object o);

	String toString();

	void print();

}