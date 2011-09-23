import java.util.ArrayList;
import java.util.Vector;

public class DeadlockFinder {

	/**
	 * This method receives an instance of class BoardState and for that Board
	 * determines whether it is a deadlocked BoardState or not, i.e. if it is
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
	 * @return Returns "True" if the BoardState is deadlocked.
	 */
	public boolean isDeadLock(BoardState state) {
		return (isDSAFD(state) || isCD(state) || isBD(state) || isDDTFB(state));
	}

	/**
	 * This is an essential help method for at least isDSD (help method to
	 * isDeadLock).
	 * 
	 * @param Pos
	 *            is the position of the box to check if deadlocked.
	 * @return Returns TRUE if the box is immovable
	 */
	private boolean isBoxImmovable(BoardState state, BoardCoordinate boxPos) {
		byte dataNorth, dataSouth, dataEast, dataWest;
		byte dataNE, dataNW, dataSE, dataSW; // North east, North west.....
		dataNorth = state.board.dataAt((byte) (boxPos.row - 1), boxPos.column);
		dataSouth = state.board.dataAt((byte) (boxPos.row + 1), boxPos.column);
		dataEast = state.board.dataAt(boxPos.row, (byte) (boxPos.column + 1));
		dataWest = state.board.dataAt(boxPos.row, (byte) (boxPos.column - 1));

		dataNE = state.board.dataAt((byte) (boxPos.row - 1),
				(byte) (boxPos.column + 1));
		dataNW = state.board.dataAt((byte) (boxPos.row - 1),
				(byte) (boxPos.column - 1));
		dataSE = state.board.dataAt((byte) (boxPos.row + 1),
				(byte) (boxPos.column + 1));
		dataSW = state.board.dataAt((byte) (boxPos.row + 1),
				(byte) (boxPos.column - 1));

		boolean northBlocked, southBlocked, westBlocked, eastBlocked;
		northBlocked = state.isOccupied((byte)(boxPos.row - 1), boxPos.column);
		southBlocked = state.isOccupied((byte)(boxPos.row + 1), boxPos.column);
		westBlocked = state.isOccupied(boxPos.row, (byte)(boxPos.column - 1));
		eastBlocked = state.isOccupied(boxPos.row, (byte)(boxPos.column + 1));

		// @formatter:off
		// 9 O'Clock
		// 							  # <- non-reachable square
		// non-reachable square -> # [] <- Box
		boolean nineOclock = (northBlocked && westBlocked);

		// 3 O'Clock
		// 		   # <-- non-reachable square
		// box -> [] # <-- non-reachable square
		boolean threeOclock = (northBlocked && eastBlocked);

		// Quarter past six
		// box -> [] # <-- non-reachable square
		// 		   # <-- non-reachable square
		boolean qPastSix = (southBlocked && eastBlocked);

		
		// Quarter to six
		// non-reachable square -> # [] <- Box
		// 							  # <- non-reachable square
		// @formatter:on
		boolean qToSix = (southBlocked && westBlocked);

		// check if box is in a corner and therefore immovable
		if (nineOclock || threeOclock || qPastSix || qToSix) {
			return true; // return, because box is in corner
		} else {
			// here we should check if there are any boxes in bowls

			// box is not totally free
			if (northBlocked || southBlocked || eastBlocked || westBlocked) {

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
	 * @param state
	 * @param boxPos
	 * @return
	 */
	private boolean inBowl(BoardState state, BoardCoordinate boxPos) {
		// TODO is this necessary? Do in isBoxImmovable instead?
		return true;
	}

	/**
	 * Actually the method name is misleading, "Wall" can either be a wall of
	 * boxes (on goal or not) or a wall of Board.TYPE_WALL.
	 * 
	 * The method determines if the box is next to any wall
	 * 
	 * @param state
	 * @param boxPos
	 * @return
	 */
	private boolean isNextToWall(BoardState state, BoardCoordinate boxPos) {
		// TODO is this necessary? Do in isBoxImmovable instead?
		return true;
	}


	/**
	 * "DSAFD" stands for "Dead square and Freeze Deadlock" This method is a
	 * help method to the method isDeadLock, it determines whether the BoardState is
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
	 * @returns "True" if the BoardState is deadlocked.
	 */
	private boolean isDSAFD(BoardState state) {
		boolean isDead = true;

		byte i, j = 0;
		int rowCount = state.board.rows(); // TODO use method provided by Emil
		int colCount = state.board.columns(); // TODO use method provided by Emil

		Vector<BoardCoordinate> boxPositions = state.boxCoordinates;
		Vector<BoardCoordinate> goalPositions = state.goalPositions();
	
		// Count boxes and goals
		int boxCount = boxPositions.size();
		int goalCount = goalPositions.size();

		ArrayList<BoardCoordinate> immovableBoxPositions = new ArrayList<BoardCoordinate>();

		// Find immovable boxes, count them
		for (i = 0; i < boxCount; i++) {
			BoardCoordinate currPos = boxPositions.get(i);
			if (isBoxImmovable(state, currPos)) {
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

	private boolean isCD(BoardState state) {
		boolean isDead = true;
		return isDead;
	}

	private boolean isBD(BoardState state) {
		boolean isDead = true;
		return isDead;
	}

	private boolean isDDTFB(BoardState state) {
		boolean isDead = true;
		return isDead;
	}
}
