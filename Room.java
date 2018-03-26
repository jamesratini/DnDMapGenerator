import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Random;
import java.awt.Rectangle;

public class Room
{
	ArrayList<Cell> allCells;
	int roomNum;
	Random rand;
	boolean isNarrowHallwayType = false;



	public Room(int numRoom)
	{
		allCells = new ArrayList<Cell>();
		roomNum = numRoom;
		rand = new Random();
		
	}
	private Random getRand()
	{
		return rand;
	}
	public void add(Cell c)
	{
		allCells.add(c);
	}
	public Cell get(int x, int y)
	{
		Cell retVal = null;
		for(int i = 0; i < allCells.size(); i++)
		{
			if(allCells.get(i).getX() == x && allCells.get(i).getY() == y)
			{
				retVal = allCells.get(i);
				break;
			}
		}

		return retVal;
	}
	public int size()
	{
		return allCells.size();
	}
	public void remove(int x, int y)
	{
		// Can't just call remove on allCells because the cells in allCells are a reference to the parent GridMap in some cases
		// and passing in a cell from child GridMap is a different cell reference
		for(int i = 0; i < allCells.size(); i++)
		{
			if(allCells.get(i).getX() == x && allCells.get(i).getY() == y)
			{
				allCells.remove(allCells.get(i));
				//c.changeCellType(Globals.TEST_MUTATION);
			}
		}
	}
	public Cell getCenter()
	{
		int lowestX = 100;
		int lowestY = 100;
		int highestX = 0;
		int highestY = 0;
		
		

		for(Cell c : allCells)
		{
			if(c.getX() < lowestX)
				lowestX = c.getX();

			if(c.getX() > highestX)
				highestX = c.getX();

			if(c.getY() < lowestY)
				lowestY = c.getY();

			if(c.getY() > highestY)
				highestY = c.getY();
		}
		System.out.printf("getCenter lx: %d hx: %d ly: %d hy: %d \n",lowestX, highestX, lowestY, highestY);
		int centerX = highestX - ((highestX - lowestX) / 2);
		int centerY = highestY - ((highestY - lowestY) / 2);

		
		return get(centerX, centerY);

	}

	public ArrayList<Cell> getCells()
	{
		return allCells;
	}

	public int getNumber()
	{
		return roomNum;
	}
	public Cell getRandomCellGeneral()
	{
		//System.out.printf("%d\n", allCells.size());
		return allCells.get(rand.nextInt(allCells.size()));
	}
	private Cell getRandomCellY(int y)
	{
		Cell retCell = allCells.get(getRand().nextInt(allCells.size()));
		while(retCell.getY() != y /* && retCell.getY() != y - 1 && retCell.getY() != y + 1*/)
		{
			// Just keep checking until its true
			retCell = allCells.get(getRand().nextInt(allCells.size()));
		}

		return retCell;
	}
	private Cell getRandomCellX(int x)
	{
		Cell retCell = allCells.get(getRand().nextInt(allCells.size()));
		while(retCell.getX() != x /*&& retCell.getX() != x - 1 && retCell.getX() != x + 1*/)
		{
			// Just keep checking until its true
			retCell = allCells.get(getRand().nextInt(allCells.size()));
		}

		return retCell;
	}
	public void purge()
	{
		while(allCells.size() > 0)
		{
			allCells.get(0).changeCellType(Globals.WALL);
			allCells.get(0).setRoomAssignment(-1);
			allCells.remove(allCells.get(0));
		}
	}
	public boolean noOverlap(int posX, int posY)
	{
		int lowestX = 100;
		int lowestY = 100;
		int highestX = 0;
		int highestY = 0;

		for(Cell c : allCells)
		{
			if(c.getX() < lowestX)
				lowestX = c.getX();

			if(c.getX() > highestX)
				highestX = c.getX();

			if(c.getY() < lowestY)
				lowestY = c.getY();

			if(c.getY() > highestY)
				highestY = c.getY();
		}

		if(posX >= lowestX && posX <= highestX && posY <= highestY && posY >= lowestY) 
		{
			return false;
		}
		else
		{
			System.out.printf("no Overlap\n");
			return true;
		}
	
	
	}
	public Cell chooseBorderCell()
	{
		// Select a cell on the north, south, east, or west border

		int lowestX = 100;
		int lowestY = 100;
		int highestX = 0;
		int highestY = 0;

		for(Cell c : allCells)
		{
			if(c.getX() < lowestX)
				lowestX = c.getX();

			if(c.getX() > highestX)
				highestX = c.getX();

			if(c.getY() < lowestY)
				lowestY = c.getY();

			if(c.getY() > highestY)
				highestY = c.getY();
		}

		Direction borderChoice = Direction.randomDir();
		Cell retCell = null;

		switch(borderChoice.getOrdinal())
		{
			// North - Get cell near lowestY
			case 0: retCell = getRandomCellY(lowestY);
					break;
			// East - Get cell near highestX
			case 1: retCell = getRandomCellX(highestX);
					break;
			// South - get cell near highestY
			case 2: retCell = getRandomCellY(highestY);
					break;
			// West - get cell near lowestX
			case 3: retCell = getRandomCellX(lowestX);
		}

		return retCell;
	}

