package clueGame;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Board {
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

		loadConfigFiles();
		calcAdjacencies();
		deal();
	}

	private void loadConfigFiles(){
		try {
			loadLegend(LEGEND_FILENAME);
			loadLayout(LAYOUT_FILENAME);
			loadCardsAndPeople(CARDANDPLAYERS_FILENAME);
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
						else 
							cells.add(new RoomCell(element[i].trim()));
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
	
	public void setCurrentPlayer(Player p) {
		this.currentPlayer = p;
	}
	
	public boolean checkAccusation(Card person, Card room, Card weapon) {
		if(person.equals(goal[0]) && room.equals(goal[1]) && weapon.equals(goal[2])) {
			return true;
		}
		return false;
		
	}
	
	public Player handleSuggestion(Card person, Card room, Card weapon) {
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
				copyWeapons.remove(cardEntry.getKey());
				break;
			}	
			i++;
		}
		// map holds all cards except goal cards.
		Map<String, Card> cards = new HashMap<String, Card>();
		iter = copySuspects.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry cardEntry = (Map.Entry) iter.next(); 
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
	
	public ArrayList getSeen() {
		return seen;
	}
	
	public void setSeen(ArrayList seen) {
		this.seen = seen;
	}
}
