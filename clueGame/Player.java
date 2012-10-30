package clueGame;

import java.util.Random;

public class Player {

	public static final int MAX_CARD_HAND = 3;
	
	private String name; 
	private Card[] cards; // Index 0 is suspect. Index 1 is weapon. Index 2 is room;
	private int startX;
	private int startY;

	public Player(String name, int startX, int startY) {
		this.name = name;
		this.startX = startX;
		this.startY = startY;
		cards = new Card[MAX_CARD_HAND];
	}
	
	public String getName() {
		return this.name;
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
		Card[] input = {suspect, weapon, room};
		int randomIndex = new Random().nextInt(3);
		if (input[randomIndex] == cards[randomIndex])
			return cards[randomIndex];
		randomIndex = (randomIndex + 1) % 3;
		if (input[randomIndex] == cards[randomIndex])
			return cards[randomIndex];
		randomIndex = (randomIndex + 1) % 3;
		if (input[randomIndex] == cards[randomIndex])
			return cards[randomIndex];
		return null;
	}
	
	public void addCard(Card c, int index) {
		cards[index] = c;
	}
	
	public boolean equals(Player other) {
		return (this.name == other.getName());
	}
	
}
