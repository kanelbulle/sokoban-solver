import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Vector;


public class Solver {

	/* List of nodes we have expanded (ie explored). 
	 * State found that lead to deadlock should be added here to avoid being explored again. */
	public static HashSet<BoardState> visited = new HashSet<BoardState>();
	public static HashMap<BoardState, Double> heuristicScore = new HashMap<BoardState, Double>();
	
	private final int TIMEOUT = 59000;
	long debugNumNodesVisited;
	long debugNumNodesExplored;
	HashMap<BoardState, Integer> debugGraphLevel = new HashMap<BoardState, Integer>();
	
	public String solve(Board initialBoard) {
		long time1 = System.currentTimeMillis();
		debugNumNodesVisited = 0;
		debugNumNodesExplored = 0;
		
		BoardState start = initialBoard.startState();
		String solution = AStar(start);

		long time2 = System.currentTimeMillis();
		System.out.println("Time: " + (time2-time1)/1000.0 + " seconds");

		return solution; 
	}


	/** 
	Uses the heuristic f(x) = g(x) + h(x) 
	where:	g(x) is the node distance in the search graph from start to state 
			h(x) is a heuristic for approximating the distance form some board state to goal state
	 */
	public String AStar(BoardState start) {
		/* List of nodes not yet expanded.
		The open list contains the cells that may fall on the optimal path we want. 
		In other words, the open list contains the cells we need to take a closer 
		look at in our search process. */
		PriorityQueue<BoardState> openSet = new PriorityQueue<BoardState>();

		/* path consists of (child, parent) boardStates, link means path from-to */
		HashMap<BoardState, BoardState> path = new HashMap<BoardState, BoardState>();

		/* Container variable used/reused during iterations */
		Vector<BoardState> childStates = new Vector<BoardState>();

		/* Initial setup */
		openSet.add(start);
		heuristicScore.put(start, Heuristics.heuristicValue(start));
		debugGraphLevel.put(start, 0);

		long startTime = new Date().getTime();
		while (!openSet.isEmpty() && (startTime+TIMEOUT > new Date().getTime())) {
			debugNumNodesExplored++;
			
			BoardState parent = openSet.poll();
			visited.add(parent);		
			if (parent.isSolved()) {
				return createSolutionPath(parent);
			}
			
//System.out.println("picked: "); parent.printState();

			boolean maybeBetterPath = false;
			parent.possibleBoxMoves(childStates);
			
			for (BoardState child : childStates) {
				if (visited.contains(child)) { 
					continue;
				}
				
				debugNumNodesVisited++;
				
//System.out.println("Testing child  (would get score: " + Heuristics.goalDistance(child) + ") at level: " + debugGraphLevel.get(parent) +1); child.printState();

				if (!openSet.contains(child)) {
					heuristicScore.put(child, Heuristics.heuristicValue(child));
					debugGraphLevel.put(child, debugGraphLevel.get(parent) +1);
					openSet.add(child);
					maybeBetterPath = true;
				} else {
					maybeBetterPath = false;
				}
				
				if (maybeBetterPath) {
					path.put(child, parent);
					heuristicScore.put(child, Heuristics.heuristicValue(child));
				}
			}
			
//printQueue(openSet);

		}

		// fail here w00t!
		throw new RuntimeException("Failed!");
	}

	public void printQueue(PriorityQueue<BoardState> queue) {
		PriorityQueue<BoardState> tmp = new PriorityQueue<BoardState>(queue);
		System.out.println("start @@@@@@@@@");
		for (int i = 0; i < tmp.size(); i++) {
			BoardState s = tmp.poll();
			System.out.println("..in queue .. pos: " + i + " cost: " + heuristicScore.get(s) + " level: " + debugGraphLevel.get(s));
			s.printState();
		}
		System.out.println("end @@@@@@@");
	}
	
	/* Backtracks from goalstate to startstate */
	public String createSolutionPath(BoardState endState) {
		System.out.println("Found goal state!");
		BoardState bsParent = endState;
		String moveSolution = "";
		while (bsParent.parent != null) {
			for (BoardState.Move m : bsParent.backtrackMoves) {
				moveSolution = "" + m.move + moveSolution;
			}

			bsParent = bsParent.parent;
		}
		System.out.println("Nodes visited: " + debugNumNodesVisited + " nodes.");
		System.out.println("Nodes explored: " + debugNumNodesExplored + " nodes.");
		System.out.println("Solution length: " + moveSolution.length());
		
		return moveSolution;
	}
	
	/*
	public String naivSolver(BoardState start) {

		LinkedList<BoardState> queue = new LinkedList<BoardState>();
		HashSet<BoardState> visitedStates = new HashSet<BoardState>();
		Vector<BoardState> childStates = new Vector<BoardState>();

		queue.add(start);

		while (!queue.isEmpty()) {
			BoardState parent = queue.poll();
			parent.possibleBoxMoves(childStates);
			for (BoardState child : childStates) {
				if (child.isSolved()) {
					BoardState bsParent = child;
					String moveSolution = "";
					while (bsParent.lastMove != BoardState.MOVE_NULL) {
						moveSolution = "" + bsParent.lastMove + moveSolution;
						bsParent = bsParent.parent;
					}

					return moveSolution;
				}

				if (DeadlockFinder.isDeadLock(child)) {
					continue;
				}

				if (visitedStates.contains(child)) {
					continue;
				}

				queue.add(child);
				visitedStates.add(child);
			}
		}

		return null;
	}
	*/

}
