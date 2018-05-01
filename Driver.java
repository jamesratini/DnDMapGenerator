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
		int numGens = 500;
		
		Population pop = new Population(10, x, y, 0.01);
		pop.initialize();

		GridMap highestFit = pop.getGridMap(0);
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
		int j =0;
		while(highestFit.getFitness() < 100000.00)
		{
			System.out.printf("Starting generation %d\n", j);
			
			pop = pop.generateNextGen(5);

			for(GridMap map : pop.getMaps())
			{
				// Evaluate each maps fitness then draw the map
				// Depending on which map the user selects, the fitness function changes
				//AQ.add(() -> {
				//System.out.printf("evaluating map\n");
					map.evaluateFitness();
					//System.out.printf("Fitness: %f\n", map.getFitness());
				//});	
				if(map.getFitness() > highestFit.getFitness())
				{

					highestFit = map;
				}
			}
			
			for(int i = 0; i < pop.getPopSize(); i++)
			{
				pop.getGridMap(i).Draw(Integer.toString(j + 1) + Integer.toString(i) + " - " + pop.getGridMap(i).getFitness() + " - " + pop.getGridMap(i).getHallwaysVector().size());
			}
			
			//pop.drawHighestFit(j);
			//highestFit.Draw(Integer.toString(j) + " - " + highestFit.getFitness() + " - " + highestFit.getHallwaysVector().size() + " - " + highestFit.getRoomsVector().size());
			//highestFit = 0;
			//AQ.finish();

			j++;
			
		}
	}

}