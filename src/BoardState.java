import java.util.Vector;

public class BoardState {
	public static final byte MOVE_DOWN = 1;
	public static final byte MOVE_LEFT = 2;
	public static final byte MOVE_NULL = -1;
	public static final byte MOVE_RIGHT = 3;
	public static final byte MOVE_UP = 0;
	
	public final Board board;
	public final Vector<BoardCoordinate> boxCoordinates;
	public final byte lastMove;
	public final BoardCoordinate playerCoordinate;

	public BoardState(Board board, BoardCoordinate playerCoordinate,
			Vector<BoardCoordinate> boxCoordinates, byte move) {
		this.board = board;
		this.playerCoordinate = playerCoordinate;
		this.boxCoordinates = boxCoordinates;
		this.lastMove = move;
	}

	public BoardState(BoardState aState, BoardCoordinate playerCoordinate,
			BoardCoordinate oldBox, BoardCoordinate newBox, byte move) {
		this.board = aState.board;
		this.playerCoordinate = playerCoordinate;
		this.lastMove = move;

		Vector<BoardCoordinate> bcs = new Vector<BoardCoordinate>();
		for (BoardCoordinate bc : bcs) {
			if (!bc.equals(oldBox)) {
				bcs.add(new BoardCoordinate(bc.row, bc.column));
			}
		}

		if (newBox != null) {
			bcs.add(newBox);
		}

		this.boxCoordinates = bcs;
	}

	public final boolean boxAt(byte row, byte column) {
		for (BoardCoordinate bc : boxCoordinates) {
			if (bc.row == row && bc.column == column) {
				return true;
			}
		}

		return false;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	public final boolean isSolved() {
		int boxOnGoalCount = 0;
		for (BoardCoordinate bc : this.boxCoordinates) {
			if (board.goalAt(bc.row, bc.column)) {
				boxOnGoalCount++;
			}
		}

		if (boxOnGoalCount >= board.goalPositions.size()) {
			return true;
		}

		return false;
	}

	public final Vector<BoardState> possibleMoves(Vector<BoardState> states) {
		states.clear();
		for (byte move = 0; move < 4; move++) {
			BoardState bs = tryMove(move, this);
			if (bs != null) {
				states.add(bs);
			}
		}

		return null;
	}

	public final void printState() {
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
		}
		
		for (byte i = 0; i < board.rows(); i++) {
			for (byte j = 0; j < board.columns(); j++) {
				switch (boardMatrix[i][j]) {
				case Board.TYPE_FLOOR:
					System.out.print(" ");
					break;
				case Board.TYPE_GOAL:
					System.out.print(".");
					break;
				case Board.TYPE_WALL:
					System.out.print("#");
					break;
				default:
					System.out.print((char) boardMatrix[i][j]);
					break;
				}
			}
			System.out.println("");
		}
	}

	@Override
	public String toString() {
		return String.format("State: playerCoordinate %s", playerCoordinate);
	}

	public final BoardState tryMove(byte direction, BoardState state) {
		BoardCoordinate pbc = state.playerCoordinate;

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

		if (board.floorAt(adjacentRow, adjacentColumn)
				|| board.goalAt(adjacentRow, adjacentColumn)) {
			// there are no obstacles. the player can move without pushing a
			// box.
			return new BoardState(state, new BoardCoordinate(adjacentRow,
					adjacentColumn), null, null, direction);
		}

		byte nextOverRow = (byte) (pbc.row + rowNextOverDiff);
		byte nextOverColumn = (byte) (pbc.column + columnNextOverDiff);

		if (boxAt(adjacentRow, adjacentColumn)) {
			// there is a box in the direction the player want to move
			if (board.floorAt(nextOverRow, nextOverColumn)
					|| board.goalAt(nextOverRow, nextOverColumn)) {
				// there is free space behind the box, move is allowed
				return new BoardState(state, new BoardCoordinate(adjacentRow,
						adjacentColumn), new BoardCoordinate(adjacentRow,
						adjacentRow), new BoardCoordinate(nextOverRow,
						nextOverColumn), direction);
			}
		}

		return null;
	}

}
