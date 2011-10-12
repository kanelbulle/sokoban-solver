
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

public class DeadlockFinder {

	// //TODO do this
	// private boolean isCD(BoardState state) {
	// boolean isDead = true;
	// return isDead;
	// }
	//
	// //TODO do this
	// private boolean isDDTFB(BoardState state) {
	// boolean isDead = true;
	// return isDead;
	// }

//	private static DeadlockFinder instance = null;
//	
//	private DeadlockFinder() {}
//	
//	public static DeadlockFinder getInstance() {
//		if(instance == null) {
//			instance = new DeadlockFinder();
//		}
//		return instance;
//	}
	
	public static boolean isDeadLock(BoardState state) {
		return isFreezeDeadlock(state);
		//return isBipartiteMatchDeadlock(state);
		//return (isFreezeDeadlock(state) || isBipartiteMatchDeadlock(state));
	}


	// Lemma: Search for alternating path from unmatched node in X to unmatched in Y,
	// if exists then there exists a matching M' with cardinality |M|+1.
	public static boolean isBipartiteMatchDeadlock(BoardState state) {
		//System.out.println("adfadsf");
		final int START = 0;
		final int END = 1;
		
		Vector<BoardCoordinate> X = new Vector<BoardCoordinate>(state.boxCoordinates);
		Vector<BoardCoordinate> Y = new Vector<BoardCoordinate>(state.goalPositions());
		HashMap<BoardCoordinate, Vector<BoardCoordinate>> edges = new HashMap<BoardCoordinate, Vector<BoardCoordinate>>();
		BoardCoordinate endNodes[] = new BoardCoordinate[2];
		HashMap<BoardCoordinate, BoardCoordinate> path = new HashMap<BoardCoordinate, BoardCoordinate>();
		
		// Remove matched goals/boxes, must use outer init var to prevent ConcurrentModificationException
		int tmpSize = X.size();
		for (int i = 0; i < tmpSize; i++) {
			BoardCoordinate x = X.get(i);
			if (state.board.goalAt(x.row, x.column)) {
				Y.remove(x);
				X.remove(x);
				--tmpSize;
				//System.out.println("Removing already filled goal: " + x + " result: " + Y);
			}
		}
		
		// Creates all reachable (directed) edges (x,y) = (box, goal)
		for (BoardCoordinate x : X) {
			// Skip boxes at goal positions, where are only interested if the res of the boxes can reach the other goals.
			if (state.board.goalAt(x.row, x.column)) { continue; }
			
			Vector<BoardCoordinate> xDirected = new Vector<BoardCoordinate>();
			for (BoardCoordinate y : Y) {
				if (!state.boxAt(y.row, y.column)) { 
					if (state.isReachable(x, y)) {					
						//System.out.println("Adding edge: " +x+" " +y);
						xDirected.add(y);
					}
				}
			}
			
			edges.put(x, xDirected);
		}
		//System.out.println("New edges: " + edges);
		
		
		while (true) {
			boolean altPathExists = findAlternatingPath(path, endNodes, X, Y, edges);
			//System.out.println(edges);	
			// Improve matching -> Remove matched x's from X, and y's from Y and redirect edges from alternating path.
			if (altPathExists) {
				BoardCoordinate currentNode = endNodes[END];
				//System.out.println("Start/End: " + endNodes[START] + "/" + endNodes[END]);
				//System.out.println(currentNode);
				do {
					BoardCoordinate nextNode = path.get(currentNode);
					//System.out.println(nextNode);
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
					X.remove(currentNode);
					currentNode = path.get(currentNode);
					
					if (currentNode == null) { break; }
					
				} while (!currentNode.equals(endNodes[START]));
			} else {
				break;
			}
			//System.out.println(edges);
		}

		// If size(X) == 0 it means that all nodes (in X which is equals to Y)
		// have been matched == no deadlock.
		if (X.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/* Search for alternating path in G
	Do a BFS for each unmatched node in X untill a unmatched node in Y is found. */
	private static boolean findAlternatingPath( HashMap<BoardCoordinate, BoardCoordinate> path,
												BoardCoordinate[] endNodes,
												Vector<BoardCoordinate> unmatchedX,
												Vector<BoardCoordinate> Y,
												HashMap<BoardCoordinate, Vector<BoardCoordinate>> edges) {
		final int START = 0;
		final int END = 1;
		
		path.clear();
//System.out.println("In findAlt: X is: " + unmatchedX);
		for (BoardCoordinate x : unmatchedX) {	
			endNodes[START] = x;
			
			// BFS Step
			LinkedList<BoardCoordinate> queue = new LinkedList<BoardCoordinate>();
			queue.push(x);

			while(!queue.isEmpty()) {
				BoardCoordinate parent = queue.pop();

				// "Goal test" if y belongs to Y = alternating path found!
				if (Y.contains(parent)) {
					endNodes[END] = parent;
					// Alternating path found!
					//System.out.println("Alternating path found!");
					return true;
				}
				
				//System.out.println("parent " + parent + " edges: " + edges);
				for (BoardCoordinate child : edges.get(parent)) {
					path.put(child, parent);
					queue.push(child);
				}
			}
		}
		
		// No alt path found. Matching is maximum
		//System.out.println("No alt path found. Matching is maximum");
		return false;
	}

	public static boolean isFreezeDeadlock(BoardState state) {
		if (!isPotentialFreezeState(state)) {
			return false;
		}

		BoardCoordinate startBox = state.boxCoordinates.lastElement();

		LinkedList<BoardCoordinate> queue = new LinkedList<BoardCoordinate>();
		HashSet<BoardCoordinate> visited = new HashSet<BoardCoordinate>();
		queue.add(startBox);
		visited.add(startBox);
		Vector<BoardCoordinate> neighbours = new Vector<BoardCoordinate>();

		BoardCoordinate currentBox;
		while((currentBox = queue.poll()) != null) {
			if (isMovable(state, currentBox)) {
				//System.out.println(" ...no");
				return false;
			} else {
				state.neighborBoxes(currentBox, neighbours);
				for (BoardCoordinate bc : neighbours) {
					if (!visited.contains(bc)) {
						queue.add(bc);
						visited.add(bc);
					}
				}
			}
		}
		//System.out.println(" ...YES!");
		// No possible moves found, deadlock detected
		return true;
	}

	// NOT USED, DONT REMOVE, DONT MESS WITH THE ZOHAN! 
	/* * = currentBox (currently being pushed, $ = oldBox (push to state previously
	 * Tests are carried out counter clockwise from currentBox */
//	private static boolean isFreezeSpecialCase(BoardState state) {
//		byte row = state.boxCoordinates.lastElement().row;
//		byte column = state.boxCoordinates.lastElement().column;
//
//		// * $
//		// $ $
//		if (state.boxAt(row, (byte)(column+1)) && state.boxAt((byte)(row-1), column) && state.boxAt((byte)(row-1), (byte)(column+1))) {
//			if (!state.board.goalAt(row, (byte)(column+1)) && !state.board.goalAt((byte)(row-1), column) && !state.board.goalAt((byte)(row-1), (byte)(column+1))) {
//				return true;
//			}
//		}
//
//		// $ *
//		// $ $
//		if (state.boxAt((byte)(row-1), column) && state.boxAt((byte)(row-1), (byte)(column-1)) && state.boxAt(row, (byte)(column-1))) {
//			if (!state.board.goalAt((byte)(row-1), column) && !state.board.goalAt((byte)(row-1), (byte)(column-1)) && !state.board.goalAt(row, (byte)(column-1))) {
//				return true;
//			}
//		}
//
//		// $ $
//		// $ *
//		if (state.boxAt(row, (byte)(column-1)) && state.boxAt((byte)(row-1), (byte)(column-1)) && state.boxAt((byte)(row-1), column)) {
//			if (!state.board.goalAt(row, (byte)(column-1)) && !state.board.goalAt((byte)(row-1), (byte)(column-1)) && !state.board.goalAt((byte)(row-1), column)) {
//				return true;
//			}
//		}
//
//		// $ $
//		// * $
//		if (state.boxAt((byte)(row-1), column) && state.boxAt((byte)(row-1), (byte)(column+1)) && state.boxAt(row, (byte)(column+1))) {
//			if (!state.board.goalAt((byte)(row-1), column) && !state.board.goalAt((byte)(row-1), (byte)(column+1)) && !state.board.goalAt(row, (byte)(column+1))) {
//				return true;
//			}
//		}
//
//		return false;
//	}


	private static boolean isPotentialFreezeState(BoardState state) {
		byte row = state.boxCoordinates.lastElement().row;
		byte column = state.boxCoordinates.lastElement().column;

		if (state.boxAt((byte)(row-1), column) && !state.board.goalAt((byte)(row-1), column)) {
			//System.out.print("case 1 (" +row+ "," + column +")");
			return true;
		}
		if (state.boxAt((byte)(row+1), column) && !state.board.goalAt((byte)(row+1), column)) {
			//System.out.print("case 2 (" +row+ "," + column +")");
			return true;
		}
		if (state.boxAt(row, (byte)(column-1)) && !state.board.goalAt(row, (byte)(column-1))) {
			//System.out.print("case 3 (" +row+ "," + column +")");
			return true;
		}
		if (state.boxAt(row, (byte)(column+1)) && !state.board.goalAt(row, (byte)(column+1))) {
			//System.out.print("case 4 (" +row+ "," + column +")");
			return true;
		}

		return false;
	}

	/* A box is deadlocked if it is blocked from at least one horizontal and one vertical direction at the same time */ 
	private static boolean isMovable(BoardState state, BoardCoordinate currentBox) {
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
