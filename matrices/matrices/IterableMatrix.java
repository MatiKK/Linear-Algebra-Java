package matrices;

public interface IterableMatrix extends Matrix {

	Matrix subMatrix(int indexRow, int indexColumn);
}
