package matrices;

public class Index2d {

	private int indexRow;
	private int indexColumn;

	public Index2d(int indR, int indC) {
		indexRow = indR;
		indexColumn = indC;
	}

	public int getRowIndex() {
		return indexRow;
	}

	public int getColumnIndex() {
		return indexColumn;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Index2d) {
			Index2d ind = (Index2d) o;
			return indexRow == ind.indexRow && indexColumn == ind.indexColumn;
		}
		return false;
	}

	@Override
	public String toString() {
		return "index (" + indexRow + ',' + indexColumn + ')';
	}

}
