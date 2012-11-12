package clueGame;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {
	
	
	public ComputerPlayer(String name, int startX, int startY) {
		super(name, startX, startY);
	}

	public BoardCell chooseMove(Set<BoardCell> targets) {
		Iterator<BoardCell> iter = targets.iterator();
		int randomTarget = new Random().nextInt(targets.size());
		int i = 0;
		while(iter.hasNext()) {
			 BoardCell next = iter.next();
			 if(next.isDoorway()) {
				return next;
			 }
		}
		for( BoardCell target : targets) {
			if(i == randomTarget ) {
				return target;
			}
			i++;
		}
		return null;
		
	}
	
	@Override
	public boolean isHuman() {
		return false;
	}
	

}
