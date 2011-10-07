import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;


public class BoardTest extends TestCase {

	@Test
	public void testMarkDeadSquares() {
		System.out.println("baj");
		String boardString = "       #####\n######## . #\n#@......$  #\n#  #$###  .#\n## $ $ #   #\n # $ $ #   #\n #     #####\n #######\n";
		String[] lines = boardString.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);
		
		board.print();
	}

}
