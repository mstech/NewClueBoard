package clueGame;

public class RoomCell extends BoardCell {
	public enum DoorDirection {UP, DOWN, LEFT, RIGHT, NONE};
	private DoorDirection doorDirection;
	private char roomInitial;
	
	public RoomCell(String room) {
		roomInitial = room.charAt(0);
		if (room.length() == 2)
			switch (room.charAt(1)){
			case 'R': 
				doorDirection = DoorDirection.RIGHT;
				break;
			case 'L': 
				doorDirection = DoorDirection.LEFT;
				break;
			case 'U': 
				doorDirection = DoorDirection.UP;
				break;
			case 'D': 
				doorDirection = DoorDirection.DOWN;
				break;
			}
		else 
			doorDirection = DoorDirection.NONE;
	}
	
	@Override
	public boolean isRoom() {
		return true;
	}
	
	@Override
	public boolean isDoorway() {
		if (doorDirection == DoorDirection.NONE)
			return false;
		return true;
	}
	
	public char getInitial() {
		return roomInitial;
	}
	
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

}
