package Test;


import java.util.Iterator;
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
		Map<String, Card> cards = board.getCards();
		Assert.assertEquals(21, cards.size());
		
		int suspect = 0;
		int weapon = 0;
		int rooms = 0;
		for (Card c : cards.values()) {
			switch (c.getCardType()) {
			case SUSPECT:
				suspect++;
				break;
			case WEAPON:
				weapon++;
				break;
			case ROOM:
				rooms++;
				break;
			}
		}
		
		Assert.assertEquals(6, suspect);
		Assert.assertEquals(6, weapon);
		Assert.assertEquals(9, rooms);
	}
	
	@Test
	public void testUniqueAndFairCardDistribution() {
		
	}
	
	@Test
	public void testAccusation() {
		
	}
	
	@Test
	public void testDisprovingSuggestion() {
		
	}
	
	@Test
	public void testSelectingTarget() {
		
	}
	
	@Test
	public void testMakingSuggestion() {
		
	}
	
}
