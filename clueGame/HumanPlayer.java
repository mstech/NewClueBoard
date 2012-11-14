package clueGame;

public class HumanPlayer extends Player {

	private boolean endTurn;
	
	public HumanPlayer(String name, int startX, int startY) {
		super(name, startX, startY);
		setEndTurn(true);
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}

	public boolean isEndTurn() {
		return endTurn;
	}

	public void setEndTurn(boolean endTurn) {
		this.endTurn = endTurn;
	}

}
