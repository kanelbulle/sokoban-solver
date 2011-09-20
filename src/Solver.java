
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;


public class Solver {

	public static HashMap<State, State> path = new HashMap<State, State>();
	
	public static int AStar(State start, State goal) {
		
		// list of nodes not yet expanded.
		PriorityQueue<State> openset = new PriorityQueue<State>();
		// list of nodes we HAVE expanded (ie explored).
		PriorityQueue<State> closedset = new PriorityQueue<State>();
		
		HashMap<State, Integer> g = new HashMap<State, Integer>();
		HashMap<State, Integer> h = new HashMap<State, Integer>();
		HashMap<State, Integer> f = new HashMap<State, Integer>();
		
		while (openset.peek() != null) {
			
			boolean tentative_is_better = false;
			
			State parent = openset.poll();
			
			if (isGoal(parent)) {
				System.out.println("Found goal state!");
				//return reconstruct_path(path, parent); // return solution.
			}
				
			Vector<State> children = parent.findPossibleChildStates();

			for (State child : children) {
				 // have to check if in a board state visited before.
				if (closedset.contains(child))
					continue;

				int tentative_g_score = g.get(child) + distance(parent, child);
				
				if (!openset.contains(child)) {
					openset.add(child);
					tentative_is_better = true;
				} else if (tentative_g_score < g.get(child))
					tentative_is_better = true;
				else 
					tentative_is_better = false;
				
				if (tentative_is_better) {
					path.put(child, parent);
				}
			}
		}
		
		// fail here
		return 0;
	}
	
	// rewrite to return path string rep?
	private static State reconstruct_path(HashMap<State, State> path, State current_node, ArrayList p) {
//		if (path.containsKey(current_node)) {
//			p.add(reconstruct_path(path, path.get(current_node), p));
//			return (p);
//		} else {
//			return current_node;
//		}
		return null;
	}

	private static int distance(State parent, State child) {
		return 0;
	}

	private static boolean isGoal(State state) {
		return false;
	}

