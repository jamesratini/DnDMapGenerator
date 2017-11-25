import java.awt.Point;
import java.awt.Rectangle;

public class RoomBlock
{
	Cell roomTiles[][];
	int xLower; // Left bound
	int xUpper; // Right bound
	int yLower; // Upper bound
	int yUpper; // Lower bound
	Rectangle surroundingBoundary;

	public RoomBlock(int xLow, int xHigh, int yLow, int yHigh)
	{
		xLower = xLow;
		xUpper = xHigh;
		yLower = yLow;
		yUpper = yHigh;
		surroundingBoundary = new Rectangle(xLower - 1, yLower - 1, xUpper + 1, yUpper + 1);
	}

	// Returns true of 2 rectangles overlap
	public boolean doOverlap(int checkX, int checkY, int roomWidth, int roomHeight)
	{

		
		boolean overlap = true;

		
		Rectangle checkRect = new Rectangle(checkX, checkY, roomWidth, roomHeight);

		return surroundingBoundary.intersects(checkRect);
	}
	public boolean checkIfCollision(int posX, int posY)
	{
		
		// Returns true if a cell collides with a room

		Rectangle room = new Rectangle(xLower, yLower, xUpper - xLower, yUpper - yLower);


		return room.contains(new Point(posX, posY));
	}
	public int getLowX()
	{
		return xLower;
	}

	public int getHighX()
	{
		return xUpper;
	}

	public int getLowY()
	{
		return yLower;
	}

	public int getHighY()
	{
		return yUpper;
	}
}