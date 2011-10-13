import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Vector;


public class Solver {

	/* List of nodes we have expanded (ie explored). 
	 * State found that lead to deadlock should be added here to avoid being explored again. */
	public static HashSet<BoardState> visited = new HashSet<BoardState>();
	public static HashMap<BoardState, Double> heuristicsScore = new HashMap<BoardState, Double>();
	
	private final int TIMEOUT = 59000;
	private final int INTERVAL = 400;
	long debugNumNodesVisited;
	long debugNumNodesExplored;
	HashMap<BoardState, Integer> debugGraphLevel = new HashMap<BoardState, Integer>();
	
	public String solve(Board initialBoard) {
		visited.clear();
		heuristicsScore.clear();
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
		
		start.printState();
		
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
		heuristicsScore.put(start, Heuristics.heuristicValue(start));
		long startTime = new Date().getTime();
		int i = 0;
		
//start.printState();
		
		while (!openSet.isEmpty()) {
			
			if (i == INTERVAL) {
				i = 0;
				if (startTime+TIMEOUT < new Date().getTime()) {
					throw new RuntimeException("Failed!");
				}
			}
			i++;
			
			debugNumNodesExplored++;
			BoardState parent = openSet.poll();
			visited.add(parent);		
		
			if (parent.isSolved()) {
				return createSolutionPath(parent);
			}
			
			//boolean maybeBetterPath = false;
			parent.possibleBoxMoves(childStates);
			
			for (BoardState child : childStates) {
				if (visited.contains(child)) { continue; }

				path.put(child, parent);
				debugNumNodesVisited++;

				if (!openSet.contains(child)) {
					heuristicsScore.put(child, Heuristics.heuristicValue(child));
					openSet.add(child);
				}
					//maybeBetterPath = true;
//				} else if (graphDistance < g.get(child)) {
//					maybeBetterPath = true;
//				} else {
//					//maybeBetterPath = false;
//				}

//				if (maybeBetterPath) {
//					g.put(child, graphDistance);
//					h.put(child, Heuristics.heuristicValue(child));
//					heuristicsScore.put(child, h.get(child));
//					//heuristicsScore.put(child, (g.get(child) + h.get(child)));
//				}
			}
		}

		// fail here w00t!
		throw new RuntimeException("Failed!");
	}

	public void printQueue(PriorityQueue<BoardState> queue) {
		PriorityQueue<BoardState> tmp = new PriorityQueue<BoardState>(queue);
		System.out.println("start @@@@@@@@@");
		for (int i = 0; i < tmp.size(); i++) {
			BoardState s = tmp.poll();
			System.out.println("..in queue .. pos: " + i + " cost: " + heuristicsScore.get(s) + " level: " + debugGraphLevel.get(s));
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
}
