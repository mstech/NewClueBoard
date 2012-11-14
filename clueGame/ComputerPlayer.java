package clueGame;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {
	BoardCell lastVisited;
	
	
	public ComputerPlayer(String name, int startX, int startY) {
		super(name, startX, startY);
		lastVisited = null;
	}

	public BoardCell chooseMove(Set<BoardCell> targets, Board b) {
		Iterator<BoardCell> iter = targets.iterator();
		int randomTarget = new Random().nextInt(targets.size());
		int i = 0;
		while(iter.hasNext()) {
			 BoardCell next = iter.next();
			 if(next.isDoorway()) {
				 RoomCell rc = (RoomCell) next;
//				 if( !(b.isInSeen(b.getRooms().get(rc.getInitial())))) {
					 if(lastVisited != next) {
						 lastVisited = next;
						 return next;
					 }
//				 }
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
