import java.util.Vector;

public class BoardState implements Comparable<BoardState> {
	public static final byte MOVE_NULL = -1;
	public static final byte MOVE_UP = 0;
	public static final byte MOVE_DOWN = 1;
	public static final byte MOVE_LEFT = 2;
	public static final byte MOVE_RIGHT = 3;

	public final Board board;
	public BoardState parent;
	public final byte lastMove;
	public final BoardCoordinate playerCoordinate;
	public final Vector<BoardCoordinate> boxCoordinates;
	private final int hashCode;

	public final int calculateHashCode() {
		int hash = 31 * playerCoordinate.hashCode();
		for (BoardCoordinate bc : boxCoordinates) {
			hash += 31 * 31 * bc.hashCode();
		}

		return hash;
	}

	public BoardState(Board board, BoardCoordinate playerCoordinate,
			Vector<BoardCoordinate> boxCoordinates, byte move) {
		this.board = board;
		this.playerCoordinate = playerCoordinate;
		this.boxCoordinates = boxCoordinates;
		this.lastMove = move;
		this.hashCode = calculateHashCode();
	}

	public BoardState(BoardState aState, BoardCoordinate playerCoordinate,
			BoardCoordinate oldBox, BoardCoordinate newBox, byte move) {
		this.board = aState.board;
		this.playerCoordinate = playerCoordinate;
		this.lastMove = move;

		Vector<BoardCoordinate> bcs = new Vector<BoardCoordinate>();
		for (BoardCoordinate bc : aState.boxCoordinates) {
			if (!bc.equals(oldBox)) {
				bcs.add(bc);
			}
		}

		if (newBox != null) {
			bcs.add(newBox);
		}

		this.boxCoordinates = bcs;
		this.hashCode = calculateHashCode();
	}

	public final boolean boxAt(byte row, byte column) {
		for (BoardCoordinate bc : boxCoordinates) {
			if (bc.row == row && bc.column == column) {
				return true;
			}
		}

		return false;
	}

	public final void neighborBoxes(BoardCoordinate boxCoordinate,
			Vector<BoardCoordinate> list) {
		list.clear();
		if (boxAt((byte) (boxCoordinate.row + 1), boxCoordinate.column)) {
			list.add(new BoardCoordinate((byte) (boxCoordinate.row + 1),
					boxCoordinate.column));
		}
		if (boxAt((byte) (boxCoordinate.row - 1), boxCoordinate.column)) {
			list.add(new BoardCoordinate((byte) (boxCoordinate.row - 1),
					boxCoordinate.column));
		}
		if (boxAt(boxCoordinate.row, (byte) (boxCoordinate.column + 1))) {
			list.add(new BoardCoordinate(boxCoordinate.row,
					(byte) (boxCoordinate.column + 1)));
		}
		if (boxAt(boxCoordinate.row, (byte) (boxCoordinate.column - 1))) {
			list.add(new BoardCoordinate(boxCoordinate.row,
					(byte) (boxCoordinate.column - 1)));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof BoardState) {
			return equals((BoardState) obj);
		}

		return false;
	}

	public boolean equals(BoardState state) {
		return state.playerCoordinate.equals(playerCoordinate)
				&& state.boxCoordinates.size() == boxCoordinates.size()
				&& state.boxCoordinates.containsAll(boxCoordinates)
				&& boxCoordinates.containsAll(state.boxCoordinates);
	}

	@Override
	public final int hashCode() {
		return hashCode;
	}

	public final boolean isSolved() {
		int boxOnGoalCount = 0;
		for (BoardCoordinate bc : this.boxCoordinates) {
			if (board.goalAt(bc.row, bc.column)) {
				boxOnGoalCount++;
			}
		}

		if (boxOnGoalCount == board.goalPositions.size()) {
			return true;
		}

		return false;
	}

	public boolean isOccupied(byte row, byte column) {
		return board.wallAt(row, column) || boxAt(row, column);
	}

	public byte boxesOnGoals() {
		byte sum = 0;
		for (BoardCoordinate boxCoordinate : boxCoordinates) {
			if (board.goalAt(boxCoordinate.row, boxCoordinate.column)) {
				sum++;
			}
		}

		return sum;
	}

	public Vector<BoardCoordinate> goalPositions() {
		return board.goalPositions;
	}

	public final void printState() {
		System.out.println(toString());
	}

