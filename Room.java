import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.Rectangle;

public class Room
{
	ArrayList<Cell> allCells;
	int roomNum;
	boolean isNarrowHallwayType = false;



	public Room(int numRoom)
	{
		allCells = new ArrayList<Cell>();
		roomNum = numRoom;
		
	}

	public void add(Cell c)
	{
		allCells.add(c);
	}
	public int[] getCenter()
	{
		int lowestX = 100;
		int lowestY = 100;
		int highestX = 0;
		int highestY = 0;
		int[] center = new int[2];
		

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

		center[0] = centerX;
		center[1] = centerY;

		return center;

	}

	public ArrayList<Cell> getCells()
	{
		return allCells;
	}

	public int getNumber()
	{
		return roomNum;
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
		if(diff < 3)
		{
			// Pretty uniform
			retVal = 20;
		}
		else if(diff < 6)
		{
			retVal = 10;
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
				retVal += 20;
			}
			else if(Math.abs(centerX - currRoomCenterX) <= 7 && Math.abs(centerY - currRoomCenterY) <= 7)
			{
				retVal += 15;
			}
			else if(Math.abs(centerX - currRoomCenterX) < (highestX - lowestX) / 2|| Math.abs(centerY - currRoomCenterY) < (highestY - lowestY) / 2)
			{
				// Rooms must overlap
				retVal -= 25;
			}

		}

		return retVal;
	}

	
	
}