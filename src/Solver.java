import java.util.Vector;

final public class Solver {

	public final String solve(Vector<String> boardLines) {
		Board board = new Board(boardLines);
		
		return "U R R U U L D L L U L L D R R R R L D D R U R U D L L U R";
	}
}
