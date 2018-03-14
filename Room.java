import java.awt.Point;
import java.util.ArrayList;
import java.awt.Rectangle;

public class Room
{
	ArrayList<Cell> allCells;
	int roomNum;


	public Room(int numRoom)
	{
		allCells = new ArrayList<Cell>();
		roomNum = numRoom;
		
	}

	public void add(Cell c)
	{
		allCells.add(c);
	}

	public ArrayList<Cell> getCells()
	{
		return allCells;
	}

	public int getNumber()
	{
		return roomNum;
	}
}