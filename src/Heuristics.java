import java.lang.Math;
import java.util.Vector;

public class Heuristics {

	public static final double INFINITY = Double.POSITIVE_INFINITY;

	
	public static double heuristicValue(BoardState boardState) {
		//return goalDistance(boardState);
		
		return emilDistance(boardState);
	}
	
	/** Approximates the from supplied boardState to some goal state */
	public static double goalDistance(BoardState boardState) {

		Vector<BoardCoordinate> goalCoordinates = boardState.goalPositions();
		double boardValue = 0;
		double playerDistance = INFINITY;

		for (BoardCoordinate pos : boardState.boxCoordinates) {
			//if (boardState.board.goalAt(pos.row, pos.column)) { continue; }
			// reset on each iteration
			double distance = INFINITY;

			for (BoardCoordinate goalPos : goalCoordinates) {
				//if (boardState.isOccupied(goalPos.row, goalPos.column)) { continue; }
				
				double curDistance = Math.sqrt(Math.pow(goalPos.row - pos.row,
						2) + Math.pow(goalPos.column - pos.column, 2));
				if (curDistance < distance) {
					// System.out.println("" + curDistance);
					distance = curDistance;
				}

			}

			boardValue += distance;

			// calculate distance for player
			BoardCoordinate playerPos = boardState.playerCoordinate;
			double curPlayerDistance = Math.sqrt(Math.pow(playerPos.row
					- pos.row, 2)
					+ Math.pow(playerPos.column - pos.column, 2));
			if (curPlayerDistance < playerDistance)
				playerDistance = curPlayerDistance;

		}

		boardValue += playerDistance;
		return boardValue;
	}
	
	public static double emilDistance(BoardState bs) {
		double dSum = 0;
		for (BoardCoordinate bPos : bs.boxCoordinates) {
			double min = Double.POSITIVE_INFINITY;
			for (BoardCoordinate gPos : bs.goalPositions()) {
				double d = Math.abs(bPos.row - gPos.row) + Math.abs(bPos.column - gPos.column);
				if (d < min) {
					min = d;
				}
			}
			dSum += min;
		}
		
		return dSum;
	}

}