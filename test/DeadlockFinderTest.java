import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;

public class DeadlockFinderTest extends TestCase {

	//private DeadlockFinder df = DeadlockFinder.getInstance();
	@Test
	public void testMain() {
		// @formatter:off
		//	Abbreviations: SNL = should not (dead)lock, SL = should (dead)lock.
		//	Legend: # = "wall", $ = box, * = box on goal, . = goal
		//		
		// 	case 1 SNL		
		//	###				####
		//	#*#				#
		//	#.#				#
		//	#$#
		//  #@#
		//  ###
		//
		//	case 2 SNL
		//	#####
		//	#.$@#
		//  #*  #
		//  #####
		//
		//  case 3 SNL
		//	####
		//  #**#
		//  #* #
		//  #. #
		//	#$ #
		//  #@ #
		//  ####
		//	
		//	case 4 SNL
		//	###
		//	# #
		//	#*#
		//	#*#
		//	#.#
		//  #$#
		//  #@#
		//	###
		//
		//	case 5 SL
		//	####
		//	# .#
		//	#$.#
		//	#$.#
		//	#$@#
		//	####
		//		
		//	case 6 SL
		//	####
		//	#. #
		//	#$ #
		//	#$@#
		//	#. #
		//	####
		//
		//	case 7 SL
		//	 	####
		//  	#..#
		//   ####$ #
		//	 #. $$@#
		//   #     #
		//	 #######	
		//
		//
		//
		// @formatter:on 

		case1();
		case2();
		case3();
		case4();
	}

	private void case1() {
		String case1 = "###\n#*#\n#.#\n#$#\n#@#\n###";
		testInBowl(case1);
	}

	private void case2() {
		String case2 = "#####\n#.$@#\n#$  #\n#####";
		testInBowl(case2);
	}

	private void case3() {
		String case3 = "####\n#$$#\n#$ #\n#. #\n#$ #\n#@ #\n####";
		testInBowl(case3);
	}

	private void case4() {
		String case4 = "###\n# #\n#*#\n#*#\n#.#\n#$#\n#@#\n###";
		testInBowl(case4);
	}

	public void testFreezeDeadlock1() {
		String mapIsDeadlock = "############\n#...  ##   #\n#.   $ $ @ #\n#.  $$     #\n#. $$      #\n####       #\n############\n";
		String[] lines = mapIsDeadlock.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);

		BoardState bs = board.startState();
		BoardState newState = new BoardState(bs, bs.playerCoordinate, new BoardCoordinate((byte) 3, (byte) 8), 
				new BoardCoordinate((byte) 3, (byte) 7), BoardState.MOVE_LEFT);

		assertTrue(DeadlockFinder.isFreezeDeadlock(newState));
	}

//	############
//	#...  ##   #
//	#.   $ $ @ #
//	#.  $$     #
//	#. $$      #
//	###        #
//	############
	public void testFreezeDeadlock2() {
		String mapIsDeadlock = "############\n#...  ##   #\n#.   $ $ @ #\n#.  $$     #\n#. $$      #\n###        #\n############\n";
		String[] lines = mapIsDeadlock.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);

		BoardState bs = board.startState();
		BoardState newState = new BoardState(bs, bs.playerCoordinate, new BoardCoordinate((byte) 3, (byte) 8), 
				new BoardCoordinate((byte) 3, (byte) 7), BoardState.MOVE_LEFT);

		assertFalse(DeadlockFinder.isFreezeDeadlock(newState));
	}

	public void testFreezeDeadlock3() {
		String mapIsDeadlock = "#######\n#. $@ #\n#.$   #\n#######";
		String[] lines = mapIsDeadlock.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);

		BoardState bs = board.startState();
		BoardState newState = new BoardState(bs, bs.playerCoordinate, new BoardCoordinate((byte) 2, (byte) 4), 
				new BoardCoordinate((byte) 2, (byte) 3), BoardState.MOVE_LEFT);
		assertFalse(DeadlockFinder.isFreezeDeadlock(newState));
	}

	public void testFreezeDeadlock4() {
		String mapIsDeadlock = "#########\n#  #    #\n###@## ##\n# # #   #\n#  $   $#\n# $ $.**#\n# ###.#*#\n#    .*.#\n#########";
		String[] lines = mapIsDeadlock.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);
		Vector<BoardState> bs = new Vector<BoardState>();
		board.startState().possibleBoxMoves(bs);
		
		assertTrue(bs.size() == 0);
	}
	
	public void testFreezeDeadlock5() {
		String mapIsDeadlock = "      x##### \n########xxx# \n#x****..$$x# \n#x # ###@ x# \n##    x#  x# \nx#    x#xxx# \n #xxxxx##### \n #######x";
		String[] lines = mapIsDeadlock.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);
		
		assertFalse(DeadlockFinder.isFreezeDeadlock(board.startState()));
	}

