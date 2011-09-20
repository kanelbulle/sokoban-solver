import java.util.Vector;

public final class Board {
	public static final byte TYPE_FLOOR = 1;
	public static final byte TYPE_WALL = (1 << 1);
	public static final byte TYPE_GOAL = (1 << 2);

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
		boardData = new byte[lines.size() + 2][maxLength + 2];

		BoardCoordinate playerCoordinate = null;
		Vector<BoardCoordinate> boxCoordinates = new Vector<BoardCoordinate>();
		// insert data from lines into matrix
		for (byte r = 1; r <= lines.size(); r++) {
			String line = lines.get(r - 1);
			for (byte c = 1; c <= line.length(); c++) {
				char character = line.charAt(c - 1);
				
				boardData[r][c] = TYPE_FLOOR;
				switch (character) {
				case '#':
					boardData[r][c] = TYPE_WALL;
					break;
				case '@':
					playerCoordinate = new BoardCoordinate(r, c);
					break;
				case '+':
					boardData[r][c] = TYPE_GOAL;
					goalPositions.add(new BoardCoordinate(r, c));
					playerCoordinate = new BoardCoordinate(r, c);
					break;
				case '$':
					boxCoordinates.add(new BoardCoordinate(r, c));
					break;
				case '*':
					boardData[r][c] = TYPE_GOAL;
					goalPositions.add(new BoardCoordinate(r, c));
					boxCoordinates.add(new BoardCoordinate(r, c));
					break;
				case '.':
					boardData[r][c] = TYPE_GOAL;
					goalPositions.add(new BoardCoordinate(r, c));
					break;
				case ' ':
					boardData[r][c] = TYPE_FLOOR;
					break;
				}
			}
		}

		startState = new BoardState(this, playerCoordinate, boxCoordinates, (byte) -1);

		print();
	}

	public final byte dataAt(byte row, byte column) {
		return boardData[row][column];
	}

	public final boolean goalAt(byte row, byte column) {
		return boardData[row][column] == TYPE_GOAL;
	}

	public final boolean wallAt(byte row, byte column) {
		return boardData[row][column] == TYPE_WALL;
	}

	public final boolean floorAt(byte row, byte column) {
		return boardData[row][column] == TYPE_FLOOR;
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

	public void print() {
		for (byte r = 0; r < rows(); r++) {
			for (byte c = 0; c < columns(); c++) {
				switch (dataAt(r, c)) {
				case TYPE_FLOOR:
					System.out.print(" ");
					break;
				case TYPE_GOAL:
					System.out.print(".");
					break;
				case TYPE_WALL:
					System.out.print("#");
					break;
				}
			}
			System.out.print("\n");
		}
	}
}
