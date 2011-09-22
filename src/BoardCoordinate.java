public class BoardCoordinate {
	public final byte row;
	public final byte column;

	public BoardCoordinate(byte row, byte column) {
		this.row = row;
		this.column = column;
	}

	public final boolean equals(BoardCoordinate bc) {
		return bc != null && bc.row == row && bc.column == column;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof BoardCoordinate) {
			return equals((BoardCoordinate) arg0);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return String.format("{%d, %d}", row, column);
	}

}
