import java.util.Random;

public class Cell
{
	// Genes
	private int cellType;
	boolean visitedDuringSolutionPath;
	private int posX;
	private int posY;
	private int roomAssignment;

	public Cell(int coordinateX, int coordinateY, boolean wall)
	{
		visitedDuringSolutionPath = false;
		roomAssignment = -1;
		posX = coordinateX;
		posY = coordinateY;
		if(wall)
		{
			cellType = Globals.WALL;
		}
		else
		{
			cellType = Globals.BLOCKED;	
		}

		// First genetic algorithm iteration
		// Random cells become doors
		// Fitness function will evaluate each map
			// Door not connecting hallways to rooms lower the fitness score
			// Fitness score decreases for each door connecting a room after N
			// Fitness score decreases for each room that doesn't contain at least one door
		/*Random rand = new Random();
		if(rand.nextInt(10) > 5)
		{
			cellType = Globals.POSSIBLE_DOOR;
		}*/
		
	}

	public boolean isRoomDoor(Cell[][] allTiles)
	{
		boolean retVal = false;
		// North door - South hall
		if((allTiles[getX()][getY() - 1].getCellType() == Globals.ROOM && allTiles[getX()][getY() + 1].getCellType() == Globals.HALLWAY) || (allTiles[getX()][getY() - 1].getCellType() == Globals.HALLWAY && allTiles[getX()][getY() + 1].getCellType() == Globals.ROOM))
		{
			retVal = true;
		}
		else if((allTiles[getX() - 1][getY()].getCellType() == Globals.ROOM && allTiles[getX() + 1][getY()].getCellType() == Globals.HALLWAY) || (allTiles[getX() - 1][getY()].getCellType() == Globals.HALLWAY && allTiles[getX() + 1][getY()].getCellType() == Globals.ROOM))
		{
			
			retVal = true;
		}

		return retVal;
	}
	public int getRoomAssignment()
	{
		return roomAssignment;
	}
	public void setRoomAssignment(int x)
	{
		roomAssignment = x;
	}
	public void setVisited(boolean val)
	{
		visitedDuringSolutionPath = val;
	}
	public boolean getVisisted()
	{
		return visitedDuringSolutionPath;
	}
	public void changeCellType(int type)
	{
		cellType = type;
	}
	public int getCellType()
	{
		return cellType;
	}
	public int getX()
	{
		return posX;
	}
	public int getY()
	{
		return posY;
	}

}