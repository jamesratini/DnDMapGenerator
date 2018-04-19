import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Rectangle;

public class Hallway
{
	ArrayList<Cell> allCells;
	Random rand;
	int hallNum;
	

	public Hallway(int numRoom)
	{
		allCells = new ArrayList<Cell>();
		hallNum = numRoom;
		rand = new Random();
		
	}

	public void add(Cell c)
	{
		allCells.add(c);
	}

	public ArrayList<Cell> getCells()
	{
		return allCells;
	}
	public int size()
	{
		return allCells.size();
	}
	public int getNumber()
	{
		return hallNum;
	}
	public Cell getRandomCell()
	{
		return allCells.get(rand.nextInt(allCells.size()));
	}
	public void purge()
	{
		for(int i = 0; i < allCells.size(); i++)
		{
			if((allCells.get(i).getCellType() != Globals.HALLWAY && allCells.get(i).getCellType() != Globals.TEST_MUTATION) && allCells.get(i).getHallwayAssignment() != getNumber())
			{
				allCells.remove(allCells.get(i));
				i--;
			}
		}
	}
	public void erase()
	{
		while(allCells.size() > 0)
		{
			allCells.remove(allCells.get(0));
		}
	}
	public void removeAllNonHallways()
	{

	}
	public boolean contains(int x, int y)
	{
		for(Cell c: allCells)
		{
			if(c.getX() == x && c.getY() == y)
			{
				return true;
			}
		}

		return false;
	}
	public void splitHallway()
	{
		// Choose splitpoint
		int i =rand.nextInt(allCells.size());
		allCells.get(i).changeCellType(Globals.WALL);
		allCells.get(i).setHallwayAssignment(-1);
		allCells.remove(i);

		for(int j =  i + 1; j < allCells.size(); j++)
		{
			allCells.get(i).setHallwayAssignment(-1);
		}
	}

	
	
}