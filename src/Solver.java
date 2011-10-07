
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;


public class Solver {

	/* List of nodes we HAVE expanded (ie explored). State found that lead to deadlock should be added here. */
	public HashSet<BoardState> closedset = new HashSet<BoardState>();
	private final int TIMEOUT = 59000;
	long debugNumNodes;
	
	public String solve(Board initialBoard) {
		long time1 = System.currentTimeMillis();
		debugNumNodes = 0;
		
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
		PriorityQueue<BoardState> openset = new PriorityQueue<BoardState>();

		/* path consists of (child, parent) boardStates, link means path from-to */
		HashMap<BoardState, BoardState> path = new HashMap<BoardState, BoardState>();

		HashMap<BoardState, Double> g = new HashMap<BoardState, Double>();
		HashMap<BoardState, Double> h = new HashMap<BoardState, Double>();
		HashMap<BoardState, Double> f = new HashMap<BoardState, Double>();

		/* Container variable used/reused during iterations */
		Vector<BoardState> childStates = new Vector<BoardState>();

		/* Initial setup */
		openset.add(start);

		g.put(start, 0.0);
		h.put(start, Heuristics.goalDistance(start));
		f.put(start, h.get(start));

		BoardState parent;
		long startTime = new Date().getTime();
		while ((parent = openset.poll()) != null && (startTime+TIMEOUT > new Date().getTime())) {	
			closedset.add(parent);

			if (parent.isSolved()) {
				System.out.println("Found goal state!");
				BoardState bsParent = parent;
				String moveSolution = "";
				while (bsParent.parent != null) {
					for (BoardState.Move m : bsParent.backtrackMoves) {
						moveSolution = "" + m.move + moveSolution;
					}

					bsParent = bsParent.parent;
				}
				System.out.println("Explored: " + debugNumNodes + " nodes.");
				System.out.println("Solution length: " + moveSolution.length());
				return moveSolution;
			}

			boolean foundBetterPath = false;
			parent.possibleBoxMoves(childStates);
			//parent.printState();
			
			for (BoardState child : childStates) {
				// have to check if in a board state visited before.
				if (closedset.contains(child)) { continue; }
				path.put(child, parent);
				debugNumNodes++;
				
				// Distance to goal for current child is ->
				// distance for parent to goal + distance from child to parent.
//				System.out.println("Parent");
//				parent.printState();
//				System.out.println("Child");
//				child.printState();
				Double graphDistance = g.get(parent) + child.backtrackMoves.size(); // TODO: setting dist(child,parent) = 1 here might be wrong...

				if (!openset.contains(child)) {
					openset.add(child);
					foundBetterPath = true;
				} else if (graphDistance < g.get(child)) {
					foundBetterPath = true;
				} else {
					foundBetterPath = false;
				}

				if (foundBetterPath) {
					g.put(child, graphDistance);
					h.put(child, Heuristics.goalDistance(child));
					f.put(child, (g.get(child) + h.get(child)));
				}
			}
		}

		// fail here w00t!
		throw new RuntimeException("Failed!");
	}


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


}
