import java.util.Vector;

public class DeadlockFinder {

	public static Vector<Boolean> boxVisited = new Vector<Boolean>();

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
		byte boxCount = (byte) state.boxCoordinates.size();
		for (Boolean beenVisited : boxVisited) {
			beenVisited = false;
		}

		// state.printState();
		return isDSAFD(state);
		// return (isDSAFD(state) || isCD(state) || isBD(state) ||
		// isDDTFB(state));
	}

	/**
	 * This is an essential help method for at least isDSD (help method to
	 * isDeadLock).
	 * 
	 * @param Pos
	 *            is the position of the box to check if deadlocked.
	 * @return Returns TRUE if the box is immovable
	 */
	private static boolean isBoxImmovable(BoardState state,
			BoardCoordinate boxCoordinate) {
		int indexOfBox = state.boxCoordinates.indexOf(boxCoordinate);
		boxVisited.add(indexOfBox, true); //mark the box to check visited.
		// check if the box is cornered
		if (isCornered(state, boxCoordinate)) {
			return true;
		} else {
			// here we should check if there are any boxes in bowls
			return inBowl(state, boxCoordinate);
		}
	}

	/*
	 * This function determines whether a box is cornered or not. There are
	 * three different scenarios in which the box could be cornered:
	 * 
	 * A: Cornered only by walls B: Cornered by a wall and cornered box(es) C:
	 * Cornered by cornered boxes only.
	 * 
	 * Problem: The board below is tricky, because the left box will be called
	 * cornered if we use: boolean wallNorth = state.board.isOccupied( since the
	 * right box corners it, but this box can be moved.
	 * 
	 * Possible solutions: A box can only be cornered by a wall or another box
	 * that also is cornered (this will result in a recursive check).
	 * 
	 * THIS METHOD SHOULD USE THE HELP METHODS "corneredByWall" AND
	 * "nextToCorneredBox" (which is recursive)?
	 */
	// @formatter:off
	//
	//	 ######## 
	//	#   # .# 
	//	#   $$.# 
	//	####@  # 
	//	   #  ## 
	//	   ####
	//
	// @formatter:on
	private static boolean isCornered(BoardState state,
			BoardCoordinate boxCoordinate) {

		if (isNextToWall(state, boxCoordinate)) {

			// base case in recursion.
			if (corneredByWall(state, boxCoordinate)) { // scenario A.
				return true;
			}

			// check scenario B
			// for all non-diagonally adjacent boxes check if they are
			// corneredByWall or nextToCorneredBox?
			Vector<BoardCoordinate> adjacentBoxes = new Vector<BoardCoordinate>();
			state.neighborBoxes(boxCoordinate, adjacentBoxes); // populate list
																// (vector) with
																// adjacent
																// boxes

			for (BoardCoordinate neighbour : adjacentBoxes) {
				// mark this box visited
				int indexOfBox = state.boxCoordinates.indexOf(neighbour); //TODO verify should this happen???
				if (!boxVisited.elementAt(indexOfBox)) {
					boxVisited.add(indexOfBox, true); //mark the box to check visited.

					boolean isCorneredByWall = corneredByWall(state, neighbour);

					boolean isNextToWall = isNextToWall(state, neighbour);

					if (isCorneredByWall || isNextToWall) {
						return true;
					} else {
						return isCornered(state, neighbour); // recursive call
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private static boolean corneredByWall(BoardState state,
			BoardCoordinate boxCoordinate) {
		boolean wallNorth = state.board.wallAt((byte) (boxCoordinate.row - 1),
				boxCoordinate.column);
		boolean wallSouth = state.board.wallAt((byte) (boxCoordinate.row + 1),
				boxCoordinate.column);
		boolean wallWest = state.board.wallAt(boxCoordinate.row,
				(byte) (boxCoordinate.column - 1));
		boolean wallEast = state.board.wallAt(boxCoordinate.row,
				(byte) (boxCoordinate.column + 1));
		// @formatter:off
		// 9 O'Clock
		// 							  # <- non-reachable square
		// non-reachable square -> # [] <- Box
		boolean corneredNineOclock = (wallNorth && wallWest);

		// 3 O'Clock
		// 		   # <-- non-reachable square
		// box -> [] # <-- non-reachable square
		boolean corneredThreeOclock = (wallNorth && wallEast);

		// Quarter past six
		// box -> [] # <-- non-reachable square
		// 		  # <-- non-reachable square
		boolean corneredQPastSix = (wallSouth && wallEast);

		
		// Quarter to six
		// non-reachable square -> # [] <- Box
		// 							  # <- non-reachable square
		// @formatter:on
		boolean corneredQToSix = (wallSouth && wallWest);

		// check if box is in a corner and therefore immovable
		if (corneredNineOclock || corneredThreeOclock || corneredQPastSix
				|| corneredQToSix) {
			return true; // Scenario A, box is in cornered by walls only
		} else {
			return false;
		}
	}

	private static boolean isNextToWall(BoardState state,
			BoardCoordinate boxCoordinate) {
		boolean wallNorth = state.board.wallAt((byte) (boxCoordinate.row - 1),
				boxCoordinate.column);
		boolean wallSouth = state.board.wallAt((byte) (boxCoordinate.row + 1),
				boxCoordinate.column);
		boolean wallWest = state.board.wallAt(boxCoordinate.row,
				(byte) (boxCoordinate.column - 1));
		boolean wallEast = state.board.wallAt(boxCoordinate.row,
				(byte) (boxCoordinate.column + 1));

		if (wallNorth || wallSouth || wallWest || wallEast) {
			return true;
		} else {
			return false;
		}
	}

	// private static boolean nextToCorneredBox(BoardState state,
	// BoardCoordinate boxCoordinate) {
	//
	// Vector<BoardCoordinate> adjacentBoxes = new Vector<BoardCoordinate>();
	// state.neighborBoxes(boxCoordinate, adjacentBoxes); //populate list
	// (vector) with adjacent boxes
	//
	// for(BoardCoordinate neighbour : adjacentBoxes) {
	// boolean cornered = nextToCorneredBox(state, neighbour);
	// }
	// return false;
	// }

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
	 * if(any non-diagonally adjacent square occupied) For any of the two
	 * scenarios do the following: boxesInBowl <- count number of boxes
	 * goalsInBowl <- count number of goals corneredBoxes <- iterate through
	 * whole column/row, count number of cornered boxes with method isCornered
	 * movableBoxes <- boxesInBowl - corneredBoxes freeGoals <- goalsInBowl -
	 * boxesOnGoals if movableBoxes < freeGoals => deadlock
	 * 
	 * 
	 * if vertical bowl go south count go north count check values else if
	 * horizontal bowl go east count go west count check values else return
	 * false end
	 * 
	 * @param state
	 *            of the board
	 * @param boxCoordinate
	 *            position of the box to examine
	 * @return returns TRUE if the box is in a bowl.
	 */
	private static boolean inBowl(BoardState state,
			BoardCoordinate boxCoordinate) {
		byte r, c = 0;
		int rowCount = state.board.rows();
		int colCount = state.board.columns();

		boolean northBlocked = state.isOccupied((byte) (boxCoordinate.row - 1),
				boxCoordinate.column);
		boolean southBlocked = state.isOccupied((byte) (boxCoordinate.row + 1),
				boxCoordinate.column);
		boolean westBlocked = state.isOccupied(boxCoordinate.row,
				(byte) (boxCoordinate.column - 1));
		boolean eastBlocked = state.isOccupied(boxCoordinate.row,
				(byte) (boxCoordinate.column + 1));

		// First of all, check if the box is against any occupied square
		// (wall/box/box on goal)
		if (northBlocked || southBlocked || westBlocked || eastBlocked) {
			byte goalsInBowl = 0;
			byte corneredBoxes = 0;
			byte movableBoxes = 0;
			byte freeGoals = 0;

			boolean boxHere = false;
			boolean goalHere = false;
			boolean boxHereCornered = false;

			// Vertical bowl
			if (westBlocked || eastBlocked) { // Could be case 1 or case 4,
												// treated as the same

				// find the boundaries for the bowl
				byte northernWallRowNo = 0; // initializes values irrelevant
				byte southernWallRowNo = (byte) rowCount; // initializes values
															// irrelevant
				// go south to find wall
				for (r = boxCoordinate.row; r < rowCount; r++) {
					if (state.board.wallAt(r, boxCoordinate.column)) {
						southernWallRowNo = r;
						break;
					}
				}

				// go north to find wall
				for (r = boxCoordinate.row; r > 0; r--) {
					if (state.board.wallAt(r, boxCoordinate.column)) {
						northernWallRowNo = r;
						break;
					}
				}
				// boundaries found

				// go from northern to southern wall
				for (r = (byte) (northernWallRowNo + 1); r < southernWallRowNo; r++) {

					if (westBlocked) {
						if (!state.isOccupied(r,
								(byte) (boxCoordinate.column - 1))) {
							// not a continuous wall west of current position =>
							// not a bowl
							return false;
						}
					} else {
						if (!state.isOccupied(r,
								(byte) (boxCoordinate.column + 1))) {
							// not a continuous wall east of current position =>
							// not a bowl
							return false;
						}
					}

					// check if goal
					if (state.board.goalAt(r, boxCoordinate.column)) {
						goalHere = true;
					}

					// check if there is a box standing here
					if (state.boxAt(r, boxCoordinate.column)) { // check if this
																// is a free
																// goal, i.e. no
																// box on it
						boxHere = true;
						BoardCoordinate posForBox = new BoardCoordinate(r,
								boxCoordinate.column);
						if (isCornered(state, posForBox)) {
							boxHereCornered = true;
						}
					}

					if (goalHere) {
						goalsInBowl++;
						if (!boxHere) {
							freeGoals++;
						}
					}

					if (boxHere) {
						if (!boxHereCornered) {
							movableBoxes++;
						} else {
							corneredBoxes++;
						}
					}

					// reset flags
					boxHere = false;
					goalHere = false;
					boxHereCornered = false;
				}

				// both must be blocked in order for the box to be in a "bowl"
				if (movableBoxes < freeGoals) {
					return true; // this is a deadlock scenario
				} else {
					return false; // not any bowl
				}

				// horizontal bowl
			} else if (northBlocked || southBlocked) { // Could be case 2 or
														// case 3, treated as
														// the same
				// find the boundaries for the bowl
				byte easternWallColumnNo = 0; // initializes values irrelevant
				byte westernWallColumnNo = (byte) colCount; // initializes
															// values irrelevant
				// go east to find wall
				for (c = boxCoordinate.column; c < colCount; c++) {
					if (state.board.wallAt(boxCoordinate.row, c)) {
						easternWallColumnNo = c;
						break;
					}
				}

				// go west to find wall
				for (c = boxCoordinate.column; c > 0; c--) {
					if (state.board.wallAt(boxCoordinate.row, c)) {
						westernWallColumnNo = c;
						break;
					}
				}
				// boundaries found

				// go from western to eastern wall
				for (c = (byte) (westernWallColumnNo + 1); c < easternWallColumnNo; c++) {

					if (northBlocked) {
						if (!state
								.isOccupied((byte) (boxCoordinate.row - 1), c)) {
							// not a continuous wall north of current position
							// => not a bowl
							return false;
						}
					} else {
						if (!state
								.isOccupied((byte) (boxCoordinate.row + 1), c)) {
							// not a continuous south east of current position
							// => not a bowl
							return false;
						}
					}

					// check if goal
					if (state.board.goalAt(boxCoordinate.row, c)) {
						goalHere = true;
					}

					// check if there is a box standing here
					if (state.boxAt(boxCoordinate.row, c)) { // check if this is
																// a free goal,
																// i.e. no box
																// on it
						boxHere = true;
						BoardCoordinate pos4Box = new BoardCoordinate(
								boxCoordinate.row, c);
						if (isCornered(state, pos4Box)) {
							boxHereCornered = true;
						}
					}

					if (goalHere) {
						goalsInBowl++;
						if (!boxHere) {
							freeGoals++;
						}
					}

					if (boxHere) {
						if (!boxHereCornered) {
							movableBoxes++;
						} else {
							corneredBoxes++;
						}
					}

					// reset flags
					boxHere = false;
					goalHere = false;
					boxHereCornered = false;
				}

				// both must be blocked in order for the box to be in a "bowl"
				if (movableBoxes < freeGoals) {
					return true; // this is a deadlock scenario
				} else {
					return false; // not any bowl
				}
			} else {
				return false; // not any bowl
			}
		} else {
			return false; // not any bowl
		}
	}

	/**
	 * "DSAFD" stands for "Dead square and Freeze Deadlock" This method is a
	 * help method to the method isDeadLock, it determines whether the
	 * BoardState is in a state when "Dead square deadlock" has occurred. Please
	 * see:
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
		Vector<BoardCoordinate> boxCoordinates = state.boxCoordinates;
		Vector<BoardCoordinate> goalPositions = state.goalPositions();

		Vector<BoardCoordinate> immovableBoxesCoordinates = new Vector<BoardCoordinate>();

		// Count boxes and goals
		byte boxCount = (byte) boxCoordinates.size();
		byte goalCount = (byte) goalPositions.size();

		int i;
		// Find immovable boxes, count them
		for (i = 0; i < boxCount; i++) {
			BoardCoordinate currPos = boxCoordinates.get(i);
			if (isBoxImmovable(state, currPos)) {
				immovableBoxesCoordinates.add(currPos);
			}
		}

		byte immovableBoxCount = (byte) immovableBoxesCoordinates.size();
		byte boxesOnGoals = state.boxesOnGoals();

		byte movableBoxes = (byte) (boxCount - immovableBoxCount);
		byte freeGoals = (byte) (goalCount - boxesOnGoals);

		if (movableBoxes < freeGoals) {
			return true; // deadlock, since there are not sufficient amount of
							// boxes free
		} else {
			return false;
		}
	}

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
}
