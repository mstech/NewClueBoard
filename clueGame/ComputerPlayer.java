package clueGame;

import java.util.Set;

public class ComputerPlayer extends Player {
	
	
	public ComputerPlayer(String name, int startX, int startY) {
		super(name, startX, startY);
	}

	public BoardCell chooseMove(Set<BoardCell> targets) {
		return null;
	}
	

}
