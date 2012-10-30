package clueGame;

import clueGame.Card.CardType;

public class Player {

	public static final int MAX_CARD_HAND = 3;
	
	private String name; 
	private Card[] cards; // Index 0 is suspect. Index 1 is room. Index 2 is weapon;
	private int startX;
	private int startY;

	public Player(String name, int startX, int startY) {
		this.name = name;
		this.startX = startX;
		this.startY = startY;
		cards = new Card[MAX_CARD_HAND];
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
	
	public void addCard(Card c, int index) {
		cards[index] = c;
	}
	
}
