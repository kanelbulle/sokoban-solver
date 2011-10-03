import java.util.ArrayList;
import java.util.List;
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
	public List<Move> backtrackMoves;
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
	
	public class Move {
		public final byte move;
		
		public Move(byte move) {
			this.move = move;
		}
	}
	
	private static int[] visited = new int[10000];
	private static int visitedIdentifier = 0;
	private static int[] movesQueue = new int[10000];
	private static byte[] backtrack = new byte[10000];

	private final int indexOfCoordinate(byte row, byte column) {
		return 100 * row + column;
	}

	private final void backtrack(List<Move> moves, byte row, byte column, BoardCoordinate start) {
		final byte[] rowLookup = {1, -1, 0, 0};
		final byte[] columnLookup = {0, 0, 1, -1};
		
		while (row != start.row || column != start.column) {
			byte move = backtrack[indexOfCoordinate(row, column)];
			row += rowLookup[move];
			column += columnLookup[move];
			
			moves.add(new Move(move));
		}
	}
	
	public final void possibleBoxMoves(Vector<BoardState> states) {
		states.clear();

		// perform BFS search from player position to find pushable boxes
		// return a list of states in which at least one box has moved
		
		visitedIdentifier++;

		int queueStart = 0;
		movesQueue[queueStart] = indexOfCoordinate(playerCoordinate.row, playerCoordinate.column);
		visited[movesQueue[queueStart]] = visitedIdentifier;
		int queueEnd = queueStart;

		do {
			// look at first position in queue
			int position = movesQueue[queueStart++];
			byte row = (byte) (position / 100);
			byte column = (byte) (position - row * 100);

			// check if there are adjacent boxes that can be pushed
			final byte[] rowDiffs = { -1, 1, 0, 0 };
			final byte[] columnDiffs = { 0, 0, -1, 1 };

			// loop through moves
			for (int i = 0; i < 4; i++) {
				byte examinedRow = (byte) (row + rowDiffs[i]);
				byte examinedColumn = (byte) (column + columnDiffs[i]);

				// if there is a box at the examined position
				if (boxAt(examinedRow, examinedColumn)) {
					byte nextOverRow = (byte) (examinedRow + rowDiffs[i]);
					byte nextOverColumn = (byte) (examinedColumn + columnDiffs[i]);

					// if there is no wall or box, push is allowed
					if (!board.wallAt(nextOverRow, nextOverColumn) && !boxAt(nextOverRow, nextOverColumn)) {
						BoardCoordinate newPlayerCoordinate = new BoardCoordinate(examinedRow, examinedColumn);
						BoardCoordinate oldBox = new BoardCoordinate(examinedRow, examinedColumn);
						BoardCoordinate newBox = new BoardCoordinate(nextOverRow, nextOverColumn);
						
						BoardState newBoardState = new BoardState(this, newPlayerCoordinate, oldBox, newBox, (byte) i);
						newBoardState.parent = this;
						
						ArrayList<Move> moves = new ArrayList<Move>();
						moves.add(new Move((byte) i));
						backtrack(moves, row, column, this.playerCoordinate);
						
						newBoardState.backtrackMoves = moves;
						states.add(newBoardState);
					}
				} else if (!board.wallAt(examinedRow, examinedColumn)) { 
					// no wall, no box: queue this position
					int index = indexOfCoordinate(examinedRow, examinedColumn);
					if (visited[index] != visitedIdentifier) {
						// has not been visited, queue it
						movesQueue[++queueEnd] = index;
						// mark as visited
						visited[index] = visitedIdentifier;
						backtrack[index] = (byte) i;
					}
				}
			}
		} while (queueStart <= queueEnd);
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
