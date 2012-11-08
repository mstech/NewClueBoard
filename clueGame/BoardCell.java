package clueGame;

import java.awt.Graphics;

public abstract class BoardCell {
	protected int row;
	protected int column;
	
	public static final int SIDE_LENGTH = 25;

	public boolean isWalkway() {
		return false;
	}
	
	public boolean isRoom() {
		return false;
	}
	
	public boolean isDoorway() {
		return false;
	}
	
	public void drawCell(Graphics g) {
		
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}

}
