public class Cell
{
	private int cellType;
	private int posX;
	private int posY;

	public Cell(int coordinateX, int coordinateY, boolean wall)
	{
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