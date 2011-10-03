
import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

import junit.framework.TestCase;


public class HeuristicsCase extends TestCase {

	@Test
	public void testDistanceSum() {
		String boardString = "#########\n# ...   #\n# $     #\n#  $    #\n#   $   #\n#     @ #\n#       #\n#########\n";
		Vector<String> boardLines = new Vector<String>(Arrays.asList(boardString.split("\n")));
		Board board = new Board(boardLines);
		
		double result = Heuristics.goalDistance(board.startState());
		System.out.println("result: " + result);
		assertTrue("result is not between 7 and 9", 7 < result && result < 9);
	}

}
