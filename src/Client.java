import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class Client {

	public static int currentBoard = 0;
	
	public static void main(String[] pArgs) {
		if (pArgs.length < 3) {
			System.out.println("usage: java Client host port boardnum");
			return;
		}

		pArgs[0] = "130.237.218.85";
		pArgs[1] = "7781";

		for (int n = 1; n < 12; n++) {
			pArgs[2] = "" + n;
			currentBoard = n;
			System.out.println("Trying board " + currentBoard);
			try {
				Socket lSocket = new Socket(pArgs[0],
						Integer.parseInt(pArgs[1]));
				PrintWriter lOut = new PrintWriter(lSocket.getOutputStream());
				BufferedReader lIn = new BufferedReader(new InputStreamReader(
						lSocket.getInputStream()));

				lOut.println(pArgs[2]);
				lOut.flush();

				String lLine = lIn.readLine();
				// read number of rows
				int lNumRows = Integer.parseInt(lLine);

				// read each row
				Vector<String> boardLines = new Vector<String>(lNumRows);
				for (int i = 0; i < lNumRows; i++) {
					lLine = lIn.readLine();
					boardLines.add(lLine);
				}

				Solver solver = new Solver();
				Board board = new Board(boardLines);

				String solution = solver.solve(board);

				lOut.println(solution);
				lOut.flush();

				// read answer from the server
				lLine = lIn.readLine();

				System.out.println(lLine);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
