
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
	
	public static boolean isDeadLock(BoardState state) {
		return isFreezeDeadlock(state);
	}


	 private boolean isBipartiteMatchDeadlock(BoardState state) {
		 
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
		
		if (state.isOccupied((byte)(row-1), column) && state.isOccupied(row, (byte)(column-1))) {
			return false;
		}
		if (state.isOccupied(row, (byte)(column-1)) && state.isOccupied((byte)(row+1), column))  {
			return false;
		}
		if (state.isOccupied((byte)(row+1), column) && state.isOccupied(row, (byte)(column+1))) {
			return false;
		}
		if (state.isOccupied(row, (byte)(column+1)) && state.isOccupied((byte)(row-1), column)) {
			return false;
		}

		return true;
	}

}
