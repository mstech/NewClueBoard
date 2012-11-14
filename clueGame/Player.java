package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Player {

	public static final int MAX_CARD_HAND = 3;

	private String name; 
	private Card[] cards; // Index 0 is suspect. Index 2 is weapon. Index 1 is room;
	private int startX;
	private int startY;
	private String room;
	private boolean wasDisproved;
	private Card[] suggestion;

	public Player(String name, int startX, int startY) {
		this.name = name;
		this.startX = startX;
		this.startY = startY;
		cards = new Card[MAX_CARD_HAND];
		wasDisproved = true;
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
	

	public Card[] makeSuggestion(Board b) {
		Card[] ret = new Card[3];

		ArrayList<Card> seen = b.getSeen();
		Map<String, Card> cards = b.getCards("suspect");

		for (Card c : cards.values()) {
			if (!seen.contains(c)) {
				for(int i= 0; i < this.cards.length; i++) {
					if(!(this.cards[i] == c)) {
						ret[0] = c;
						break;
					}
				}
			}
		}

		cards = b.getCards("weapon");
		for (Card c : cards.values()) {
			if (!seen.contains(c)) {
				ret[2] = c;
				break;
			}
		}

		ret[1] = b.getCard("room", room);
		suggestion = ret;
		return ret;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public Card disproveSuggestion(Card suspect, Card room, Card weapon) {
		Card[] input = {suspect, room, weapon};
		int randomIndex = new Random().nextInt(3);

		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(input[i] == cards[j]) {
					return cards[j];
				}
			}
		}
		/*for(int i = 0 ; i < 3; i ++) {
			if (input[i] == cards[randomIndex])
				return cards[randomIndex];
		}

		randomIndex = (randomIndex + 1) % 3;
		for(int i = 0; i <3; i++){
			if (input[i] == cards[randomIndex])
				return cards[randomIndex];
		}


		randomIndex = (randomIndex + 1) % 3;
		for(int i =0 ; i < 3 ; i++){ 
			if (input[i] == cards[randomIndex])
				return cards[randomIndex];
		}
*/
		return null;
	}

	public void addCard(Card c, int index) {
		cards[index] = c;
	}
	public void setRoom(String s) {
		room = s;
	}
	public boolean equals(Player other) {
		return (this.name == other.getName());
	}

	public boolean isHuman() {
		return false;
	}
	
	public void setWasDisproved(boolean disproved) {
		wasDisproved = disproved;
	}
	public boolean getWasDisproved() {
		return wasDisproved;
	}
	public Card[] getSuggestion() {
		return suggestion;
	}

	public void draw(Graphics g) {
		if(name.equals("Miss Scarlett")) {
			g.setColor(Color.RED);
			g.fillOval(startX*BoardCell.SIDE_LENGTH, startY*BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH);
		}
		else if(name.equals("Colonel Mustard")) {
			g.setColor(Color.ORANGE);
			g.fillOval(startX*BoardCell.SIDE_LENGTH, startY*BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH);
		}
		else if(name.equals("Mrs. White")) {
			g.setColor(Color.WHITE);
			g.fillOval(startX*BoardCell.SIDE_LENGTH, startY*BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH);
		}
		else if(name.equals("Reverend Green")) {
			g.setColor(Color.GREEN);
			g.fillOval(startX*BoardCell.SIDE_LENGTH, startY*BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH);
		}
		else if(name.equals("Mrs. Peacock")) {
			g.setColor(Color.MAGENTA);
			g.fillOval(startX*BoardCell.SIDE_LENGTH, startY*BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH);
		}
		else if(name.equals("Professor Plum")) {
			g.setColor(Color.BLUE);
			g.fillOval(startX*BoardCell.SIDE_LENGTH, startY*BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH, BoardCell.SIDE_LENGTH);
		}
	}

}
