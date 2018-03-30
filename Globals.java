public class Globals
{
	public static int ROOM = 0;
	public static int HALLWAY = 1;
	public static int WALL = 2;
	public static int DOOR = 3;
	public static int BLOCKED = 4;
	public static int TEST_MUTATION = 5;
	public static int TEST_MUTATION_2 = 6;
	
	
	public int getNumTypes()
	{
		return getClass().getDeclaredFields().length;
	}
}