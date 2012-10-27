package clueGame;

public class Player {

	private String name; 
	private Card[] cards;
	private int startX;
	private int startY;

	public Player(String name, int startX, int startY) {
		this.name = name;
		this.startX = startX;
		this.startY = startY;
	}
	
	public int getX() {
		return startX;
	}
	
	public int getY() {
		return startY;
	}
	
}
