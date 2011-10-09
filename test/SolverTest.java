
import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

import junit.framework.TestCase;


public class SolverTest extends TestCase {

	@Test
	public void testSolve() {
		String boardString = "#####\n#@$.#\n#####";
		String[] lines = boardString.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);
		
		Solver solver = new Solver();
		String solution = solver.solve(board);
		
		System.out.println(solution);
		assertTrue("solution is incorrect", solution.equalsIgnoreCase("3"));
		
		boardString = "#####\n#.$ #\n# @ #\n# $.#\n#####";
		lines = boardString.split("\n");
		vLines = new Vector<String>(Arrays.asList(lines));
		board = new Board(vLines);
		
		solver = new Solver();
		solution = solver.solve(board);
		System.out.println(solution);
		
		
	}

	@Test
	public void testSolve2() {
		String boardString = "#######\n#@    #\n#  $ .#\n#######";
		String[] lines = boardString.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);
		
		Solver solver = new Solver();
		String solution = solver.solve(board);
		System.out.println(solution);		
	}
}
