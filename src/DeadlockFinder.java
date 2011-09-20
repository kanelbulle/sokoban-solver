import java.util.ArrayList;

public class DeadlockFinder {

	/* The following will be replaced by the real variables, provided by Emil */
	//begin temporary
	byte IS_BOX = 0x23;
	byte IS_GOAL = 0x24;
	byte IS_FLOOR = 0x25;
	byte IS_BOX_ON_GOAL = 0x26;
	byte IS_WALL = 0x27;
	byte IS_PLAYER = 0x28;
	byte IS_NOTHING = 0x29;
	//end temporary
	
	private class Board {
		byte atPos(Pos currPos) {
			return 0x11;
		}
	};
	
	private class Pos{
		int row;
		int col;
		public Pos(int row, int col) {
			this.row = row;
			this.col = col;
		}
	};

	/**
	 * This method receives an instance of class Board and for that Board
	 * determines whether it is a deadlocked board or not, i.e. if it is
	 * possible to solve the game from that state.
	 * 
	 * According to the sokobano.de wiki there are five different kinds of
	 * deadlocks, see http://sokobano.de/wiki/index.php?title=Deadlocks for
	 * details.
	 * 
	 * 1) DSD "Dead square deadlocks" - the box moving cannot be undone, box
	 * can't be pushed to any goal.
	 * 
	 * 2) FD "Freeze deadlocks" - the box has become immovable due to e.g.
	 * adjacent boxes.
	 * 
	 * NOTE: Here we have combined the check for DSD and FD into one method.
	 * 
	 * 3) CD "Corral [sic] deadlocks" - boxes close off part of the level, that
	 * now has become unreachable.
	 * 
	 * 4) BD "Bipartite deadlocks" - wrong box has been pushed to the goal, one
	 * of the boxes left cannot be pushed to any goal, hence a goal will be
	 * without a free box.
	 * 
	 * 5) DDTFB "Deadlocks due to frozen boxes" - Frozen boxes don't create a
	 * Freeze deadlock when being located on a goal. Nevertheless they may
	 * influence the reachable area of other boxes.
	 * 
	 * @param Board to determine if deadlocked or not
	 * @return Returns "True" if the board is deadlocked.
	 */
	public boolean isDeadLock(Board pBoard) {
		return (isDSAFD(pBoard) || isCD(pBoard) || isBD(pBoard) || isDDTFB(pBoard));
	}
	
	
	/**
	 * This is an essential help method for at least isDSD (help method to isDeadLock).
	 * @param Pos is the position of the box to check if deadlocked.
	 * @return Returns TRUE if the box is immovable
	 */
	private boolean isBoxImmovable(Pos boxPos) {
		
		int freeAdjacentPosCount = 0;
		
		//TODO do this!
		
		return true;
	}

	/**
	 * "DSAFD" stands for "Dead square and Freeze Deadlock"
	 * This method is a help method to the method isDeadLock, it determines
	 * whether the board is in a state when "Dead square deadlock" has occurred.
	 * Please see: http://sokobano.de/wiki/index.php?title=Deadlocks#Dead_square_deadlocks
	 * for further details and example.
	 * 
	 * Here comes heuristic for finding "Dead square deadlock":
	 * 
	 * Find position (coordinate) for all boxes and goals
	 * Count non deadlocked boxes, if this count is less than
	 * number of goals => deadlock. 
	 * 
	 * This method uses a help method isBoxImmovable to see if it is deadlocked.
	 * 
	 * @param Board to determine if deadlocked or not.
	 * @returns "True" if the board is deadlocked.
	 */
	private boolean isDSAFD(Board pBoard) {
		boolean isDead = true;

		int i, j = 0;
		int rowCount = 10; // TODO use method provided by Emil
		int colCount = 10; // TODO use method provided by Emil

		ArrayList<Pos> boxPositions = new ArrayList<Pos>();
		ArrayList<Pos> goalPositions = new ArrayList<Pos>();
		
		//Get positions for every box and goal
		for (i = 0; i < rowCount; i++) {
			for(j = 0; j < colCount; j++) {
				Pos currPos = new Pos(i,j);
				byte currByte = pBoard.atPos(currPos);

				//current position is a box
				if(currByte == IS_BOX || currByte == IS_BOX_ON_GOAL) {
					boxPositions.add(currPos);
				} else if(currByte == IS_GOAL) { //current position is a goal
					goalPositions.add(currPos);
				}
			}
		}
		
		//Count boxes and goals
		int boxCount = boxPositions.size();
		int goalCount = goalPositions.size();
		
		ArrayList<Pos> immovableBoxPositions = new ArrayList<Pos>();
		
		//Find immovable boxes, count them
		for(i = 0; i < boxCount; i++) {
			Pos currPos = boxPositions.get(i);
			if(isBoxImmovable(currPos)) {
				immovableBoxPositions.add(currPos);
			}
		}
		
		int immovableBoxCount = immovableBoxPositions.size();
		
		if(immovableBoxCount > goalCount) {
			isDead = true;
		} else {
			isDead = false;
		}
		
		return isDead;
	}


	private boolean isCD(Board pBoard) {
		boolean isDead = true;
		return isDead;
	}

	private boolean isBD(Board pBoard) {
		boolean isDead = true;
		return isDead;
	}

	private boolean isDDTFB(Board pBoard) {
		boolean isDead = true;
		return isDead;
	}
}
