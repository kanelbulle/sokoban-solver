import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

public class DeadlockFinder {

	public static boolean isDeadLock(BoardState state) {
		return (isBipartiteMatchDeadlock(state) || isFreezeDeadlock(state));
		//return isFreezeDeadlock(state);
	}

	// Lemma: Search for alternating path from unmatched node in X to unmatched
	// in Y,
	// if exists then there exists a matching M' with cardinality |M|+1.
	public static boolean isBipartiteMatchDeadlock(BoardState state) {
		System.out.println("Testing board: "); state.printState();
		final int START = 0;
		final int END = 1;

		Vector<BoardCoordinate> X = new Vector<BoardCoordinate>(state.boxCoordinates);
		Vector<BoardCoordinate> Y = new Vector<BoardCoordinate>(state.goalPositions());
		HashMap<BoardCoordinate, Vector<BoardCoordinate>> edges = new HashMap<BoardCoordinate, Vector<BoardCoordinate>>();
		BoardCoordinate endNodes[] = new BoardCoordinate[2];
		HashMap<BoardCoordinate, BoardCoordinate> path = new HashMap<BoardCoordinate, BoardCoordinate>();

		// Remove matched goals/boxes, must use outer init var to prevent
		// ConcurrentModificationException
		int tmpSize = X.size();
		for (int i = 0; i < tmpSize; i++) {
			BoardCoordinate x = X.get(i);
			if (state.board.goalAt(x.row, x.column) ){//&& isMovable(state, x)) {
				Y.remove(x);
				X.remove(x);
				--tmpSize;
				//System.out.println("Removing already filled goal that cant be moved: " + x + " result: " + Y);
			}
		}

		// Creates all reachable (directed) edges (x,y) = (box, goal)
		for (BoardCoordinate x : X) {
			// Skip boxes at goal positions, where are only interested if the
			// res of the boxes can reach the other goals.
			if (state.board.goalAt(x.row, x.column)) {
				continue;
			}

			Vector<BoardCoordinate> xDirected = new Vector<BoardCoordinate>();
			for (BoardCoordinate y : Y) {
				//if (!state.boxAt(y.row, y.column)) {
					if (state.isReachable(x, y)) {
						//System.out.println("Adding edge: " +x+" " +y);
						xDirected.add(y);
					}
				//}
			}

			edges.put(x, xDirected);
		}
		
		//System.out.println("New edges: " + edges);

		while (true) {
			boolean altPathExists = findAlternatingPath(path, endNodes, X, Y, edges);
			//System.out.println("Path found: " + path);
			// Improve matching -> Remove matched x's from X, and y's from Y and
			// redirect edges from alternating path.
			if (altPathExists) {
				BoardCoordinate currentNode = endNodes[END];
				// System.out.println("Start/End: " + endNodes[START] + "/" +
				// endNodes[END]);
				// System.out.println(currentNode);
				do {
					BoardCoordinate nextNode = path.get(currentNode);
					// System.out.println(nextNode);
					if (!edges.containsKey(currentNode)) {
						Vector<BoardCoordinate> yEdges = new Vector<BoardCoordinate>();
						yEdges.add(nextNode);
						edges.put(currentNode, yEdges);
					} else if (!edges.get(currentNode).contains(nextNode)) {
						edges.get(currentNode).add(nextNode);
					}

					if (edges.containsKey(nextNode)) {
						edges.get(nextNode).remove(currentNode);
					}

					X.remove(nextNode);
					//Y.remove(currentNode);
					//Y.remove(nextNode);
					//X.remove(currentNode);
					currentNode = path.get(currentNode);

					if (currentNode == null) {
						break;
					}

				} while (!currentNode.equals(endNodes[START]));
			} else {
				break;
			}
			//System.out.println(edges);
		}

		// If size(X) == 0 it means that all nodes (in X which is equals to Y)
		// have been matched == no deadlock.
		if (X.size() == 0) {
			System.out.println("State NOT deadlocked.");
			return false;
		} else {
			System.out.println("State IS deadlocked." + X);
			return true;
		}
	}

	/*
	 * Search for alternating path in G Do a BFS for each unmatched node in X
	 * untill a unmatched node in Y is found.
	 */
	private static boolean findAlternatingPath( HashMap<BoardCoordinate, BoardCoordinate> path,
												BoardCoordinate[] endNodes, Vector<BoardCoordinate> unmatchedX,
												Vector<BoardCoordinate> Y, HashMap<BoardCoordinate, Vector<BoardCoordinate>> edges) {
		final int START = 0;
		final int END = 1;

		path.clear();
		// System.out.println("In findAlt: X is: " + unmatchedX);
		for (BoardCoordinate x : unmatchedX) {
			endNodes[START] = x;

			// BFS Step
			LinkedList<BoardCoordinate> queue = new LinkedList<BoardCoordinate>();
			queue.push(x);

			while (!queue.isEmpty()) {
				BoardCoordinate parent = queue.pop();

				//System.out.println("parent " + parent + " edges: " + edges);
				for (BoardCoordinate child : edges.get(parent)) {
					path.put(child, parent);
					queue.push(child);
					
					// "Goal test" if y belongs to Y = alternating path found!
					if (Y.contains(child) || unmatchedX.contains(child)) {
						endNodes[END] = parent;
						// Alternating path found!
						//System.out.println("Alternating path found! " + path);
						return true;
					}
				}
			}
		}

		// No alt path found. Matching is maximum
		System.out.println("No alt path found. Matching is maximum");
		return false;
	}

