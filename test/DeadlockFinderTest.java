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
		//  #$  #
		//  #####
		//
		//  case 3 SNL
		//	####
		//  #$$#
		//  #$ #
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
		//	
		// @formatter:on 
		
		String case1 = "###\n#*#\n#.#\n#$#\n#@#\n###";
		String case2 = "#####\n#.$@#\n#$  #\n#####";
		String case3 = "####\n#$$#\n#$ #\n#. #\n#$ #\n#@ #\n####";
		String case4 = "###\n# #\n#*#\n#*#\n#.#\n#$#\n#@#\n###";
		testInBowl(case1);
		testInBowl(case2);
		testInBowl(case3);
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
