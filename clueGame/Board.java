package clueGame;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Board {
	private ArrayList<BoardCell> cells;
	private Map<Character, String> rooms;
	private int numRows;
	private int numColumns;
	private int gridPieces;
	private Map<Integer, LinkedList<Integer>> adjMtx;
	private Set<BoardCell> targets;
	private boolean[] visited;
	private LinkedList<Integer> currentPath;
	
	public Board() {
		super();
		cells = new ArrayList<BoardCell>();
		rooms = new TreeMap<Character, String>();
		loadConfigFiles("legend.txt", "Clue Layout.csv");
		adjMtx = new TreeMap<Integer, LinkedList<Integer>>();
		calcAdjacencies();
		targets = new HashSet<BoardCell>();
		gridPieces = numRows * numColumns;
		currentPath = new LinkedList<Integer>();
		visited = new boolean[gridPieces];
		for (int i=0; i<gridPieces; i++)
			visited[i] = false;
	}

	public void loadConfigFiles(String legend, String layout){
		try {
		loadLegend(legend);
		loadLayout(layout);
		} catch (BadConfigFormatException e){
			System.out.println(e.toString());
		}
		System.out.println("Files are successfully loaded.");
	}
	
	public void loadLegend(String legend) throws BadConfigFormatException{
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
	
	public void loadLayout(String layout) throws BadConfigFormatException{
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
	
	public void generateNewTargets(int startPos, int steps){
		visited[startPos] = true;
		LinkedList<Integer> currentList = (LinkedList<Integer>) this.getAdjList(startPos).clone();
		int nextPos;
		while(!currentList.isEmpty()){
			nextPos = currentList.pop();
			if(!visited[nextPos]) { 
				currentPath.add(nextPos);
				if (getCellAt(nextPos).isDoorway())
					targets.add(getCellAt(currentPath.getLast()));
				else if (currentPath.size() == steps){
					targets.add(getCellAt(currentPath.getLast()));
					System.out.println(currentPath.toString());
				}
				else 
					generateNewTargets(nextPos, steps);
				currentPath.removeLast();
				visited[nextPos] = false;
			}
		}
	}
	
	public void calcTargets(int startPos, int steps){
		targets.clear();
		generateNewTargets(startPos, steps);
	}
	
	public void calcAdjacencies(){
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
	
	public boolean adjToWalkway(int index, int walkwayIndex){
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
