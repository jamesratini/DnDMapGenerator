public class Globals
{
	public static int ROOM = 1;
	public static int HALLWAY = 2;
	public static int WALL = 3;
	public static int DOOR = 4;
	public static int BLOCKED = 5;
	
	public int getNumTypes()
	{
		return getClass().getDeclaredFields().length;
	}
}