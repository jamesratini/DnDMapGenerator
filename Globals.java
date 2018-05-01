public class Globals
{
	public static int ROOM = 0;
	public static int HALLWAY = 1;
	public static int WALL = 2;
	public static int DOOR = 3;
	public static int BLOCKED = 4;
	public static int MONSTER = 5;
	public static int TREASURE = 6;
	public static int TRAP = 7;
	public static int TEST_MUTATION = 8;
	public static int TEST_MUTATION_2 = 9;
	
	
	public int getNumTypes()
	{
		return getClass().getDeclaredFields().length;
	}
}