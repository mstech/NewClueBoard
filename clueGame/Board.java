package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Board extends JPanel {
	public static final String LEGEND_FILENAME = "legend.txt";
	public static final String LAYOUT_FILENAME = "Clue Layout.csv";
	public static final String CARDANDPLAYERS_FILENAME = "PeopleAndCards.txt";
	public static final int MAX_CARD_HAND = 3;

	private ArrayList<BoardCell> cells;
	private Map<Character, String> rooms;
	private LinkedList<Integer> visited;
	private Map<Integer, LinkedList<Integer>> adjMtx;
	private Set<BoardCell> targets;
	private LinkedList<Integer> currentPath;

	private Map<String, Player> players;
	private Map<String, Card> suspect;
	private Map<String, Card> room;
	private Map<String, Card> weapon;
	private Card[] goal; // Index 0 is suspect. Index 1 is room. Index 2 is weapon.

	private int numRows;
	private int numColumns;
	private int diceRoll;
	private boolean isGameBegin;
	private ControlPanel cp;

	private Player currentPlayer;
	private Card matchedCard;
	private ArrayList<Card> seen;

	public Board() {
		super();
		cells = new ArrayList<BoardCell>();
		rooms = new TreeMap<Character, String>();
		adjMtx = new TreeMap<Integer, LinkedList<Integer>>();
		targets = new HashSet<BoardCell>();
		currentPath = new LinkedList<Integer>();
		visited = new LinkedList<Integer>();
		players = new HashMap<String, Player>();
		suspect = new HashMap<String, Card>();
		room = new HashMap<String, Card>();
		weapon = new HashMap<String, Card>();
		seen = new ArrayList<Card>();
		goal = new Card[MAX_CARD_HAND];
		isGameBegin = true;

		addMouseListener(new BoardMouseListener());
		loadConfigFiles();
		calcAdjacencies();
		deal();


		//lol
	}

	public void addControlPanel(ControlPanel cp){
		this.cp = cp;
	}

	public boolean isGameBegin() {
		return isGameBegin;
	}

	public void setGameBegin(boolean isGameBegin) {
		this.isGameBegin = isGameBegin;
	}

	private void loadConfigFiles(){
		try {
			loadLegend(LEGEND_FILENAME);
			loadLayout(LAYOUT_FILENAME);
			loadCardsAndPeople(CARDANDPLAYERS_FILENAME);
			for(int i = 0; i < cells.size(); i++) {
				cells.get(i).setRow(i%numColumns);
				cells.get(i).setColumn(i/numColumns);
			}
		} catch (BadConfigFormatException e){
			System.out.println(e.toString());
		}
		System.out.println("Files are successfully loaded.");
	}

	private void loadLegend(String legend) throws BadConfigFormatException{
		try {
			FileReader inLegend = new FileReader(legend);
			Scanner readLegend = new Scanner(inLegend);
			String[] element;
			String current;
			while(readLegend.hasNext()){
				current = readLegend.nextLine();
				element = current.split(",");
				if (element.length != 2)
					throw new BadConfigFormatException("Row of the legend file doesn't have exactly 2 items.");
				else if(element[0].length() != 1)
					throw new BadConfigFormatException("Invalid room initial in legend file.");
				else {
					rooms.put(element[0].charAt(0), element[1].trim());
				}
			}
			readLegend.close();
		} catch (FileNotFoundException e){
			System.out.println("Can't find the legend file: " + legend);
		}
	}

	private void loadLayout(String layout) throws BadConfigFormatException{
		try {
			FileReader inLayout = new FileReader(layout);
			Scanner readLayout = new Scanner(inLayout);
			String[] element;
			String current;
			numColumns = 0;
			numRows = 0;
			while(readLayout.hasNext()){
				current = readLayout.nextLine();
				element = current.split(",");
				numRows++;
				if (numColumns == 0)
					numColumns = element.length;
				else if (numColumns != element.length)
					throw new BadConfigFormatException("Rows don't have the same number of columns in layout file.");
				for (int i=0; i<element.length; i++){
					if ((element[i].trim().length() <= 2) && (rooms.containsKey(element[i].trim().charAt(0)))){
						if (element[i].trim().equalsIgnoreCase("W"))
							cells.add(new WalkwayCell());
						else {
							cells.add(new RoomCell(element[i].trim()));

						} 
					} else
						throw new BadConfigFormatException("Invalid room initial in layout file.");
				}
			}

			readLayout.close();

		} catch (FileNotFoundException e){
			System.out.println("Can't find the layout file: " + layout);
		}
	}

	public void loadCardsAndPeople(String filename) throws BadConfigFormatException {
		try {
			FileReader inFile = new FileReader(filename);
			Scanner readFile = new Scanner(inFile);

			String[] elements;
			String current;
			while (readFile.hasNext()) {
				current = readFile.nextLine();
				if (!"".equals(current)) {
					elements = current.split(" ");
					String name = "";
					int i;
					if ("player".equalsIgnoreCase(elements[0])) {
						i = 4;
						while (i < elements.length) {
							name += elements[i++] + " ";
						}
						name = name.substring(0, name.length() -1);

						int startX = Integer.parseInt(elements[2]);
						int startY = Integer.parseInt(elements[3]);
						if ("computer".equalsIgnoreCase(elements[1])) {
							players.put(name, new ComputerPlayer(name, startX, startY));
						} else if ("human".equalsIgnoreCase(elements[1])) {
							players.put(name, new HumanPlayer(name, startX, startY));
						}
					} else if ("card".equalsIgnoreCase(elements[0])) {
						i = 2;
						while (i < elements.length) {
							name += elements[i++] + " ";
						}
						name = name.substring(0, name.length() -1);

						if ("suspect".equalsIgnoreCase(elements[1])) {
							suspect.put(name, new Card(name, Card.CardType.SUSPECT));
						} else if ("weapon".equalsIgnoreCase(elements[1])) {
							weapon.put(name, new Card(name, Card.CardType.WEAPON));
						} else if ("room".equalsIgnoreCase(elements[1])) {
							room.put(name, new Card(name, Card.CardType.ROOM));
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Can't find cards and people config file: " + filename);
		}
	}




	private void generateNewTargets(int startPos, int steps){
		visited.push(startPos);
		LinkedList<Integer> currentList = (LinkedList<Integer>) this.getAdjList(startPos).clone();
		int nextPos;
		while(!currentList.isEmpty()){
			nextPos = currentList.pop();
			if (!visited.contains(nextPos)) {
				currentPath.add(nextPos);
				if (getCellAt(nextPos).isDoorway())
					targets.add(getCellAt(currentPath.getLast()));
				else if (currentPath.size() == steps){
					targets.add(getCellAt(currentPath.getLast()));
					//System.out.println(currentPath.toString());
				}
				else {
					generateNewTargets(nextPos, steps);
					visited.pop();
				}
				currentPath.removeLast();

			}
		}
	}

	public void calcTargets(int startPos, int steps){
		targets.clear();
		visited.clear();
		generateNewTargets(startPos, steps);
	}

	private void calcAdjacencies(){
		int index;
		for (int i=0; i<numRows; i++){
			for (int j=0; j<numColumns; j++){
				index = calcIndex(i, j);
				LinkedList<Integer> adjList = new LinkedList<Integer>();
				//	current cell is a room cell
				if (getRoomCellAt(i, j) != null)
					if (!getRoomCellAt(i, j).isDoorway())
						adjMtx.put(index, adjList);
					else {
						RoomCell currentCell = getRoomCellAt(i, j);
						switch (currentCell.getDoorDirection()){
						case RIGHT:
							if (j != numColumns-1)
								adjList.add(calcIndex(i, j+1));
							break;
						case LEFT:
							if (j != 0)
								adjList.add(calcIndex(i, j-1));
							break;
						case UP:
							if (i != 0)
								adjList.add((calcIndex(i-1, j)));
							break;
						case DOWN:
							if (i != numRows-1)
								adjList.add((calcIndex(i+1, j)));
							break;
						}
						adjMtx.put(index, adjList);
					}
				//	Current cell is walkway
				else if (getCellAt(index).isWalkway()){
					if ((i != 0) && adjToWalkway(calcIndex(i-1, j), index))
						adjList.add(calcIndex(i-1, j));
					if ((j != 0) && adjToWalkway(calcIndex(i, j-1), index))
						adjList.add(calcIndex(i, j-1));
					if ((i != numRows-1) && adjToWalkway(calcIndex(i+1, j), index))
						adjList.add(calcIndex(i+1, j));
					if ((j != numColumns-1) && adjToWalkway(calcIndex(i, j+1), index))
						adjList.add(calcIndex(i, j+1));
					adjMtx.put(index, adjList);
				}
			}
		}
	}

	/*
	 * True if `index` is a room and a door pointing in the direction to `walkawayindex`
	 * 
	 */
	private boolean adjToWalkway(int index, int walkwayIndex){
		//	room cell but not doorway => not reachable
		if (getCellAt(index).isRoom() && !getCellAt(index).isDoorway())
			return false;
		//	room cell and is a door
		else if (getCellAt(index).isRoom()){
			RoomCell currentCell = (RoomCell) getCellAt(index);
			switch (currentCell.getDoorDirection()){
			case RIGHT:
				if (index != walkwayIndex - 1)
					return false;
				break;
			case LEFT:
				if (index != walkwayIndex + 1)
					return false;
				break;
			case UP:
				if (index != walkwayIndex + numColumns)
					return false;
				break;
			case DOWN:
				if (index != walkwayIndex - numColumns)
					return false;
				break;
			}
		}
		return true;
	}

	public void setAnswer(Card person, Card room, Card weapon) {
		goal[0] = person;
		goal[1] = room;
		goal[2] = weapon;

	}
	public Card[] getAnswer() {
		return goal;
	}

	public void setCurrentPlayer(Player p) {
		this.currentPlayer = p;
	}

	public void resetCurrentPlayer() {
		this.currentPlayer = null;
	}

	public boolean checkAccusation(Card person, Card room, Card weapon) {
		if(person.equals(goal[0]) && room.equals(goal[1]) && weapon.equals(goal[2])) {
			return true;
		}
		return false;

	}

	public Card handleSuggestion(Card suspect, Card room, Card weapon) {

		/*
		for (String key : players.keySet()) {
			Player p = players.get(key);
			if (p == currentPlayer) continue;

			Card c = p.disproveSuggestion(suspect, weapon, room);
			if (c != null) {
				//System.out.println("Matched " + p.getName() + " with " + c.getName());
				return p;
			}
		}
		 */

		Map<String, Player> copyPlayers = new HashMap<String, Player>(players);
		while (!copyPlayers.isEmpty()) {
			List<String> keys = new ArrayList<String>(copyPlayers.keySet());
			int randomPlayer = new Random().nextInt(copyPlayers.size());
			String key = keys.get(randomPlayer);

			Player p = copyPlayers.get(key);
			if (p == currentPlayer) {
				copyPlayers.remove(key);
				continue;
			}

			Card c = p.disproveSuggestion(suspect, room, weapon); 
			if (c != null) {
				return c;
			}		

			copyPlayers.remove(key);
		}

		return null;
	}

	public void deal() {
		Map<String, Card> copySuspects = new HashMap<String, Card>(suspect);
		Map<String, Card> copyRooms = new HashMap<String, Card>(room);
		Map<String, Card> copyWeapons = new HashMap<String, Card>(weapon);
		// adds suspects goal card
		Iterator iter = copySuspects.entrySet().iterator();
		int randomCard = new Random().nextInt(copySuspects.size());
		int i = 0;
		while(iter.hasNext()) {
			Map.Entry cardEntry = (Map.Entry) iter.next();
			if(i == randomCard) {
				goal[0] = (Card) cardEntry.getValue();
				System.out.println("Suspect: " + goal[0].getName());
				copySuspects.remove(cardEntry.getKey());
				break;
			}
			i++;
		}
		// add room goal card.
		iter = copyRooms.entrySet().iterator();
		randomCard = new Random().nextInt(copyRooms.size());
		i = 0;
		while(iter.hasNext()) {
			Map.Entry cardEntry = (Map.Entry) iter.next();
			if(i == randomCard) {
				goal[1] = (Card) cardEntry.getValue();
				System.out.println("Room: " + goal[1].getName());
				copyRooms.remove(cardEntry.getKey());
				break;
			}	
			i++;
		}
		// add weapon goal card.
		iter = copyWeapons.entrySet().iterator();
		randomCard = new Random().nextInt(copyWeapons.size());
		i = 0;
		while(iter.hasNext()) {
			Map.Entry cardEntry = (Map.Entry) iter.next();
			if(i == randomCard) {
				goal[2] = (Card) cardEntry.getValue();
				System.out.println("Weapon: " + goal[2].getName());
				copyWeapons.remove(cardEntry.getKey());
				break;
			}	
			i++;
		}
		// map holds all cards except goal cards.
		Map<String, Card> cards = new HashMap<String, Card>();
		// adds the suspect cards to cards.
		iter = copySuspects.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Card> cardEntry = (Map.Entry) iter.next(); 
			cards.put((String) cardEntry.getKey(), (Card) cardEntry.getValue());
		}

		// adds the room cards to cards.
		iter = copyRooms.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry cardEntry = (Map.Entry) iter.next(); 
			cards.put((String) cardEntry.getKey(), (Card) cardEntry.getValue());
		}
		iter = copyWeapons.entrySet().iterator();
		// adds the weapons cards to cards.
		while(iter.hasNext()) {
			Map.Entry cardEntry = (Map.Entry) iter.next(); 
			cards.put((String) cardEntry.getKey(), (Card) cardEntry.getValue());
		}

		// deals 3 unique cards to every player.
		Iterator playersIter = players.entrySet().iterator();
		while(playersIter.hasNext()) {
			Map.Entry playerEntry = (Map.Entry) playersIter.next();
			Player player = (Player) playerEntry.getValue();
			for(int j = 0; j < MAX_CARD_HAND; j++) {
				iter = cards.entrySet().iterator();
				randomCard = new Random().nextInt(cards.size());
				i = 0;
				while(iter.hasNext()) {
					Map.Entry<String, Card> entry = (Map.Entry) iter.next();
					if(i == randomCard) {
						player.addCard(entry.getValue(), j);
						cards.remove(entry.getKey());
						break;
					}
					i++;
				}
			}
		}


	}


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(BoardCell b : cells) {
			if(b.isRoom()) {
				b.drawCell(g, this);
			}
			else{
				b.drawCell(g, this);
			}
		}
		Iterator<Entry<String, Player>> iter = players.entrySet().iterator();
		while(iter.hasNext()) {
			iter.next().getValue().draw(g);
		}

	}

	public void nextPlayer() {

		for(BoardCell b : targets) {
			if(b.isWalkway()){
				b.setColor(Color.YELLOW);
				repaint();
			}
			else{
				b.setColor(Color.GRAY);
			}
		}

		if(currentPlayer == null){
			currentPlayer = getHumanPlayer();
		}

		Iterator<Entry<String, Player>> iter = players.entrySet().iterator();
		while(iter.hasNext()) {
			if(iter.next().getValue().equals(currentPlayer)) {
				if(!(iter.hasNext())) {
					iter = players.entrySet().iterator();
					currentPlayer = iter.next().getValue();
					return;
				}
				currentPlayer = iter.next().getValue();

				if(currentPlayer.isHuman()){
					HumanPlayer hp = (HumanPlayer) currentPlayer;
					hp.setEndTurn(false);
				}

				return;
			}

		}

	}

	public void rollDice(){
		Random randomizer = new Random();
		diceRoll = randomizer.nextInt(6) +1;
	}

	public ArrayList<String> drawTargets() {
		calcTargets(calcIndex(currentPlayer.getY(), currentPlayer.getX()), diceRoll);
		ArrayList<String> stuff = new ArrayList<String>();
		if(currentPlayer.isHuman()) {
			for(BoardCell b : targets) {
				b.setColor(Color.CYAN);
				repaint();
			}
		}
		else{
			if(!(currentPlayer.getWasDisproved())) {
				boolean win = checkAccusation(currentPlayer.getSuggestion()[0], currentPlayer.getSuggestion()[1], currentPlayer.getSuggestion()[2]);
				if(win) {
					JOptionPane.showMessageDialog(new JFrame(), "Computer Player Wins! Suggestion: " + currentPlayer.getSuggestion()[0].getName() + " " + currentPlayer.getSuggestion()[1].getName() + " " + currentPlayer.getSuggestion()[2].getName(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
					//System.exit(0);
				}
				else {
					JOptionPane.showMessageDialog(new JFrame(), "Computer Player made a bad Accusation! Accusation: " + currentPlayer.getSuggestion()[0].getName() + " " + currentPlayer.getSuggestion()[1].getName() + " " + currentPlayer.getSuggestion()[2].getName(), "Bad Accusation", JOptionPane.INFORMATION_MESSAGE);
				}
				currentPlayer.setWasDisproved(true);
				return null;
			}
			ComputerPlayer temp = (ComputerPlayer)currentPlayer;
			BoardCell bc = temp.chooseMove(targets, this);
			currentPlayer.setStartX(bc.getRow());
			currentPlayer.setStartY(bc.getColumn());
			repaint();
			if(bc.isDoorway()) {
				RoomCell rc = (RoomCell) bc;
				Card[] tempCards = currentPlayer.makeSuggestion(this);
				char c = rc.getInitial();
				String roomName = rooms.get(c);
				tempCards[1] = getCard("room", roomName);
				Card proof = handleSuggestion(tempCards[0], tempCards[1], tempCards[2]);
				stuff.add((tempCards[0].getName()));
				stuff.add((tempCards[1].getName()));
				stuff.add((tempCards[2].getName()));
				players.get(tempCards[0].getName()).setStartY(rc.getColumn());
				players.get(tempCards[0].getName()).setStartX(rc.getRow());
				repaint();
				seen.add(proof);
				if(proof == null) {
					currentPlayer.setWasDisproved(false);
					return stuff;
				} else {
					currentPlayer.setWasDisproved(true);
					stuff.add((proof.getName()));
				}
			}
		}

		return stuff;
	}


	public boolean isInSeen(String name) {
		for(Card c : seen) {
			if(c.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}


	public Card getMatchedCard() {
		return matchedCard;
	}

	public RoomCell getRoomCellAt(int row, int col){
		int index = calcIndex(row, col);
		if (cells.get(index).isRoom())
			return (RoomCell) cells.get(index);
		else
			return null;
	}

	public BoardCell getCellAt(int idx) {
		return cells.get(idx);
	}

	public Map<Character, String> getRooms() {
		return rooms;
	}

	public int calcIndex(int row, int col){
		return row*numColumns + col;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public LinkedList<Integer> getAdjList(int index){
		return adjMtx.get(index);
	}

	public Set<BoardCell> getTargets(){
		return targets;
	}

	public Map<String, Player> getPlayers() {
		return players;
	}

	public Map<String, Card> getCards(String cardType) {
		if(cardType.equals("suspect")) {
			return suspect;
		}
		else if(cardType.equals("room")) {
			return room;
		}
		else if(cardType.equals("weapon")) {
			return weapon;
		}
		return null;
	}

	public Player getPlayer(String name) {
		return players.get(name);
	}

	public Card getCard(String cardType, String name) {
		return getCards(cardType).get(name);
	}

	public ArrayList<Card> getSeen() {
		return seen;
	}

	public void setSeen(ArrayList<Card> seen) {
		this.seen = seen;
	}

	public Player getHumanPlayer() {
		Iterator<Entry<String, Player>> iter = players.entrySet().iterator();
		while(iter.hasNext()) {
			Player p = iter.next().getValue();
			if(p.isHuman()) {
				return p;
			}
		}
		return null;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	public int getDiceRoll() {
		return diceRoll;
	}

	public Card[] getGoal() {
		return goal;
	}

	public class BoardMouseListener implements MouseListener {
		public void mouseClicked (MouseEvent event) {
			if( currentPlayer != null && currentPlayer.isHuman()){
				HumanPlayer hp = (HumanPlayer) currentPlayer;
				if(!(hp.isEndTurn())){
					boolean valid  = false;
					for(BoardCell c : targets) {
						if(event.getX() > c.getRow()*BoardCell.SIDE_LENGTH && event.getX() <= c.getRow() * BoardCell.SIDE_LENGTH + BoardCell.SIDE_LENGTH ){
							if(event.getY() > c.getColumn()*BoardCell.SIDE_LENGTH && event.getY() <= c.getColumn() * BoardCell.SIDE_LENGTH + BoardCell.SIDE_LENGTH ){

								currentPlayer.setStartX(c.getRow());
								currentPlayer.setStartY(c.getColumn());
								if(c.isDoorway()){

									HumanMakeSuggestion suggestion = new HumanMakeSuggestion((RoomCell)c);
									suggestion.setSize(200, 200);
									suggestion.setVisible(true);

								}
								valid = true;

								hp = (HumanPlayer) currentPlayer;
								hp.setEndTurn(true);

							}	

						}

					}
					if(! valid){
						JOptionPane.showMessageDialog(new JFrame(), "Invalid Location", "Error", JOptionPane.ERROR_MESSAGE);
					}
					if(valid){
						for(BoardCell b : targets) {
							if(b.isWalkway()){
								b.setColor(Color.YELLOW);
								repaint();
							}
							else{
								b.setColor(Color.GRAY);
							}
						}
					}
				} else{

					JOptionPane.showMessageDialog(new JFrame(), "Its not your turn!", "Error", JOptionPane.ERROR_MESSAGE);
					for(BoardCell b : targets) {
						if(b.isWalkway()){
							b.setColor(Color.YELLOW);
							repaint();
						}
						else{
							b.setColor(Color.GRAY);
						}
					}

				}
			}
		}
		public void mousePressed(MouseEvent event) {
		}
		public void mouseReleased (MouseEvent event) {

		}
		public void mouseEntered (MouseEvent event) {

		}
		public void mouseExited (MouseEvent event) {

		}

	}

	public class HumanMakeSuggestion extends JDialog {

		private RoomPanel roomPanel;
		private WeaponPanel weaponPanel;
		private SuspectPanel suspectPanel;
		private RoomCell rc;

		public HumanMakeSuggestion( RoomCell c) {
			this.rc = c;
			roomPanel = new RoomPanel(this.rc);
			weaponPanel = new WeaponPanel();
			suspectPanel = new SuspectPanel();
			add(roomPanel);
			add(weaponPanel);
			add(suspectPanel);
			JButton suggestionButton = new JButton("Make Suggestion");
			add(suggestionButton);
			suggestionButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Card roomCard = room.get(rooms.get(rc.getInitial()));
					Card weaponCard = weapon.get(weaponPanel.getWeaponChoice().getSelectedItem());
					Card suspectCard = suspect.get(suspectPanel.getSuspectChoice().getSelectedItem());
					Card proof = handleSuggestion(suspectCard, roomCard, weaponCard);
					if(proof != null) {
						seen.add(proof);
					}
					players.get(suspectCard.getName()).setStartY(rc.getColumn());
					players.get(suspectCard.getName()).setStartX(rc.getRow());
					repaint();

					cp.getSuggestion().setTextBox(suspectCard.getName() + " " + roomCard.getName() + " " + weaponCard.getName());
					if(proof != null){
						cp.getResponse().setTextBox(proof.getName());
					}
					else{
						cp.getResponse().setTextBox("No proof");
					}




					setVisible(false);

				}

			});
			setLayout(new GridLayout(4, 1));
		}

	}

	public class RoomPanel extends JPanel {

		private RoomCell c;

		public RoomPanel(RoomCell c) {
			this.c = c;
			JLabel roomLabel = new JLabel("Room");
			JTextField room = new JTextField(rooms.get(c.getInitial()));
			room.setEditable(false);
			add(roomLabel);
			add(room);

		}
	}

	public class WeaponPanel extends JPanel {
		private JComboBox<String> weaponChoice;

		public WeaponPanel() {
			JLabel weaponLabel = new JLabel("Weapon");
			weaponChoice = new JComboBox<String>();
			Iterator<Entry<String, Card>> iter = weapon.entrySet().iterator();
			while(iter.hasNext()) {
				String temp = iter.next().getValue().getName();
				weaponChoice.addItem(temp);
			}
			add(weaponLabel);
			add(weaponChoice);
		}

		public JComboBox<String> getWeaponChoice() {
			return weaponChoice;
		}

	}

	public class SuspectPanel extends JPanel {

		private JComboBox<String> suspectChoice;

		public SuspectPanel() {
			JLabel suspectLabel = new JLabel("Suspect");
			suspectChoice = new JComboBox<String>();
			Iterator<Entry<String, Card>> iter = suspect.entrySet().iterator();
			while(iter.hasNext()) {
				String temp = iter.next().getValue().getName();
				suspectChoice.addItem(temp);
			}
			add(suspectLabel);
			add(suspectChoice);
		}

		public JComboBox<String> getSuspectChoice() {
			return suspectChoice;
		}
	}

	public Map<String, Card> getRoom() {
		return room;
	}

	public Map<String, Card> getWeapon() {
		return weapon;
	}

	public Map<String, Card> getSuspect() {
		return suspect;
	}

}
