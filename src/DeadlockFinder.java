
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
	// private boolean isBD(BoardState state) {
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


	public static boolean isFreezeDeadlock(BoardState state) {
		if (!isPotentialFreezeState(state)) {
			return false;
		}
		
		BoardCoordinate startBox = state.boxCoordinates.lastElement();
		
		LinkedList<BoardCoordinate> queue = new LinkedList<BoardCoordinate>();
		HashSet<BoardCoordinate> visited = new HashSet<BoardCoordinate>();
		queue.add(startBox);
		visited.add(startBox);
		
		BoardCoordinate currentBox;
		while((currentBox = queue.poll()) != null) {
			if (isMovable(state, currentBox)) {
				return false;
			} else {
				Vector<BoardCoordinate> neighbours = new Vector<BoardCoordinate>();
				state.neighborBoxes(currentBox, neighbours);
				for (BoardCoordinate bc : neighbours) {
					if (!visited.contains(bc)) {
						queue.add(bc);
						visited.add(bc);
					}
				}
			}
		}
		
		// No possible moves found, deadlock detected
		return true;
	}

	private static boolean isPotentialFreezeState(BoardState state) {
		byte row = state.boxCoordinates.lastElement().row;
		byte column = state.boxCoordinates.lastElement().column;
	
		if (state.boxAt((byte)(row-1), column) && !state.board.goalAt((byte)(row-1), column)) {
			return true;
		}
		if (state.boxAt((byte)(row+1), column) && !state.board.goalAt((byte)(row+1), column)) {
			return true;
		}
		if (state.boxAt(row, (byte)(column-1)) && !state.board.goalAt(row, (byte)(column-1))) {
			return true;
		}
		if (state.boxAt(row, (byte)(column+1)) && !state.board.goalAt(row, (byte)(column+1))) {
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