	public static boolean isFreezeDeadlock(BoardState state) {
		return isNewFreezeDeadlock(state);
	}

	private static boolean isPotentialFreezeState(BoardState state) {
		byte row = state.boxCoordinates.lastElement().row;
		byte column = state.boxCoordinates.lastElement().column;

		if (state.boxAt((byte) (row - 1), column) && !state.board.goalAt((byte) (row - 1), column)) {
			// System.out.print("case 1 (" +row+ "," + column +")");
			return true;
		}
		if (state.boxAt((byte) (row + 1), column) && !state.board.goalAt((byte) (row + 1), column)) {
			// System.out.print("case 2 (" +row+ "," + column +")");
			return true;
		}
		if (state.boxAt(row, (byte) (column - 1)) && !state.board.goalAt(row, (byte) (column - 1))) {
			// System.out.print("case 3 (" +row+ "," + column +")");
			return true;
		}
		if (state.boxAt(row, (byte) (column + 1)) && !state.board.goalAt(row, (byte) (column + 1))) {
			// System.out.print("case 4 (" +row+ "," + column +")");
			return true;
		}

		return false;
	}

	private static byte[] frozenBoxes = new byte[100];
	private static int frozenEnd = 0;

	public static boolean isNewFreezeDeadlock(BoardState state) {
		if (!isPotentialFreezeState(state)) {
			return false;
		}

		BoardCoordinate box = state.boxCoordinates.lastElement();

		frozenEnd = 0;
		boolean frozen = isFreezePrivate(state, box.row, box.column);
		if (frozen) {
			for (int i = 0; i < frozenEnd; i += 2) {
				if (!state.board.goalAt(frozenBoxes[i], frozenBoxes[i + 1])) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean isFreezePrivate(BoardState state, byte row, byte column) {
		boolean blockedHorizontally = false;
		boolean blockedVertically = false;
		byte backup = state.board.boardData[row][column];
		state.board.boardData[row][column] = Board.TYPE_WALL;

		if (blockedHorizontally(state, row, column)) {
			blockedHorizontally = true;
		}
		if (!blockedHorizontally && state.boxAt(row, (byte) (column - 1))) {
			blockedHorizontally = isFreezePrivate(state, row, (byte) (column - 1));
		}
		if (!blockedHorizontally && state.boxAt(row, (byte) (column + 1))) {
			blockedHorizontally = isFreezePrivate(state, row, (byte) (column + 1));
		}

		if (blockedVertically(state, row, column)) {
			blockedVertically = true;
		}
		if (!blockedVertically && state.boxAt((byte) (row - 1), column)) {
			blockedVertically = isFreezePrivate(state, (byte) (row - 1), column);
		}
		if (!blockedVertically && state.boxAt((byte) (row + 1), column)) {
			blockedVertically = isFreezePrivate(state, (byte) (row + 1), column);
		}

		state.board.boardData[row][column] = backup;

		if (blockedHorizontally && blockedVertically) {
			frozenBoxes[frozenEnd++] = row;
			frozenBoxes[frozenEnd++] = column;

			return true;
		}

		return false;
	}

	private static final boolean blockedVertically(BoardState state, byte row, byte column) {
		if (state.board.wallAt((byte) (row - 1), column)
				|| state.board.wallAt((byte) (row + 1), column)) {
			return true;
		}

		if (state.board.deadAt((byte) (row - 1), column)
				&& state.board.deadAt((byte) (row + 1), column)) {
			return true;
		}

		return false;
	}

	private static final boolean blockedHorizontally(BoardState state, byte row, byte column) {
		if (state.board.wallAt(row, (byte) (column - 1))
				|| state.board.wallAt(row, (byte) (column + 1))) {
			return true;
		}

		if (state.board.deadAt(row, (byte) (column - 1))
				&& state.board.deadAt(row, (byte) (column + 1))) {
			return true;
		}

		return false;
	}

	/* A box is deadlocked if it is blocked from at least one horizontal and one vertical direction at the same time */ 
	public static boolean isMovable(BoardState state, BoardCoordinate currentBox) {
		byte row = currentBox.row;
		byte column = currentBox.column;	

		if ((state.isOccupied((byte)(row-1), column) || state.board.deadAt((byte)(row-1), column)) && (state.isOccupied(row, (byte)(column-1)) || state.board.deadAt(row, (byte)(column-1)))) {
			return false;
		}
		if ((state.isOccupied(row, (byte)(column-1)) || state.board.deadAt(row, (byte)(column-1))) && (state.isOccupied((byte)(row+1), column) || state.board.deadAt((byte)(row+1), column)))  {
			return false;
		}
		if ((state.isOccupied((byte)(row+1), column) || state.board.deadAt((byte)(row+1),column)) && (state.isOccupied(row, (byte)(column+1)) || state.board.deadAt(row, (byte)(column+1)))) {
			return false;
		}
		if ((state.isOccupied(row, (byte)(column+1)) || state.board.deadAt(row, (byte)(column+1))) && (state.isOccupied((byte)(row-1), column) || state.board.deadAt((byte)(row-1), column))) {
			return false;
		}

		return true;
	}
}
