import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Population
{
	// Array of GridMaps
	private GridMap allMaps[];
	private double mutationRate;
	private float fitnessScores[];
	private int popSize;

	public Population(int size, int mapWidth, int mapHeight, double mutation)
	{
		popSize = size;
		mutationRate = mutation;
		allMaps = new GridMap[popSize];
		fitnessScores = new float[popSize];

		for(int i = 0; i < popSize; i++)
		{
			allMaps[i] = new GridMap(mapWidth, mapHeight);

			allMaps[i].Draw();
		}
	}
	public void fitnessEvaluation()
	{
		// Iterate through each map
		// Run the current fitness function on each map
		// Store the fitness scores of each map
		// Reproduce

		for(int i = 0; i < popSize; i++)
		{
			fitnessScores[i] = allMaps[i].evaluateFitness();
		}

		for(int i = 0; i < popSize; i++)
		{
			System.out.println(fitnessScores[i]);
		}
	}
}