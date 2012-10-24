///////////////////////////////////////////////////////////////////////////////////////////////
//	This is my tests, please make sure Board constructor loads "legend.txt", "Clue Layout.csv"
//////////////////////////////////////////////////////////////////////////////////////////////
package Test;

import java.util.LinkedList;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTests {
	private static Board board;
	@BeforeClass
	public static void setUp() {
		board = new Board();
	}

	// Ensure that player does not move around within room
	@Test
	public void testAdjacenciesInsideRooms()
	{
		// Test a corner
		LinkedList<Integer> testList = board.getAdjList(board.calcIndex(0, 0));
		Assert.assertEquals(0, testList.size());
		// Test one that has walkway underneath
		testList = board.getAdjList(board.calcIndex(5, 0));
		Assert.assertEquals(0, testList.size());
		// Test one that has walkway above
		testList = board.getAdjList(board.calcIndex(7, 7));
		Assert.assertEquals(0, testList.size());
		// Test one that is in middle of room
		testList = board.getAdjList(board.calcIndex(16, 1));
		Assert.assertEquals(0, testList.size());
		// Test one beside a door
		testList = board.getAdjList(board.calcIndex(7, 10));
		Assert.assertEquals(0, testList.size());
		// Test one in a corner of room
		testList = board.getAdjList(board.calcIndex(15, 14));
		Assert.assertEquals(0, testList.size());
	}

	// Ensure that the adjacency list from a doorway is only the
	// walkway. NOTE: This test could be merged with door 
	// direction test. 
	@Test
	public void testAdjacencyRoomExit()
	{
		// TEST DOORWAY LEFT 
		LinkedList<Integer> testList = board.getAdjList(board.calcIndex(7, 14));
		Assert.assertEquals(1, testList.size());
		Assert.assertTrue(testList.contains(board.calcIndex(7, 13)));
		// TEST DOORWAY RIGHT
		testList = board.getAdjList(board.calcIndex(0, 11));
		Assert.assertEquals(1, testList.size());
		Assert.assertTrue(testList.contains(board.calcIndex(0, 12)));
		//TEST DOORWAY DOWN
		testList = board.getAdjList(board.calcIndex(13, 8));
		Assert.assertEquals(1, testList.size());
		Assert.assertTrue(testList.contains(board.calcIndex(14, 8)));
		//TEST DOORWAY UP
		testList = board.getAdjList(board.calcIndex(15, 15));
		Assert.assertEquals(1, testList.size());
		Assert.assertTrue(testList.contains(board.calcIndex(14, 15)));
		
	}

	// Test a variety of walkway scenarios
	@Test
	public void testAdjacencyWalkways()
	{
		// Test on top edge of board, 2 walkway pieces
		LinkedList<Integer> testList = board.getAdjList(board.calcIndex(0, 4));
		Assert.assertTrue(testList.contains(5));
		Assert.assertTrue(testList.contains(board.calcIndex(1, 4)));
		Assert.assertTrue(testList.contains(board.calcIndex(0, 3)));
		Assert.assertEquals(3, testList.size());
		
		// Test on left edge of board, 1 walkway piece
		testList = board.getAdjList(board.calcIndex(6, 0));
		Assert.assertTrue(testList.contains(board.calcIndex(6, 1)));
		Assert.assertEquals(1, testList.size());

		// Test between two rooms, walkways up and down
		testList = board.getAdjList(board.calcIndex(17, 16));
		Assert.assertTrue(testList.contains(board.calcIndex(16, 16)));
		Assert.assertTrue(testList.contains(board.calcIndex(18, 16)));
		Assert.assertEquals(2, testList.size());

		// Test surrounded by 4 walkways
		testList = board.getAdjList(board.calcIndex(12,4));
		Assert.assertTrue(testList.contains(board.calcIndex(11, 4)));
		Assert.assertTrue(testList.contains(board.calcIndex(13, 4)));
		Assert.assertTrue(testList.contains(board.calcIndex(12, 3)));
		Assert.assertTrue(testList.contains(board.calcIndex(12, 5)));
		Assert.assertEquals(4, testList.size());
		
		// Test on bottom edge of board, next to 2 room piece
		testList = board.getAdjList(board.calcIndex(19, 5));
		Assert.assertTrue(testList.contains(board.calcIndex(18, 5)));
		Assert.assertTrue(testList.contains(board.calcIndex(19, 4)));
		Assert.assertEquals(2, testList.size());
		
		// Test on ridge edge of board, next to 1 room piece
		testList = board.getAdjList(board.calcIndex(6, 19));
		Assert.assertTrue(testList.contains(board.calcIndex(6, 18)));
		Assert.assertEquals(1, testList.size());

	}

	// Test adjacency at entrance to rooms
	@Test
	public void testAdjacencyDoorways()
	{
		// Test beside a door direction RIGHT
		LinkedList<Integer> testList = board.getAdjList(board.calcIndex(7, 12));
		Assert.assertTrue(testList.contains(board.calcIndex(7, 11)));
		Assert.assertTrue(testList.contains(board.calcIndex(7, 13)));
		Assert.assertTrue(testList.contains(board.calcIndex(6, 12)));
		Assert.assertTrue(testList.contains(board.calcIndex(8, 12)));
		Assert.assertEquals(4, testList.size());
		// Test beside a door direction DOWN
		testList = board.getAdjList(board.calcIndex(14, 8));
		Assert.assertTrue(testList.contains(board.calcIndex(14, 9)));
		Assert.assertTrue(testList.contains(board.calcIndex(14, 7)));
		Assert.assertTrue(testList.contains(board.calcIndex(13, 8)));
		Assert.assertTrue(testList.contains(board.calcIndex(15, 8)));
		Assert.assertEquals(4, testList.size());
		// Test beside a door direction LEFT
		testList = board.getAdjList(board.calcIndex(0, 13));
		Assert.assertTrue(testList.contains(board.calcIndex(0, 14)));
		Assert.assertTrue(testList.contains(board.calcIndex(0, 12)));
		Assert.assertTrue(testList.contains(board.calcIndex(1, 13)));
		Assert.assertEquals(3, testList.size());
		// Test beside a door direction UP
		testList = board.getAdjList(board.calcIndex(14, 15));
		Assert.assertTrue(testList.contains(board.calcIndex(14, 16)));
		Assert.assertTrue(testList.contains(board.calcIndex(14, 14)));
		Assert.assertTrue(testList.contains(board.calcIndex(13, 15)));
		Assert.assertTrue(testList.contains(board.calcIndex(15, 15)));
		Assert.assertEquals(4, testList.size());
	}

	
	// Tests of just walkways, 1 step, includes on edge of board
	// and beside room
	// Have already tested adjacency lists on all four edges, will
	// only test two edges here
	@Test
	public void testTargetsOneStep() {
		board.calcTargets(board.calcIndex(19, 13), 1);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(2, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(18, 13))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(19, 12))));	
		
		board.calcTargets(board.calcIndex(12, 0), 1);
		targets= board.getTargets();
		Assert.assertEquals(1, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(12, 1))));
	}
	// Tests of just walkways, 2 steps
	@Test
	public void testTargetsTwoSteps() {
		board.calcTargets(board.calcIndex(10, 19), 2);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(1, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(11, 18))));
		
		board.calcTargets(board.calcIndex(19, 4), 2);
		targets= board.getTargets();
		Assert.assertEquals(2, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(18, 5))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(17, 4))));			
	}
	// Tests of just walkways, 4 steps
	@Test
	public void testTargetsFourSteps() {
		board.calcTargets(board.calcIndex(6, 17), 4);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(1, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(6, 13))));
		
		// Includes a path that doesn't have enough length
		board.calcTargets(board.calcIndex(11, 12), 4);
		targets= board.getTargets();
		Assert.assertEquals(11, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(7, 12))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(8, 13))));	
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(10, 13))));	
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(9, 12))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(12, 13))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(13, 12))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 13))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(13, 14))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(13, 10))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 11))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(15, 12))));
	}	
	// Tests of just walkways plus one door, 6 steps
	@Test
	public void testTargetsSixSteps() {
		board.calcTargets(board.calcIndex(18, 16), 6);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(4, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(15, 15))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(13, 15))));	
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 14))));	
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(12, 16))));	
	}	
	
	// Test getting into a room
	@Test 
	public void testTargetsIntoRoom()
	{
		// One room is exactly 2 away
		board.calcTargets(board.calcIndex(15, 5), 2);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(7, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(15, 3))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 4))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(16, 4))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(17, 5))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(13, 5))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 6))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(15, 7))));
	}
	
	// Test getting into room, doesn't require all steps
	@Test
	public void testTargetsIntoRoomShortcut() 
	{
		board.calcTargets(board.calcIndex(7, 4), 2);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(7, 3))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(6, 3))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(5, 4))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(6, 5))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(8, 5))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(9, 4))));
		
		board.calcTargets(board.calcIndex(0, 12), 4);
		targets= board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(3, 13))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(2, 12))));	
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(1, 13))));	
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(4, 12))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(0, 11))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(0, 14))));
	}

	// Test getting out of a room
	@Test
	public void testRoomExit()
	{
		// Take one step, essentially just the adj list
		board.calcTargets(board.calcIndex(15, 11), 1);
		Set<BoardCell> targets= board.getTargets();
		// Ensure doesn't exit through the wall
		Assert.assertEquals(1, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(15, 12))));
		// Take two steps
		board.calcTargets(board.calcIndex(15, 11), 2);
		targets= board.getTargets();
		Assert.assertEquals(3, targets.size());
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 12))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(15, 13))));
		Assert.assertTrue(targets.contains(board.getCellAt(board.calcIndex(16, 12))));
	}

}
