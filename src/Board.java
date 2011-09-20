import java.util.Vector;

public final class Board {
	public static final byte TYPE_NOTHING = 0;
	public static final byte TYPE_WALL = 0x23;
	public static final byte TYPE_PLAYER = 0x40;
	public static final byte TYPE_PLAYER_ON_GOAL = 0x2b;
	public static final byte TYPE_BOX = 0x24;
	public static final byte TYPE_BOX_ON_GOAL = 0x2a;
	public static final byte TYPE_GOAL_SQUARE = 0x2e;
	public static final byte TYPE_FLOOR = 0x20;

	private byte[][] boardData;
	Vector<BoardCoordinate> goalPositions = new Vector<BoardCoordinate>();
	private BoardState startState;

	public Board(Vector<String> lines) {
		// determine maximum length of lines
		int maxLength = -1;
		for (String l : lines) {
			if (l.length() > maxLength) {
				maxLength = l.length();
			}
		}

		// create empty board data matrix
		boardData = new byte[lines.size()+2][maxLength+2];

		BoardCoordinate playerCoordinate = null;
		Vector<BoardCoordinate> boxCoordinates = new Vector<BoardCoordinate>();
		// insert data from lines into matrix
		for (byte r = 1; r <= lines.size(); r++) {
			String line = lines.get(r);
			for (byte c = 1; c <= line.length(); c++) {
				char character = line.charAt(c);
				boardData[r][c] = (byte) character;

				switch (character) {
				case TYPE_GOAL_SQUARE:
				case TYPE_PLAYER_ON_GOAL:
				case TYPE_BOX_ON_GOAL:
					goalPositions.add(new BoardCoordinate(r, c));
					boxCoordinates.add(new BoardCoordinate(r, c));
					break;
				case TYPE_NOTHING:
				case TYPE_WALL:
				case TYPE_PLAYER:
					playerCoordinate = new BoardCoordinate(r, c);
					break;
				case TYPE_BOX:
					boxCoordinates.add(new BoardCoordinate(r, c));
					break;
				case TYPE_FLOOR:
					break;
				}
			}
		}

		startState = new BoardState(this, playerCoordinate, boxCoordinates);
	}

	public final byte dataAt(byte row, byte column) {
		return boardData[row][column];
	}

	public final byte rows() {
		return (byte) boardData.length;
	}

	public final byte columns() {
		return (byte) boardData[0].length;
	}

	public final BoardState startState() {
		return startState;
	}

}
