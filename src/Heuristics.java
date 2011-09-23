
import java.lang.Math;
import java.util.Vector;
//import BoardCoordinate;

public class Heuristics {

	// public final BoardCoordinate playerCoordinate;
	// public final Vector<BoardCoordinate> boxCoordinates;
	// public Vector<BoardCoordinate> goalPositions()
	
	public static final double INFINITY = 999999;
	
	public static double distanceSum(BoardState boardState) {
		
		Vector<BoardCoordinate> goalCoordinates = boardState.goalPositions();
		double boardValue = 0;
		double playerDistance = INFINITY;
		
		for (BoardCoordinate pos : boardState.boxCoordinates) {
			// reset on each iteration
			double distance = INFINITY;

			for (BoardCoordinate goalPos : goalCoordinates) {
				double curDistance = Math.sqrt(Math.pow(goalPos.row - pos.row, 2) +
											Math.pow(goalPos.column - pos.column, 2));
				if (curDistance < distance) {
					boardValue += distance;
				}
			}
			
			// calculate distance for player
			BoardCoordinate playerPos = boardState.playerCoordinate;
			double curPlayerDistance = Math.sqrt(Math.pow(playerPos.row - pos.row, 2) + Math.pow(playerPos.column - pos.column, 2));
			if (curPlayerDistance < playerDistance)
				playerDistance = curPlayerDistance;
			
		}
		
		boardValue += playerDistance;
		return boardValue;
	}
	
}