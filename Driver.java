import java.util.*;

public class Driver
{
	public static void main(String args[])
	{
		// Generate population 
		int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 50;
		int y = args.length == 2 ? (Integer.parseInt(args[1])) : 50;

		// Must be included args
		int mapType = 1; // 1 = cavern
		int numGens = 200;

		Population pop = new Population(20, x, y, 0.50);
		pop.initialize();

		for(GridMap map : pop.getMaps())
		{
			// Evaluate each maps fitness then draw the map
			// Depending on which map the user selects, the fitness function changes
			//AQ.add(() -> {
				 map.evaluateFitness();
			//});	
		}
		
			

		for(int i = 0; i < pop.getPopSize(); i++)
		{
			pop.getGridMap(i).Draw(Integer.toString(i + 1));
		}
		//AQ.finish();
	

		// Test hallways
		for(int j = 0; j < numGens; j++)
		{
			System.out.printf("Starting generation %d out of %d \n", j, numGens);
			
			pop = pop.generateNextGen(5);

			for(GridMap map : pop.getMaps())
			{
				// Evaluate each maps fitness then draw the map
				// Depending on which map the user selects, the fitness function changes
				AQ.add(() -> {
				//System.out.printf("evaluating map\n");
					 map.evaluateFitness();
					  //System.out.printf("Fitness: %f\n", map.getFitness());
				});	
			}
			

			for(int i = 0; i < pop.getPopSize(); i++)
			{
				pop.getGridMap(i).Draw(Integer.toString(((j + 1) * pop.getPopSize() + 1) + i));
			}

			AQ.finish();

	
			
		}
	}
}