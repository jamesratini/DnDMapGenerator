public class Driver
{
	public static void main(String args[])
	{
		// Generate population 
		int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 50;
		int y = args.length == 2 ? (Integer.parseInt(args[1])) : 50;
		int numGens = 1;

		Population nextPop;
		Population pop = new Population(20, x, y, 0.5);

		//pop = pop.generateNextGen(5);

		for(int j = 0; j < numGens; j++)
		{
			pop = pop.generateNextGen(5);
			for(int i = 0; i < pop.getPopSize(); i++)
			{
				// Evaluate each maps fitness then draw the map
				// Depending on which map the user selects, the fitness function changes
				pop.getGridMap(i).evaluateFitnessCavern();
				pop.getGridMap(i).draw(Integer.toString((j * 10) + i));
			}
		}
		

		
		
	}
}