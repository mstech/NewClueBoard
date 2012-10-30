package clueGame;

import clueGame.Card.CardType;

public class Player {

	private String name; 
	private Card[] cards; // Index 0 is suspect. Index 1 is room. Index 2 is weapon;
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
	
	public Card[] getCards() {
		return cards;
	}
	
	public Card[] makeSuggestion() {
		return null;
	}
	
	public Card disproveSuggestion(Card suspect, Card weapon, Card room) {
		return null;
	}
	
	public void addCard(Card c) {
		if(c.getCardType()== CardType.SUSPECT) {
			cards[0] = c;
		}
		else if(c.getCardType()== CardType.ROOM) {
			cards[1] = c;
		}
		else if(c.getCardType()== CardType.WEAPON) {
			cards[2] = c;
		}
	}
	
}
