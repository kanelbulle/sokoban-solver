
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
}
