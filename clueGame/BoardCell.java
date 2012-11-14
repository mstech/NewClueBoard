package clueGame;

import java.awt.Graphics;

public abstract class BoardCell {
	protected int row;
	protected int column;
	protected java.awt.Color color;
	
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
	
	public void drawCell(Graphics g, Board b) {
		
	}
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}

	public void setColor(java.awt.Color color) {
		this.color = color;
	}
	
	
}
