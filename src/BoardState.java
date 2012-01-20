import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
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
	public ArrayList<Move> backtrackMoves;

	public long hashCode = 0;

	public final long calculateHashCode() {
		long hash = board.zValues[playerCoordinate.row][playerCoordinate.column];
		for (BoardCoordinate bc : boxCoordinates) {
			hash ^= board.zValues[bc.row][bc.column];
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

	public BoardState(BoardState aState, BoardCoordinate playerCoordinate, BoardCoordinate oldBox,
			BoardCoordinate newBox, byte move) {
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

	public final void neighborBoxes(BoardCoordinate boxCoordinate, Vector<BoardCoordinate> list) {
		list.clear();
		if (boxAt((byte) (boxCoordinate.row + 1), boxCoordinate.column)) {
			list.add(new BoardCoordinate((byte) (boxCoordinate.row + 1), boxCoordinate.column));
		}
		if (boxAt((byte) (boxCoordinate.row - 1), boxCoordinate.column)) {
			list.add(new BoardCoordinate((byte) (boxCoordinate.row - 1), boxCoordinate.column));
		}
		if (boxAt(boxCoordinate.row, (byte) (boxCoordinate.column + 1))) {
			list.add(new BoardCoordinate(boxCoordinate.row, (byte) (boxCoordinate.column + 1)));
		}
		if (boxAt(boxCoordinate.row, (byte) (boxCoordinate.column - 1))) {
			list.add(new BoardCoordinate(boxCoordinate.row, (byte) (boxCoordinate.column - 1)));
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
		return state.hashCode == hashCode && state.playerCoordinate.equals(playerCoordinate)
				&& state.boxCoordinates.size() == boxCoordinates.size()
				&& boxCoordinates.containsAll(state.boxCoordinates)
				&& state.boxCoordinates.containsAll(boxCoordinates);
	}

	@Override
	public final int hashCode() {
		return (int) hashCode;
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
			boardMatrix[playerCoordinate.row][playerCoordinate.column] = '@';
			break;
		}

		String representation = "";
		for (byte i = 0; i < board.rows(); i++) {
			for (byte j = 0; j < board.columns(); j++) {
				switch (boardMatrix[i][j]) {
				case Board.TYPE_DEAD:
					representation += "x";
					break;
				case 0:
				case Board.TYPE_FLOOR:
					representation += " ";
					break;
				case Board.TYPE_GOAL:
					representation += ".";
					break;
				case Board.TYPE_WALL:
					representation += "#";
					break;
				case Board.TYPE_DEAD | Board.TYPE_FLOOR:
					representation += "x";
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
		final byte[] rowLookup = { 1, -1, 0, 0 };
		final byte[] columnLookup = { 0, 0, 1, -1 };

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
					if (!board.wallAt(nextOverRow, nextOverColumn)
							&& !boxAt(nextOverRow, nextOverColumn)) {
						if (board.deadAt(nextOverRow, nextOverColumn)) {
							continue;
						}

						BoardCoordinate newPlayerCoordinate = new BoardCoordinate(examinedRow,
								examinedColumn);
						BoardCoordinate oldBox = new BoardCoordinate(examinedRow, examinedColumn);
						BoardCoordinate newBox = new BoardCoordinate(nextOverRow, nextOverColumn);

						BoardState newBoardState = new BoardState(this, newPlayerCoordinate,
								oldBox, newBox, (byte) i);
						newBoardState.parent = this;

						ArrayList<Move> moves = new ArrayList<Move>();
						moves.add(new Move((byte) i));
						backtrack(moves, row, column, this.playerCoordinate);

						newBoardState.backtrackMoves = moves;

						// check if new state is a no influence
						while (newBoardState.isNoInfluence()) {
							newPlayerCoordinate = new BoardCoordinate(newBox.row, newBox.column);
							oldBox = new BoardCoordinate(newBox.row, newBox.column);
							newBox = new BoardCoordinate((byte) (newBox.row + rowDiffs[i]),
									(byte) (newBox.column + columnDiffs[i]));

							// check if additional push is allowed
							if (!board.wallAt(newBox.row, newBox.column)
									&& !newBoardState.boxAt(newBox.row, newBox.column)) {
								if (board.deadAt(newBox.row, newBox.column)) {
									break;
								}

								newBoardState = new BoardState(newBoardState, newPlayerCoordinate,
										oldBox, newBox, (byte) i);
								newBoardState.parent = this;

								moves.add(0, new Move((byte) i));
								newBoardState.backtrackMoves = moves;
							} else {
								break;
							}
						}

						if (!DeadlockFinder.isDeadLock(newBoardState))
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

	/* Test if a box on position 'start' can reach some position 'end'. */
	public boolean isReachable(BoardCoordinate start, BoardCoordinate end) {
		HashSet<BoardCoordinate> visited = new HashSet<BoardCoordinate>();
		LinkedList<BoardCoordinate> queue = new LinkedList<BoardCoordinate>();
		//public final Vector<BoardCoordinate> boxCoordinates;
		BoardCoordinate save = start;
		boxCoordinates.remove(start);
		
		queue.push(start);
		visited.add(start);
		//System.out.println("(isReachable) trying (start, stop)" + start + " " + end);

		while (!queue.isEmpty()) {
			BoardCoordinate currentNode = queue.pop();
			// Mask for adjacent (possible) positions this box can be pushed to.
			final byte[] rowDiffs = { -1, 1, 0, 0 };
			final byte[] columnDiffs = { 0, 0, -1, 1 };
			final byte[] playerRowDiffs = { 1, -1, 0, 0 };
			final byte[] playerColDiffs = { 0, 0, 1, -1 };

			for (int i = 0; i < 4; i++) {

				byte examinedRow = (byte) (currentNode.row + rowDiffs[i]);
				byte examinedColumn = (byte) (currentNode.column + columnDiffs[i]);
				BoardCoordinate nextNode = new BoardCoordinate(examinedRow, examinedColumn);
				
				byte playerRow = (byte) (currentNode.row + playerRowDiffs[i]);
				byte playerColumn = (byte) (currentNode.column + playerColDiffs[i]);
				//System.out.println(examinedRow + " " + examinedColumn + " " + playerRow + " " + playerColumn);

				if (!visited.contains(nextNode)) {
					visited.add(nextNode);
					if (board.wallAt(nextNode.row, nextNode.column) || (boxAt(nextNode.row, nextNode.column) && board.goalAt(nextNode.row, nextNode.column) && DeadlockFinder.isMovable(this, nextNode))) {
						//System.out.println("Parent: " + currentNode + " detected deadspot at child: " + nextNode);
					} else {
						// test if player can push
						if (!isOccupied(playerRow, playerColumn) || !boxAt(playerRow, playerColumn)) {
							if (end.equals(nextNode)) {
								//System.out.println("Destination reached. " + nextNode);
								boxCoordinates.add(save);
								return true;
							}
	
							queue.push(nextNode);
						}
					}
				}
			}

		}
		boxCoordinates.add(save);
		//System.out.println("Unreachable");
		return false;
	}

	public boolean isNoInfluence() {
		BoardCoordinate box = boxCoordinates.lastElement();
		if (board.goalAt(box.row, box.column)) {
			return false;
		}

		byte[] leftOfPlayerRow = { 0, 0, 1, -1 };
		byte[] leftOfPlayerCol = { -1, 1, 0, 0 };
		byte[] rightOfPlayerRow = { 0, 0, -1, 1 };
		byte[] rightOfPlayerCol = { 1, -1, 0, 0 };

		byte lm = this.lastMove;
		BoardCoordinate pc = playerCoordinate;
		if (board.wallAt((byte) (leftOfPlayerRow[lm] + pc.row),
				(byte) (leftOfPlayerCol[lm] + pc.column))
				&& board.wallAt((byte) (rightOfPlayerRow[lm] + pc.row),
						(byte) (rightOfPlayerCol[lm] + pc.column))) {
			byte[] fwRow = { -1, 1, 0, 0 };
			byte[] fwCol = { 0, 0, -1, 1 };
			if (board.wallAt((byte) (pc.row + leftOfPlayerRow[lm] + fwRow[lm]), (byte) (pc.column
					+ leftOfPlayerCol[lm] + fwCol[lm]))
					|| board.wallAt((byte) (pc.row + rightOfPlayerRow[lm] + fwRow[lm]),
							(byte) (pc.column + rightOfPlayerCol[lm] + fwCol[lm]))) {
				// System.out.println("Found noInfluence state");
				return true;
			}
		}

		return false;
	}

	public int compareTo(BoardState rhs) {
		double thisVal = Solver.heuristicsScore.get(this);
		double rhsVal = Solver.heuristicsScore.get(rhs);

		if (thisVal > rhsVal)
			return 1;
		else if (thisVal < rhsVal)
			return -1;
		else
			return 0;
	}
	
	
}
