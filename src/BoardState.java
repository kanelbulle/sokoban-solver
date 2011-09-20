import java.util.Vector;

public class BoardState {
	public final Board board;
	public final BoardCoordinate playerCoordinate;
	public final Vector<BoardCoordinate> boxCoordinates;

	public static final byte MOVE_UP = 0;
	public static final byte MOVE_DOWN = 1;
	public static final byte MOVE_LEFT = 2;
	public static final byte MOVE_RIGHT = 3;

	public BoardState(Board board, BoardCoordinate playerCoordinate,
			Vector<BoardCoordinate> boxCoordinates) {
		this.board = board;
		this.playerCoordinate = playerCoordinate;
		this.boxCoordinates = boxCoordinates;
	}

	public BoardState(BoardState aState, BoardCoordinate playerCoordinate,
			BoardCoordinate oldBox, BoardCoordinate newBox) {
		this.board = aState.board;
		this.playerCoordinate = playerCoordinate;

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
		for (BoardCoordinate bc : this.boxCoordinates) {
			if (board.dataAt(bc.row, bc.column) != Board.TYPE_BOX_ON_GOAL
					&& board.dataAt(bc.row, bc.column) != Board.TYPE_GOAL_SQUARE
					&& board.dataAt(bc.row, bc.column) != Board.TYPE_PLAYER_ON_GOAL) {
				return false;
			}
		}

		return true;
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
		byte nextOverRow = (byte) (pbc.row + rowNextOverDiff);
		byte nextOverColumn = (byte) (pbc.column + columnNextOverDiff);
		byte adjacentSquare = board.dataAt(adjacentRow, adjacentColumn);
		byte nextOverSquare = board.dataAt(nextOverRow, nextOverColumn);

		if (adjacentSquare == Board.TYPE_FLOOR
				|| adjacentSquare == Board.TYPE_GOAL_SQUARE) {
			// there are no obstacles. the player can move.
			return new BoardState(state, new BoardCoordinate(adjacentRow,
					adjacentColumn), null, null);
		}

		if (boxAt(adjacentRow, adjacentColumn)) {
			// there is a box in the direction the player want to move
			if (nextOverSquare == Board.TYPE_FLOOR
					|| nextOverSquare == Board.TYPE_GOAL_SQUARE) {
				// there is free space behind the box, move is allowed
				return new BoardState(state, new BoardCoordinate(adjacentRow,
						adjacentColumn), new BoardCoordinate(adjacentRow,
						adjacentRow), new BoardCoordinate(nextOverRow,
						nextOverColumn));
			}
		}

		return null;
	}

}
