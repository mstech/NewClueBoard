package Test;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.Player;


public class GameSetupTests {
	private static Board board;
	
	@BeforeClass
	public static void setUp() {
		board = new Board();
	}
	
	@Test
	public void testLoadingPeople() {
		Map<String, Player> players = board.getPlayers();
		Assert.assertEquals(6, players.size());
		
		Player humanPlayer = board.getPlayer("Professor Plum");
		Assert.assertNotNull(humanPlayer);
		Assert.assertEquals(16, humanPlayer.getX());
		Assert.assertEquals(19, humanPlayer.getY());
		
		Player computerPlayer = board.getPlayer("Miss Scarlett");
		Assert.assertNotNull(humanPlayer);
		Assert.assertEquals(5, computerPlayer.getX());
		Assert.assertEquals(0, computerPlayer.getY());
		
		Player computerPlayer2 = board.getPlayer("Mrs. White");
		Assert.assertNotNull(humanPlayer);
		Assert.assertEquals(0, computerPlayer2.getX());
		Assert.assertEquals(12, computerPlayer2.getY());
		
	}
	
	@Test
	public void testLoadingcards() {
		Map<String, Card> suspect = board.getCards("suspect");
		Map<String, Card> weapons = board.getCards("weapon");
		Map<String, Card> rooms = board.getCards("room");
		
		int totalSize = suspect.size() + weapons.size() + rooms.size();
		Assert.assertEquals(21, totalSize);

		Assert.assertEquals(6, suspect.size());
		Assert.assertEquals(6, weapons.size());
		Assert.assertEquals(9, rooms.size());
		
		Card c = board.getCard("Miss Scarlett", "suspect");
		Assert.assertNotNull(c);
		c = board.getCard("Candlestick", "weapon");
		Assert.assertNotNull(c);
		c = board.getCard("Kitchen", "room");
		Assert.assertNotNull(c);
	}
	
	@Test
	public void testUniqueAndFairCardDistribution() {
		board.deal();
		Map<String, Player> players = board.getPlayers();
		
		int numCards = 3;
		boolean sameNumberOfCards = true;
		List<Card> seenCards = new LinkedList<Card>();
		boolean seenACard = false;;
		
		for (Player p : players.values()) {
			numCards += 3;
			if (p.getCards().length != 3)
				sameNumberOfCards = false;
			for (Card c : p.getCards()) {
				if (seenCards.contains(c)) {
					seenACard = true;
				} else {
					seenCards.add(c);
				}
			}
		}
		
		//6 players, 3 cards each + 1 3 card solution = 21
		Assert.assertEquals(21, numCards);
		//Set to false if a player does not have 3 cards
		Assert.assertTrue(sameNumberOfCards);
		//Set to true if we have seen a card
		Assert.assertFalse(seenACard);
	}
	
	@Test
	public void testAccusation() {
		Card suspect = board.getCard("Miss Scarlett", "suspect");
		Card weapon = board.getCard("Candlestick", "weapon");
		Card room = board.getCard("Kitchen", "room");
		board.setAnswer(suspect, room, weapon);
		
		Assert.assertTrue(board.checkAccusation(suspect, room, weapon));
		
		suspect = board.getCard("Mrs. Peacock", "suspect");
		Assert.assertFalse(board.checkAccusation(suspect, room, weapon));
		
		weapon = board.getCard("Dagger", "weapon");
		Assert.assertFalse(board.checkAccusation(suspect, room, weapon));
		
		room = board.getCard("Dining Hall", "room");
		Assert.assertFalse(board.checkAccusation(suspect, room, weapon));
	}
	
	@Test
	public void testDisprovingSuggestion() {
		Card suspect1 = board.getCard("Miss Scarlett", "suspect");
		Card weapon1 = board.getCard("Candlestick", "weapon");
		Card room1 = board.getCard("Kitchen", "room");
		
		Card suspect2 = board.getCard("Mrs. Peacock", "suspect");
		Card weapon2 = board.getCard("Dagger", "weapon");
		Card room2 = board.getCard("Dining Hall", "room");
		
		Player p = new Player("Mrs. White", 0, 0);
		p.addCard(suspect1);
		p.addCard(weapon1);
		p.addCard(room1);
		
		//suspect is right
		Assert.assertNotNull(p.disproveSuggestion(suspect1, weapon2, room2));
		//weapon is right
		Assert.assertNotNull(p.disproveSuggestion(suspect2, weapon1, room2));
		//room is right
		Assert.assertNotNull(p.disproveSuggestion(suspect2, weapon2, room1));
		//none are right
		Assert.assertNull(p.disproveSuggestion(suspect2, weapon2, room2));
		
	}
	
	@Test
	public void testSelectingTarget() {
		
	}
	
	@Test
	public void testMakingSuggestion() {
		
	}
	
}
