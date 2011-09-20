import java.util.ArrayList;

public class DeadlockFinder {

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
	 * @param Board
	 *            to determine if deadlocked or not
	 * @return Returns "True" if the board is deadlocked.
	 */
	public boolean isDeadLock(Board theBoard) {
		return (isDSAFD(theBoard) || isCD(theBoard) || isBD(theBoard) || isDDTFB(theBoard));
	}

	/**
	 * This is an essential help method for at least isDSD (help method to
	 * isDeadLock).
	 * 
	 * @param Pos
	 *            is the position of the box to check if deadlocked.
	 * @return Returns TRUE if the box is immovable
	 */
	private boolean isBoxImmovable(Board theBoard, BoardCoordinate boxPos) {
		byte dataNorth, dataSouth, dataEast, dataWest;
		dataNorth = theBoard.dataAt((byte) (boxPos.row - 1), boxPos.column);
		dataSouth = theBoard.dataAt((byte) (boxPos.row + 1), boxPos.column);
		dataEast = theBoard.dataAt(boxPos.row, (byte) (boxPos.column + 1));
		dataWest = theBoard.dataAt(boxPos.row, (byte) (boxPos.column - 1));

		// @formatter:off
		// 9 O'Clock
		// 							  # <- non-reachable square
		// non-reachable square -> # [] <- Box
		boolean nineOclock = (!isReachable(dataNorth) && !isReachable(dataWest));

		// 3 O'Clock
		// 		   # <-- non-reachable square
		// box -> [] # <-- non-reachable square
		boolean threeOclock = (!isReachable(dataNorth) && !isReachable(dataEast));

		// Quarter past six
		// box -> [] # <-- non-reachable square
		// 		   # <-- non-reachable square
		boolean qPastSix = (!isReachable(dataSouth) && !isReachable(dataEast));

		
		// Quarter to six
		// non-reachable square -> # [] <- Box
		// 							  # <- non-reachable square
		// @formatter:on
		boolean qToSix = (!isReachable(dataSouth) && !isReachable(dataWest));

		// check if box is in a corner and therefore immovable
		if (nineOclock || threeOclock || qPastSix || qToSix) {
			return true; // return, because box is in corner
		} else {
			// Check if box is stuck against a wall next to which there are no
			// goals.
			
			//box is not totally free
			if(!isReachable(dataNorth) || !isReachable(dataSouth) || !isReachable(dataEast) || !isReachable(dataWest)) {
				
			}
			return false; // TODO remove this
		}
	}

	/**
	 * This method determines if the box is "in a bowl", that means that the box
	 * is stuck against local "wall" that is closed off on the sides, so that
	 * the box can't be moved from that wall, or stuck against a outer "wall".
	 * 
	 * We use the word "wall" since the wall can be either a wall of
	 * Board.TYPE_WALL or boxes or boxes on goals.
	 * 
	 * Here is a few examples of situations where the box is "in a bowl":
	 * Legend: # = "wall", [] = box, X = goal
	 */
	// @formatter:off
	 // 
	 //  1) WestBowl		2) NorthBowl		3)	SouthBowl	4) EastBowl
	 // 
	 // 	##				#########		    #	X	 #		   	   ##
	 // 	#				#	[]	#			#	[]	 #				#
	 // 	#  X			  X					##########				#
	 // 	#[]														  []#
	 // 	#														X	#
	 // 	##														   ##
	 // 
	 // @formatter:on 
	/**
	 * @param theBoard
	 * @param boxPos
	 * @return
	 */
	private boolean inBowl(Board theBoard, BoardCoordinate boxPos) {
		//TODO is this necessary? Do in isBoxImmovable instead?
		return true;
	}

	/**
	 * Actually the method name is misleading, "Wall" can either be a wall of
	 * boxes (on goal or not) or a wall of Board.TYPE_WALL.
	 * 
	 * The method determines if the box is next to any wall
	 * 
	 * @param theBoard
	 * @param boxPos
	 * @return
	 */
	private boolean isNextToWall(Board theBoard, BoardCoordinate boxPos) {
		//TODO is this necessary? Do in isBoxImmovable instead?
		return true;
	}

	/**
	 * This method determines whether the player can walk on a given square or
	 * not.
	 * 
	 * @param squareData
	 *            the square to examine
	 * @return Returns FALSE if it is not reachable, else TRUE.
	 */
	private boolean isReachable(byte squareData) {
		if (squareData == Board.TYPE_BOX
				|| squareData == Board.TYPE_BOX_ON_GOAL
				|| squareData == Board.TYPE_NOTHING
				|| squareData == Board.TYPE_WALL) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * "DSAFD" stands for "Dead square and Freeze Deadlock" This method is a
	 * help method to the method isDeadLock, it determines whether the board is
	 * in a state when "Dead square deadlock" has occurred. Please see:
	 * http://sokobano.de/wiki/index.php?title=Deadlocks#Dead_square_deadlocks
	 * for further details and example.
	 * 
	 * Here comes heuristic for finding "Dead square deadlock":
	 * 
	 * Find position (coordinate) for all boxes and goals Count non deadlocked
	 * boxes, if this count is less than number of goals => deadlock.
	 * 
	 * This method uses a help method isBoxImmovable to see if it is deadlocked.
	 * 
	 * @param Board
	 *            to determine if deadlocked or not.
	 * @returns "True" if the board is deadlocked.
	 */
	private boolean isDSAFD(Board theBoard) {
		boolean isDead = true;

		byte i, j = 0;
		int rowCount = 10; // TODO use method provided by Emil
		int colCount = 10; // TODO use method provided by Emil

		ArrayList<BoardCoordinate> boxPositions = new ArrayList<BoardCoordinate>();
		ArrayList<BoardCoordinate> goalPositions = new ArrayList<BoardCoordinate>();

		// Get positions for every box and goal
		for (i = 0; i < rowCount; i++) {
			for (j = 0; j < colCount; j++) {
				BoardCoordinate currPos = new BoardCoordinate(i, j);
				byte currByte = theBoard.dataAt(i, j);

				// current position is a box
				if (currByte == Board.TYPE_BOX
						|| currByte == Board.TYPE_BOX_ON_GOAL) { // TODO low
																	// prio,
																	// when
																	// merged,
																	// remove
																	// "Board."
					boxPositions.add(currPos);
				} else if (currByte == Board.TYPE_GOAL_SQUARE) { // current
																	// position
																	// is a goal
					goalPositions.add(currPos);
				}
			}
		}

		// Count boxes and goals
		int boxCount = boxPositions.size();
		int goalCount = goalPositions.size();

		ArrayList<BoardCoordinate> immovableBoxPositions = new ArrayList<BoardCoordinate>();

		// Find immovable boxes, count them
		for (i = 0; i < boxCount; i++) {
			BoardCoordinate currPos = boxPositions.get(i);
			if (isBoxImmovable(theBoard, currPos)) {
				immovableBoxPositions.add(currPos);
			}
		}

		int immovableBoxCount = immovableBoxPositions.size();

		if (immovableBoxCount > goalCount) {
			isDead = true;
		} else {
			isDead = false;
		}

		return isDead;
	}

	private boolean isCD(Board theBoard) {
		boolean isDead = true;
		return isDead;
	}

	private boolean isBD(Board theBoard) {
		boolean isDead = true;
		return isDead;
	}

	private boolean isDDTFB(Board theBoard) {
		boolean isDead = true;
		return isDead;
	}
}