	@Override
	public String toString() {
		byte[][] boardMatrix;
		boardMatrix = new byte[board.rows()][];
		for (byte i = 0; i < board.rows(); i++) {
			boardMatrix[i] = new byte[board.columns()];
			for (byte j = 0; j < board.columns(); j++) {
				boardMatrix[i][j] = board.dataAt(i, j);
			}
		}

		for (BoardCoordinate bc : boxCoordinates) {
			switch (boardMatrix[bc.row][bc.column]) {
			case Board.TYPE_FLOOR:
				boardMatrix[bc.row][bc.column] = '$';
				break;
			case Board.TYPE_GOAL:
				boardMatrix[bc.row][bc.column] = '*';
				break;
			}
		}

		switch (boardMatrix[playerCoordinate.row][playerCoordinate.column]) {
		case Board.TYPE_FLOOR:
			boardMatrix[playerCoordinate.row][playerCoordinate.column] = '@';
			break;
		case Board.TYPE_GOAL:
			boardMatrix[playerCoordinate.row][playerCoordinate.column] = '+';
			break;
		default:
			assert (false);
			break;
		}

		String representation = "";
		for (byte i = 0; i < board.rows(); i++) {
			for (byte j = 0; j < board.columns(); j++) {
				switch (boardMatrix[i][j]) {
				case Board.TYPE_FLOOR:
					representation += " ";
					break;
				case Board.TYPE_GOAL:
					representation += ".";
					break;
				case Board.TYPE_WALL:
					representation += "#";
					break;
				default:
					representation += (char) boardMatrix[i][j];
					break;
				}
			}
			representation += "\n";
		}

		return representation;
	}

	public final void possibleBoxMoves(Vector<BoardState> states) {
		states.clear();

		// perform BFS search from player position to find pushable boxes
		// return a list of states in which at least one box has moved

	}

	public final Vector<BoardState> possibleMoves(Vector<BoardState> states) {
		states.clear();
		for (byte move = 0; move < 4; move++) {
			BoardState bs = tryMove(move);
			if (bs != null) {
				states.add(bs);
			}
		}

		return null;
	}

	public final BoardState tryMove(byte direction) {
		BoardCoordinate pbc = playerCoordinate;

		// calculate adjacent square and next over depending on the direction
		byte rowDiff = 0, columnDiff = 0, rowNextOverDiff = 0, columnNextOverDiff = 0;
		switch (direction) {
		case MOVE_UP:
			rowDiff = -1;
			rowNextOverDiff = -2;
			break;
		case MOVE_DOWN:
			rowDiff = 1;
			rowNextOverDiff = 2;
			break;
		case MOVE_LEFT:
			columnDiff = -1;
			columnNextOverDiff = -2;
			break;
		case MOVE_RIGHT:
			columnDiff = 1;
			columnNextOverDiff = 2;
			break;
		}

		byte adjacentRow = (byte) (pbc.row + rowDiff);
		byte adjacentColumn = (byte) (pbc.column + columnDiff);

		if ((board.floorAt(adjacentRow, adjacentColumn) || board.goalAt(
				adjacentRow, adjacentColumn))
				&& !boxAt(adjacentRow, adjacentColumn)) {
			// there are no obstacles. the player can move without pushing a
			// box.
			BoardState bs = new BoardState(this, new BoardCoordinate(
					adjacentRow, adjacentColumn), null, null, direction);
			bs.parent = this;
			return bs;
		}

		byte nextOverRow = (byte) (pbc.row + rowNextOverDiff);
		byte nextOverColumn = (byte) (pbc.column + columnNextOverDiff);

		if (boxAt(adjacentRow, adjacentColumn)) {
			// there is a box in the direction the player want to move
			if (!board.wallAt(nextOverRow, nextOverColumn)
					&& !boxAt(nextOverRow, nextOverColumn)) {
				// there is free space behind the box, push is allowed
				BoardState bs = new BoardState(this, new BoardCoordinate(
						adjacentRow, adjacentColumn), new BoardCoordinate(
						adjacentRow, adjacentColumn), new BoardCoordinate(
						nextOverRow, nextOverColumn), direction);
				bs.parent = this;
				return bs;
			}
		}

		return null;
	}

	public int compareTo(BoardState rhs) {
		double thisVal = Heuristics.goalDistance(this);
		double rhsVal = Heuristics.goalDistance(rhs);

		if (thisVal > rhsVal)
			return 1;
		else if (thisVal < rhsVal)
			return -1;
		else
			return 0;
	}
}