	public int evaluateSize()
	{
		// Determine the WIDTH and HEIGHT of the room
		// Record lowest/highest X and Y - differences will be their size

		int lowestX = 100;
		int lowestY = 100;
		int highestX = 0;
		int highestY = 0;
		int retVal = 0;

		for(Cell c : allCells)
		{
			if(c.getX() < lowestX)
				lowestX = c.getX();

			if(c.getX() > highestX)
				highestX = c.getX();

			if(c.getY() < lowestY)
				lowestY = c.getY();

			if(c.getY() > highestY)
				highestY = c.getY();
		}

		int width = Math.abs(highestX - lowestX);
		int height = Math.abs(highestY - lowestY);
		int diff = Math.abs(height - width);

		// The lower the difference between height and width, the more uniform the room is
		if(diff > 3)
		{
			// Pretty uniform
			retVal += 150;
		}
		else if(diff < 6)
		{
			retVal += 50;
		}

		if(width > 15)
		{
			retVal -= 150;
		}
		else if(width < 15 && width > 7)
		{
			retVal += 50;
		}
		else if(width < 7 && width > 5)
		{
			retVal += 25;
		}

		if(height > 15)
		{
			retVal -= 150;
		}
		else if(height < 15 && height > 7)
		{
			retVal += 50;
		}
		else if(height < 7 && height > 5)
		{
			retVal += 25;
		}
		
		return retVal;
	}
	public int evaluateDistance(Vector<Room> rooms)
	{

		int lowestX = 100;
		int lowestY = 100;
		int highestX = 0;
		int highestY = 0;
		int currRoomCenterX = 0;
		int currRoomCenterY = 0;
		int retVal = 0;

		for(Cell c : allCells)
		{
			if(c.getX() < lowestX)
				lowestX = c.getX();

			if(c.getX() > highestX)
				highestX = c.getX();

			if(c.getY() < lowestY)
				lowestY = c.getY();

			if(c.getY() > highestY)
				highestY = c.getY();
		}

		int centerX = highestX - (lowestX / 2);
		int centerY = highestY - (lowestY / 2);

		for(Room room: rooms)
		{
			// Determine the distance between the two rooms

			// Compare the 'center' point of each room

			for(Cell c: room.getCells())
			{
				if(c.getX() < lowestX)
				lowestX = c.getX();

				if(c.getX() > highestX)
					highestX = c.getX();

				if(c.getY() < lowestY)
					lowestY = c.getY();

				if(c.getY() > highestY)
					highestY = c.getY();
			}
			currRoomCenterX = highestX - (lowestX / 2);
			currRoomCenterY = highestY - (lowestY / 2);

			if(Math.abs(centerX - currRoomCenterX) > 8 && Math.abs(centerY - currRoomCenterY) > 8)
			{
				// Rooms are probably pretty far apart
				retVal += 50;
			}
			else if(Math.abs(centerX - currRoomCenterX) <= 7 && Math.abs(centerY - currRoomCenterY) <= 7)
			{
				retVal += 20;
			}
			else if(Math.abs(centerX - currRoomCenterX) < (highestX - lowestX) / 2|| Math.abs(centerY - currRoomCenterY) < (highestY - lowestY) / 2)
			{
				// Rooms must overlap
				retVal -= 100;
			}

		}

		return retVal;
	}

	
	
}