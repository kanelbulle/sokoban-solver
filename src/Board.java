import java.util.Random;
import java.util.Vector;

public final class Board {
	public static final byte TYPE_FLOOR = (1 << 0);
	public static final byte TYPE_WALL = (1 << 1);
	public static final byte TYPE_GOAL = (1 << 2);
	public static final byte TYPE_DEAD = (1 << 3);

	private static final byte NOT_CORNERED = -1;
	private static final byte NW = 0;
	private static final byte NE = 1;
	private static final byte SW = 2;
	private static final byte SE = 3;

	public byte[][] boardData;
	public double[][] heuristicValues;
	public long[][] zValues;
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
		zValues = new long[lines.size() + 2][maxLength + 2];
		heuristicValues = new double[lines.size() + 2][maxLength + 2];
		
		BoardCoordinate playerCoordinate = null;
		Vector<BoardCoordinate> boxCoordinates = new Vector<BoardCoordinate>();
		
		Random random = new Random();
		// insert data from lines into matrix
		for (byte r = 1; r <= lines.size(); r++) {
			String line = lines.get(r - 1);
			for (byte c = 1; c <= line.length(); c++) {
				char character = line.charAt(c - 1);

				zValues[r][c] = random.nextLong();
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

		markDeadSquares();

		startState = new BoardState(this, playerCoordinate, boxCoordinates,
				BoardState.MOVE_NULL);
	}

	private final byte isCornered(byte r, byte c) {
		byte rMin1 = (byte) (r - 1);
		byte rPlus1 = (byte) (r + 1);
		byte cMin1 = (byte) (c - 1);
		byte cPlus1 = (byte) (c + 1);
		if (wallAt(rMin1, c) && wallAt(r, cMin1))
			return NW;
		if (wallAt(rMin1, c) && wallAt(r, cPlus1))
			return NE;
		if (wallAt(r, cPlus1) && wallAt(rPlus1, c))
			return SE;
		if (wallAt(r, cMin1) && wallAt(rPlus1, c))
			return SW;

		return NOT_CORNERED;
	}

	private void markDeadSquares() {
		for (byte row = 1; row < rows() - 1; row++) {
			for (byte column = 1; column < columns() - 1; column++) {
				if (!floorAt(row, column)) {
					continue;
				}

				byte isCornered = isCornered(row, column);
				if (isCornered != NOT_CORNERED) {
					if (goalAt(row, column)) {
						continue;
					}

					byte rd = 0, cd = 0;
					switch (isCornered) {
					case NW:
						rd = -1;
						cd = -1;
						break;
					case NE:
						rd = -1;
						cd = 1;
						break;
					case SW:
						rd = 1;
						cd = -1;
						break;
					case SE:
						rd = 1;
						cd = 1;
						break;
					}

					// the corner is certainly dead, so start with marking the
					// corner as dead square
					boardData[row][column] |= TYPE_DEAD;

					// starting at the corner, walk along the row
					// until wall is reached or an opening is reached
					byte currCol = column;
					while (currCol > 0 && currCol < columns()) {
						currCol -= cd;

						if (goalAt(row, currCol)) {
							break;
						} else if (wallAt(row, currCol)) {
							// end reached. all previously visited squares on
							// this row are dead squares
							// backtrack and mark dead
							for (byte c = (byte) (currCol + cd); c != column; c += cd) {
								boardData[row][c] |= TYPE_DEAD;
							}
							break;
						} else if (!wallAt((byte) (row + rd), currCol)) {
							// there is a hole in the limiting wall and no other
							// squares but the corner can be marked as dead
							// squares

							break;
						}
					}

					// starting at the corner, walk along the column
					// until wall is reached or an opening is reached
					byte currRow = row;
					while (currRow > 0 && currRow < rows()) {
						currRow -= rd;
						if (goalAt(currRow, column)) {
							break;
						} else if (wallAt(currRow, column)) {
							// end reached. all previously visited squares on
							// this row are dead squares
							// backtrack and mark dead
							for (byte r = (byte) (currRow + rd); r != row; r += rd) {
								boardData[r][column] |= TYPE_DEAD;
							}
							break;
						} else if (!wallAt(currRow, (byte) (column + cd))) {
							// there is a hole in the limiting wall and no other
							// squares but the corner can be marked as dead
							// squares

							break;
						}
					}
				}
			}
		}
	}

	public final byte columns() {
		return (byte) boardData[0].length;
	}

	public final byte dataAt(byte row, byte column) {
		return boardData[row][column];
	}

	public final boolean floorAt(byte row, byte column) {
		return (boardData[row][column] & TYPE_FLOOR) != 0;
	}

	public final boolean goalAt(byte row, byte column) {
		return boardData[row][column] == TYPE_GOAL;
	}

	public final boolean wallAt(byte row, byte column) {
		return boardData[row][column] == TYPE_WALL;
	}

	public final boolean deadAt(byte row, byte column) {
		return (boardData[row][column] & TYPE_DEAD) != 0;
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
				case TYPE_DEAD:
					System.out.print("x");
				}
			}
			System.out.print("\n");
		}
	}

	public final byte rows() {
		return (byte) boardData.length;
	}

	public final BoardState startState() {
		return startState;
	}

}
