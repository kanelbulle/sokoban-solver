import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;

public class Solver {

	public String solve(Board initialBoard) {
		//return naivSolver(initialBoard.startState);
		BoardState start = initialBoard.startState();
		return AStar(start);
	}

	public static String naivSolver(BoardState start) {

		LinkedList<BoardState> queue = new LinkedList<BoardState>();
		HashSet<BoardState> visitedStates = new HashSet<BoardState>();
		Vector<BoardState> childStates = new Vector<BoardState>();

		queue.add(start);
		
		while (!queue.isEmpty()) {
			BoardState parent = queue.poll();
			parent.possibleMoves(childStates);
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

	/** 
	Uses the heuristic f(x) = g(x) + h(x) 
	where:	g(x) is the node distance in the search graph from start to state 
			h(x) is a heuristic for approximating the distance form some board state to goal state
	*/
	public static String AStar(BoardState start) {
		/* List of nodes not yet expanded.
		The open list contains the cells that may fall on the optimal path we want. 
		In other words, the open list contains the cells we need to take a closer 
		look at in our search process. */
		PriorityQueue<BoardState> openset = new PriorityQueue<BoardState>();
		/* List of nodes we HAVE expanded (ie explored). */
		HashSet<BoardState> closedset = new HashSet<BoardState>();
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
		while ((parent = openset.poll()) != null) {	
			closedset.add(parent);
		
			if (parent.isSolved()) {
				//return constructPath(path, parent);
				BoardState bsParent = parent;
				String moveSolution = "";
				while (bsParent.lastMove != BoardState.MOVE_NULL) {
					moveSolution = "" + bsParent.lastMove + moveSolution;
					bsParent = bsParent.parent;
				}
				return moveSolution;
			}
			
			boolean foundBetterPath = false;
			parent.possibleMoves(childStates);
			
			for (BoardState child : childStates) {
				// have to check if in a board state visited before.
				if (closedset.contains(child)) { continue; }
				path.put(child, parent);
						
				// Distance to goal for current child is ->
				// distance for parent to goal + distance from child to parent.
				Double graphDistance = g.get(parent) + 1; // TODO: setting dist(child,parent) = 1 here might be wrong...

				if (!openset.contains(child)) {
					openset.add(child);
					foundBetterPath = true;
				} else if (graphDistance < g.get(parent)) {
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
		return "Failure!";
	}
}
