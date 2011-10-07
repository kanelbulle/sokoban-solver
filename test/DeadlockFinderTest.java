import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;

public class DeadlockFinderTest extends TestCase {

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

	private void testInBowl(String boardString) {
		String[] lines = boardString.split("\n");
		Vector<String> vLines = new Vector<String>(Arrays.asList(lines));
		Board board = new Board(vLines);

		BoardState bs1 = board.startState();
		assertFalse(DeadlockFinder.isDeadLock(bs1));

	}

}
