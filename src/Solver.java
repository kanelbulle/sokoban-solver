
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;


public class Solver {

	public static HashMap<BoardState, BoardState> path = new HashMap<BoardState, BoardState>();
	
	public String solve(BoardState boardState) {
		int ret = AStar(boardState, null);
		return "Found: " + ret;
	}
	
	public static int AStar(BoardState start, BoardState goal) {
		
		System.out.println("Start state is: " + start);
		
		// list of nodes not yet expanded.
		PriorityQueue<BoardState> openset = new PriorityQueue<BoardState>();
		openset.add(start);
		// list of nodes we HAVE expanded (ie explored).
		PriorityQueue<BoardState> closedset = new PriorityQueue<BoardState>();
		
		HashMap<BoardState, Integer> g = new HashMap<BoardState, Integer>();
		HashMap<BoardState, Integer> h = new HashMap<BoardState, Integer>();
		HashMap<BoardState, Integer> f = new HashMap<BoardState, Integer>();
		
		g.put(start, 0);
		h.put(start, costEstimate(start, goal));
		f.put(start, h.get(start));
		
		Vector<BoardState> childStates = new Vector<BoardState>();
		
		while (openset.peek() != null) {
			
			System.out.println("asdf");
			
			boolean tentative_is_better = false;
			
			BoardState parent = openset.poll();
			
			if (parent.isSolved()) {
				System.out.println("Found goal state!");
				//return reconstruct_path(path, parent); // return solution.
			}
				
			parent.possibleMoves(childStates);

			for (BoardState child : childStates) {
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
					g.put(child, tentative_g_score);
					h.put(child, costEstimate(child, goal));
					f.put(child, (g.get(child) + h.get(child)));
				}
				
			}
		}
		
		// fail here
		return 0;
	}
	
	private static Integer costEstimate(BoardState start, BoardState goal) {
		return 1;
	}

	// rewrite to return path string rep?
	private static BoardState reconstruct_path(HashMap<BoardState, BoardState> path, BoardState current_node, ArrayList p) {
//		if (path.containsKey(current_node)) {
//			p.add(reconstruct_path(path, path.get(current_node), p));
//			return (p);
//		} else {
//			return current_node;
//		}
		return null;
	}

	private static int distance(BoardState parent, BoardState child) {
		return 0;
	}


}

