public class Driver
{
	public static void main(String args[])
	{
		int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 8;
		int y = args.length == 2 ? (Integer.parseInt(args[1])) : 8;
		GridMap testMap = new GridMap(x, y);
		testMap.Draw();
	}
}