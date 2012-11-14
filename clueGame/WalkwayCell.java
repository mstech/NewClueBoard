package clueGame;

import java.awt.Color;
import java.awt.Graphics;

public class WalkwayCell extends BoardCell{

	public WalkwayCell() {
		super();
		color = Color.YELLOW;
	}
	
	@Override
	public boolean isWalkway() {
		return true;
	}
	
	@Override
	public void drawCell(Graphics g, Board b) {
		g.setColor(color);
		g.fillRect(row*SIDE_LENGTH,  column*SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH);
		g.setColor(Color.BLACK);
		g.drawRect(row*SIDE_LENGTH,  column*SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH);

	}
}
