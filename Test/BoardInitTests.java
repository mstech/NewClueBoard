///////////////////////////////////////////////////////////////////////////////////////////////
//	This is my tests, please make sure Board constructor loads "legend.txt", "Clue Layout.csv"
//////////////////////////////////////////////////////////////////////////////////////////////
package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.RoomCell;

public class BoardInitTests {

	private static Board board;
	public static final int NUM_ROOMS = 11;
	public static final int NUM_ROWS = 20;
	public static final int NUM_COLUMNS = 20;
	
	@BeforeClass
	public static void setUp() {
		board = new Board();	
	}
	
	@Test
	public void testRooms() {
		Map<Character, String> rooms = board.getRooms();
		// Check for the correct numbers of rooms.
		assertEquals(NUM_ROOMS, rooms.size());
		// Check for the correct room type.
		assertEquals("Conservatory", rooms.get('C'));
		assertEquals("Kitchen", rooms.get('K'));
		assertEquals("Ballroom", rooms.get('B'));
		assertEquals("Billiard room", rooms.get('R'));
		assertEquals("Library", rooms.get('L'));
		assertEquals("Study", rooms.get('S'));
		assertEquals("Dining room", rooms.get('D'));
		assertEquals("Lounge", rooms.get('O'));
		assertEquals("Hall", rooms.get('H'));
		assertEquals("Closet", rooms.get('X'));
		assertEquals("Walkway", rooms.get('W'));
	}
	
