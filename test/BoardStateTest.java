import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;


public class BoardStateTest extends TestCase {

	@Test
	public void testEquals() {
		String boardString = "########\n#   # .#\n#   $$.#\n####   #\n   #@ ##\n   #### ";
		String[] lines = boardString.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);
		
		BoardState bs1 = board.startState();
		BoardState bs2 = new BoardState(bs1, new BoardCoordinate((byte) 4, (byte) 5), null, null, (byte) 0);
		
		BoardCoordinate bc1 = bs2.boxCoordinates.elementAt(0);
		BoardCoordinate bc2 = new BoardCoordinate(bc1.row, (byte) (bc1.column + 1));
		
		BoardState bs3 = new BoardState(bs1, new BoardCoordinate((byte) 4, (byte) 5), bc1, bc2, (byte) 0);
		
		assertNotNull(bs2);
		assertNotNull(bs3);
		
		assertNotNull(bs2.playerCoordinate);
		assertNotNull(bs2.boxCoordinates);
		assertNotNull(bs3.playerCoordinate);
		assertNotNull(bs3.boxCoordinates);
						
		assertFalse(bs2.equals(bs3));
		
		// TODO figure out why the hell this gives some weird null error
		assertFalse(bs3.equals(bs2));
		
		assertFalse(bs2.hashCode() == bs3.hashCode());
		
		BoardState bs2Copy = new BoardState(bs2, bs2.playerCoordinate, null, null, (byte) 0);
		assertTrue(bs2.equals(bs2Copy));
		assertTrue(bs2Copy.equals(bs2));
	}

}
