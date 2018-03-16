import java.awt.Point;
import java.util.ArrayList;
import java.awt.Rectangle;

public class Hallway
{
	ArrayList<Cell> allCells;
	int hallNum;
	

	public Hallway(int numRoom)
	{
		allCells = new ArrayList<Cell>();
		hallNum = numRoom;
		
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
		return hallNum;
	}

	
	
}