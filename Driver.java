public class Driver
{
	public static void main(String args[])
	{
		// Generate population 
		int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 50;
		int y = args.length == 2 ? (Integer.parseInt(args[1])) : 50;

		Population pop = new Population(20, x, y, 0.5);

		pop.fitnessEvaluation();
		
	}
}