	@Test
	public void testBoardDimensions() {
		// Check for the correct board dimensions.
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());		
	}
	
	// Test a doorway in each direction, plus two cell that is not
	// a doorway
	@Test
	public void testDoorDirections() {
		// Test every room's door.
		// Check the conservatory's door.
		RoomCell room = board.getRoomCellAt(0, 3);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.RIGHT, room.getDoorDirection());
		// Check the lounge's door.
		room = board.getRoomCellAt(0, 11);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.RIGHT, room.getDoorDirection());
		// Check the library's door.
		room = board.getRoomCellAt(0, 14);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.LEFT, room.getDoorDirection());
		// Check the study's door.
		room = board.getRoomCellAt(7, 3);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.RIGHT, room.getDoorDirection());
		// Check the hall's door.
		room = board.getRoomCellAt(7, 11);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.RIGHT, room.getDoorDirection());
		// Check the hall's second door.
		room = board.getRoomCellAt(13, 8);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.DOWN, room.getDoorDirection());
		// Check the dining room's door.
		room = board.getRoomCellAt(7, 14);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.LEFT, room.getDoorDirection());
		// Check the ballroom's door
		room = board.getRoomCellAt(15, 3);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.RIGHT, room.getDoorDirection());
		// Check the kitchen's door.
		room = board.getRoomCellAt(15, 11);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.RIGHT, room.getDoorDirection());
		// Check the billiard room's door.
		room = board.getRoomCellAt(15, 15);
		assertTrue(room.isDoorway());
		assertEquals(RoomCell.DoorDirection.UP, room.getDoorDirection());
		// Test that room pieces that aren't doors know it
		room = board.getRoomCellAt(0, 0);
		assertFalse(room.isDoorway());	
		// Test that walkways are not doors
		BoardCell cell = board.getCellAt(board.calcIndex(6, 0));
		assertFalse(cell.isDoorway());		

	}
	
	// Test that we have the correct number of doors
	@Test
	public void testNumberOfDoorways() 
	{
		int numDoors = 0;
		int totalCells = board.getNumColumns() * board.getNumRows();
		Assert.assertEquals(400, totalCells);
		for (int i=0; i<totalCells; i++)
		{
			BoardCell cell = board.getCellAt(i);
			if (cell.isDoorway())
				numDoors++;
		}
		Assert.assertEquals(10, numDoors);
	}

	
	@Test
	public void testCalcIndex() {
		// Test each corner of the board
		assertEquals(0, board.calcIndex(0, 0));
		assertEquals(NUM_COLUMNS-1, board.calcIndex(0, NUM_COLUMNS-1));
		assertEquals(380, board.calcIndex(NUM_ROWS-1, 0));
		assertEquals(399, board.calcIndex(NUM_COLUMNS-1, NUM_ROWS-1));
		// Test a couple others
		assertEquals(210, board.calcIndex(10, 10));
		assertEquals(302, board.calcIndex(15, 2));		
	}
	
	// Test the corners of each room to make sure produce the correct room's initial.
	@Test
	public void testRoomInitials() {
		assertEquals('C', board.getRoomCellAt(0, 0).getInitial());
		assertEquals('C', board.getRoomCellAt(5, 0).getInitial());
		assertEquals('C', board.getRoomCellAt(5, 3).getInitial());
		assertEquals('C', board.getRoomCellAt(0, 3).getInitial());
		assertEquals('O', board.getRoomCellAt(0, 6).getInitial());
		assertEquals('O', board.getRoomCellAt(5, 6).getInitial());
		assertEquals('O', board.getRoomCellAt(5, 11).getInitial());
		assertEquals('O', board.getRoomCellAt(0, 11).getInitial());
		assertEquals('L', board.getRoomCellAt(0, 14).getInitial());
		assertEquals('L', board.getRoomCellAt(5, 14).getInitial());
		assertEquals('L', board.getRoomCellAt(5, 19).getInitial());
		assertEquals('L', board.getRoomCellAt(0, 19).getInitial());
		assertEquals('S', board.getRoomCellAt(7, 0).getInitial());
		assertEquals('S', board.getRoomCellAt(11, 0).getInitial());
		assertEquals('S', board.getRoomCellAt(11, 3).getInitial());
		assertEquals('S', board.getRoomCellAt(7, 3).getInitial());
		assertEquals('H', board.getRoomCellAt(7, 6).getInitial());
		assertEquals('H', board.getRoomCellAt(13, 6).getInitial());
		assertEquals('H', board.getRoomCellAt(13, 8).getInitial());
		assertEquals('H', board.getRoomCellAt(12, 11).getInitial());
		assertEquals('H', board.getRoomCellAt(7, 11).getInitial());
		assertEquals('D', board.getRoomCellAt(7, 14).getInitial());
		assertEquals('D', board.getRoomCellAt(12, 14).getInitial());
		assertEquals('D', board.getRoomCellAt(12, 15).getInitial());
		assertEquals('D', board.getRoomCellAt(11, 16).getInitial());
		assertEquals('D', board.getRoomCellAt(10, 17).getInitial());
		assertEquals('D', board.getRoomCellAt(9, 19).getInitial());
		assertEquals('D', board.getRoomCellAt(7, 19).getInitial());
		assertEquals('B', board.getRoomCellAt(13, 0).getInitial());
		assertEquals('B', board.getRoomCellAt(19, 0).getInitial());
		assertEquals('B', board.getRoomCellAt(19, 3).getInitial());
		assertEquals('B', board.getRoomCellAt(13, 3).getInitial());
		assertEquals('K', board.getRoomCellAt(16, 6).getInitial());
		assertEquals('K', board.getRoomCellAt(19, 6).getInitial());
		assertEquals('K', board.getRoomCellAt(19, 11).getInitial());
		assertEquals('K', board.getRoomCellAt(15, 11).getInitial());
		assertEquals('K', board.getRoomCellAt(15, 11).getInitial());
		assertEquals('R', board.getRoomCellAt(15, 14).getInitial());
		assertEquals('R', board.getRoomCellAt(19, 14).getInitial());
		assertEquals('R', board.getRoomCellAt(19, 14).getInitial());
		assertEquals('R', board.getRoomCellAt(15, 15).getInitial());
		assertEquals('X', board.getRoomCellAt(13, 17).getInitial());
		assertEquals('X', board.getRoomCellAt(19, 17).getInitial());
		assertEquals('X', board.getRoomCellAt(19, 19).getInitial());
		assertEquals('X', board.getRoomCellAt(12, 19).getInitial());
		assertEquals('X', board.getRoomCellAt(12, 18).getInitial());
	}
}