//	/*  ######
//		#@$ .#  
//		###### */
//	public void testBipartiteDeadlock1() {
//		String mapIsDeadlock = "######\n#@$ .#\n######";
//		String[] lines = mapIsDeadlock.split("\n");
//		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
//		Board board = new Board(vLines);
//
//		BoardState bs = board.startState();
//		assertFalse(DeadlockFinder.isBipartiteMatchDeadlock(bs));
//	}
//
//	/*  ######
//		#@$#.# mega deadlock 
//		###### */
//	public void testBipartiteDeadlock2() {
//		String mapIsDeadlock = "######\n#@$#.#\n######";
//		String[] lines = mapIsDeadlock.split("\n");
//		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
//		Board board = new Board(vLines);
//
//		BoardState bs = board.startState();
//		assertTrue(DeadlockFinder.isBipartiteMatchDeadlock(bs));
//	}
//
//	/* 	######
//		#@$ .#
//		# $ .#
//		###### */
//	public void testBipartiteDeadlock3() {
//		String mapIsDeadlock = "######\n#@$ .#\n# $ .#\n######";
//		String[] lines = mapIsDeadlock.split("\n");
//		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
//		Board board = new Board(vLines);
//
//		BoardState bs = board.startState();
//		assertFalse(DeadlockFinder.isBipartiteMatchDeadlock(bs));
//	}
//
//
//	/* 	#######
//		#@$ . #
//		#   . #
//		# ### #	is deadlock, box on goal could have started int (3,3)
//		# $  *#
//		####### */
//	public void testBipartiteDeadlock4() {
//		String mapIsDeadlock = "#######\n#@$ . #\n#   . #\n# ### #\n# $  *#\n#######";
//		String[] lines = mapIsDeadlock.split("\n");
//		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
//		Board board = new Board(vLines);
//
//		BoardState bs = board.startState();
//		assertTrue(DeadlockFinder.isBipartiteMatchDeadlock(bs));
//	}
	
	
	private void testInBowl(String boardString) {
		String[] lines = boardString.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);

		BoardState bs1 = board.startState();
		assertFalse("Detected deadlock in board that isn't a deadlock", DeadlockFinder.isDeadLock(bs1));
	}
	
	@Test
	public void testIsBipartite() {
		String mapIsDeadlock = "################### \n#x   $   .........# \n#x$ $#$$ $ $ $ $ @# \n#xxxxxx############ \n########x";
		String[] lines = mapIsDeadlock.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);
		
		assertFalse(DeadlockFinder.isDeadLock(board.startState()));
	}

	@Test
	public void testIsDeadLock() throws IOException {
		FileInputStream fis = new FileInputStream("deadlock-cases.txt");
		DataInputStream in = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		boolean shouldDeadlock;
		Vector<String> lines = new Vector<String>();
		int correctIdentified = 0;
		int total = 0;
		while ((line = br.readLine()) != null) {
			lines.clear();
			if (line.startsWith("DEADLOCK")) {
				shouldDeadlock = true;
			} else if (line.startsWith("NOT DEADLOCK")) {
				shouldDeadlock = false;
			} else {
				continue;
			}

			while ((line = br.readLine()) != null) {
				if (line.length() < 2) {
					break;
				}

				lines.add(line);
			}

			total++;

			Board board = new Board(lines);
//			System.out.println((shouldDeadlock ? "DEADLOCK" : "NOT DEADLOCK"));
//			board.startState().printState();

			boolean result = false;
			for (BoardCoordinate bc : board.startState().boxCoordinates) {
				if (board.deadAt(bc.row, bc.column)) {
					result = true;
					break;
				}
			}
			
			if (!result) result = DeadlockFinder.isDeadLock(board.startState());
			
//			if (result) {
//				System.out.print("identified as deadlock");
//			} else {
//				System.out.print("identified as not deadlock");
//			}
//			
//			System.out.println("\n\n\n");

			if (result == shouldDeadlock) {
				correctIdentified++;
			}
		}

//		System.out.println("Identified " + correctIdentified + " of " + total + " deadlocks");
		assertEquals(correctIdentified, total);

		in.close();
	}
}
