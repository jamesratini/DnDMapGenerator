import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Population
{
	// Array of GridMaps
	private GridMap allMaps[];
	private double mutationRate;
	private double fitnessScores[];
	private int popSize;

	public Population(int size, int mapWidth, int mapHeight, double mutation)
	{
		popSize = size;
		mutationRate = mutation;
		allMaps = new GridMap[popSize];
		fitnessScores = new double[popSize];

		for(int i = 0; i < popSize; i++)
		{
			allMaps[i] = new GridMap(mapWidth, mapHeight);
			allMaps[i].initialize();
			fitnessScores[i] = allMaps[i].evaluateFitness();
			allMaps[i].draw(Integer.toString(i));
			System.out.printf("Map %d Fitness: %f \n", i, fitnessScores[i]);
			
		}


	}

}