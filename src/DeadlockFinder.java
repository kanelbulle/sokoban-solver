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
	public static boolean isDeadLock(BoardState state) {
		return isDSAFD(state);
//		return (isDSAFD(state) || isCD(state) || isBD(state) || isDDTFB(state));
	}

	/**
	 * This is an essential help method for at least isDSD (help method to
	 * isDeadLock).
	 * 
	 * @param Pos
	 *            is the position of the box to check if deadlocked.
	 * @return Returns TRUE if the box is immovable
	 */
	private static boolean isBoxImmovable(BoardState state, BoardCoordinate boxPos) {
		//check if the box is cornered
		if(isCornered(state, boxPos)) {
			return true;
		} else {
			// here we should check if there are any boxes in bowls
			return inBowl(state, boxPos);
		}
	}
	
	private static boolean isCornered(BoardState state, BoardCoordinate boxPos) {
		boolean northBlocked = state.isOccupied((byte)(boxPos.row - 1), boxPos.column);
		boolean southBlocked = state.isOccupied((byte)(boxPos.row + 1), boxPos.column);
		boolean westBlocked = state.isOccupied(boxPos.row, (byte)(boxPos.column - 1));
		boolean eastBlocked = state.isOccupied(boxPos.row, (byte)(boxPos.column + 1));
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
			return false;
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
	 */
	// @formatter:off
	 //
	 // 	Legend: # = "wall", [] = box, X = goal
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
	 * Here is the algorithm for this method:
	 * 	
	 * if(any non-diagonally adjacent square occupied)
	 * 		For any of the two scenarios do the following:
	 * 			boxesInBowl <- count number of boxes
	 *			goalsInBowl <- count number of goals
	 *			corneredBoxes <- iterate through whole column/row, count number of cornered boxes with method isCornered
	 *			movableBoxes <- boxesInBowl - corneredBoxes
	 *			freeGoals <- goalsInBowl - boxesOnGoals
	 *			if movableBoxes < freeGoals => deadlock
	 *			
	 * 
	 * 		if vertical bowl
	 * 			go south
	 * 				count
	 * 			go north
	 * 				count
	 * 			check values	
	 * 		else if horizontal bowl
	 * 			go east
	 * 				count
	 * 			go west
	 * 				count
	 * 			check values	
	 * else
	 * 		return false
	 * end
	 * 
	 * @param state of the board
	 * @param boxPos position of the box to examine
	 * @return returns TRUE if the box is in a bowl.
	 */
	private static boolean inBowl(BoardState state, BoardCoordinate boxPos) {
		byte r, c = 0;
		int rowCount = state.board.rows(); // TODO use method provided by Emil
		int colCount = state.board.columns(); // TODO use method provided by Emil
		
		boolean northBlocked = state.isOccupied((byte)(boxPos.row - 1), boxPos.column);
		boolean southBlocked = state.isOccupied((byte)(boxPos.row + 1), boxPos.column);
		boolean westBlocked = state.isOccupied(boxPos.row, (byte)(boxPos.column - 1));
		boolean eastBlocked = state.isOccupied(boxPos.row, (byte)(boxPos.column + 1));
		
		//First of all, check if the box is against any occupied square (wall/box/box on goal)
		if(northBlocked || southBlocked || westBlocked || eastBlocked) {
			byte goalsInBowl = 0;
			byte corneredBoxes = 0;
			byte movableBoxes = 0;
			byte freeGoals = 0;
			
			boolean boxHere = false;
			boolean goalHere = false;
			boolean boxHereCornered = false;
			
			//Vertical bowl
			if(westBlocked || eastBlocked) { //Could be case 1 or case 4, treated as the same
				
				//find the boundaries for the bowl
				byte northernWallRowNo = 0; //initializes values irrelevant
				byte southernWallRowNo = (byte) rowCount;  //initializes values irrelevant
				//go south to find wall
				for(r = boxPos.row; r < rowCount; r++) {
					if(state.board.wallAt(r, boxPos.column)) {
						northernWallRowNo = r;
						break;
					}
				}
				
				//go north to find wall
				for(r = boxPos.row; r > 0; r--) {
					if(state.board.wallAt(r, boxPos.column)) {
						southernWallRowNo = r;
						break;
					}
				}
				//boundaries found
				
				
				//go from northern to southern wall
				for(r = northernWallRowNo; r < southernWallRowNo; r++) {
					
					//check if goal
					if(state.board.goalAt(r, boxPos.column)) {
						goalHere = true;
					}
					
					//check if there is a box standing here
					if(!state.boxAt(r, boxPos.column)) { //check if this is a free goal, i.e. no box on it
						boxHere = true;
						BoardCoordinate pos4Box = new BoardCoordinate(r, boxPos.column);
						if(isCornered(state, pos4Box)) {
							boxHereCornered = true;
						}
					}
					
					if(goalHere) {
						goalsInBowl++;
						if(!boxHere) {
							freeGoals++;
						}
					}
					
					if(boxHere) {
						if(!boxHereCornered) {
							movableBoxes++;
						} else {
							corneredBoxes++;
						}
					}
					
					//reset flags
					boxHere = false;
					goalHere = false;
					boxHereCornered = false;
				}
				
				
				//both must be blocked in order for the box to be in a "bowl"
				if(movableBoxes < freeGoals) {
					return true; //this is a deadlock scenario
				} else {
					return false; //not any bowl
				}
				
			//horizontal bowl
			} else if (northBlocked || southBlocked) { //Could be case 2 or case 3, treated as the same
				//find the boundaries for the bowl
				byte easternWallColumnNo = 0; //initializes values irrelevant
				byte westernWallColumnNo = (byte) colCount;  //initializes values irrelevant
				//go south to find wall
				for(c = boxPos.column; c < colCount; c++) {
					if(state.board.wallAt(boxPos.row, c)) {
						easternWallColumnNo = c;
						break;
					}
				}
				
				//go north to find wall
				for(c = boxPos.column; c > 0; c--) {
					if(state.board.wallAt(boxPos.row, c)) {
						westernWallColumnNo = c;
						break;
					}
				}
				//boundaries found
				
				
				//go from western to eastern wall
				for(c = westernWallColumnNo; c < easternWallColumnNo; c++) {
					
					//check if goal
					if(state.board.goalAt(boxPos.row, c)) {
						goalHere = true;
					}
					
					//check if there is a box standing here
					if(!state.boxAt(boxPos.row, c)) { //check if this is a free goal, i.e. no box on it
						boxHere = true;
						BoardCoordinate pos4Box = new BoardCoordinate(boxPos.row, c);
						if(isCornered(state, pos4Box)) {
							boxHereCornered = true;
						}
					}
					
					if(goalHere) {
						goalsInBowl++;
						if(!boxHere) {
							freeGoals++;
						}
					}
					
					if(boxHere) {
						if(!boxHereCornered) {
							movableBoxes++;
						} else {
							corneredBoxes++;
						}
					}
					
					//reset flags
					boxHere = false;
					goalHere = false;
					boxHereCornered = false;
				}
				
				
				//both must be blocked in order for the box to be in a "bowl"
				if(movableBoxes < freeGoals) {
					return true; //this is a deadlock scenario
				} else {
					return false; //not any bowl
				}
			} else {
				return false; //not any bowl
			}
		} else {
			return false; //not any bowl
		}
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
	private static boolean isDSAFD(BoardState state) {
		Vector<BoardCoordinate> boxPositions = state.boxCoordinates;
		Vector<BoardCoordinate> goalPositions = state.goalPositions();

		Vector<BoardCoordinate> immovableBoxPositions = new Vector<BoardCoordinate>();

		// Count boxes and goals
		byte boxCount = (byte) boxPositions.size();
		byte goalCount = (byte) goalPositions.size();
		
		int i;
		// Find immovable boxes, count them
		for (i = 0; i < boxCount; i++) {
			BoardCoordinate currPos = boxPositions.get(i);
			if (isBoxImmovable(state, currPos)) {
				immovableBoxPositions.add(currPos);
			}
		}

		byte immovableBoxCount = (byte) immovableBoxPositions.size();
		byte boxesOnGoals = state.boxesOnGoals();
		
		byte freeBoxes = (byte) (boxCount - immovableBoxCount);
		byte freeGoals = (byte) (goalCount - boxesOnGoals); 

		if (freeBoxes < freeGoals) {
			return true;
		} else {
			return false;
		}
	}

//	//TODO do this
//	private boolean isCD(BoardState state) {
//		boolean isDead = true;
//		return isDead;
//	}
//
//	//TODO do this
//	private boolean isBD(BoardState state) {
//		boolean isDead = true;
//		return isDead;
//	}
//
//	//TODO do this
//	private boolean isDDTFB(BoardState state) {
//		boolean isDead = true;
//		return isDead;
//	}
}
