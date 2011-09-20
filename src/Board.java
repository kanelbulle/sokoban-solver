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



	public Board(Vector<String> lines) {
		// determine maximum length of lines
		int maxLength = -1;
		for (String l : lines) {
			if (l.length() > maxLength) {
				maxLength = l.length();
			}
		}

		// create empty board data matrix
		boardData = new byte[lines.size()][maxLength];

		// insert data from lines into matrix
		for (int r = 0; r < lines.size(); r++) {
			String line = lines.get(r);
			for (int c = 0; c < line.length(); c++) {
				char character = line.charAt(c);
				boardData[r][c] = (byte) character;

				switch (character) {
				case TYPE_GOAL_SQUARE:
				case TYPE_PLAYER_ON_GOAL:
				case TYPE_BOX_ON_GOAL:
					goalPositions.add(new BoardCoordinate((byte) r, (byte) c));
					break;
				case TYPE_NOTHING:
				case TYPE_WALL:
				case TYPE_PLAYER:
				case TYPE_BOX:
				case TYPE_FLOOR:
					break;
				}
			}
		}
	}

	public final byte dataAt(byte row, byte column) {
		return boardData[row][column];
	}

}
