import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class Client {

	public static void main(String[] pArgs) {
		if (pArgs.length < 3) {
			System.out.println("usage: java Client host port boardnum");
			return;
		}
		
		
		int startBoard = 1;
		int endBoard = 1;
		if (pArgs[2].contains("-")) {
			try {
				String[] ps = pArgs[2].split("-");
				startBoard = Integer.parseInt(ps[0]);
				endBoard = Integer.parseInt(ps[1]);
			} catch (Exception e) {
				
			}
		} else {
			try {
				startBoard = Integer.parseInt(pArgs[2]);
				endBoard = startBoard;
			} catch (Exception e) {
				
			}
		}
		
		boolean result[] = new boolean[endBoard - startBoard + 1];
		for (int n = startBoard; n <= endBoard; n++) {
			pArgs[2] = "" + n;
			System.out.println("Trying board " + n);
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
				if (solution.length() > 0) {
					result[n-startBoard] = true;
				}
				lOut.println(solution);
				lOut.flush();

				// read answer from the server
				lLine = lIn.readLine();

				System.out.println(lLine);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		int count = 0;
		for (int n = 0; n < endBoard - startBoard + 1; n++) {
			System.out.println("Board " + (n + startBoard) + ": " + (result[n] ? "pass" : "fail"));
			if (result[n]) count++;
		}
		
		System.out.println("Completed " + count + " out of " + (endBoard - startBoard + 1) + " boards");
	}
}
