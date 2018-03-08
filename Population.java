import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

public class Population
{
	// Array of GridMaps
	private ArrayList<GridMap> allMaps;
	private double mutationRate;
	private double fitnessScores[];
	private int popSize;

	public Population(int size, int mapWidth, int mapHeight, double mutation) 
	{
		popSize = size;
		mutationRate = mutation;
		allMaps = new ArrayList<GridMap>(popSize);
		fitnessScores = new double[popSize];

		for(int i = 0; i < popSize; i++)
		{
			allMaps.add(i, new GridMap(mapWidth, mapHeight));
			allMaps.get(i).initialize();
			fitnessScores[i] = allMaps.get(i).evaluateFitness();
			allMaps.get(i).draw(Integer.toString(i));
			
			
		}
		// Sort in ascending order
		sortByFitness();

		// Choose a Map A and Map B for mating
		// TODO: Dont allot duplicate parents
		GridMap parentA = fitnessProportionateSelection();
		GridMap parentB = fitnessProportionateSelection();

		// Create offspring
		crossover(parentA, parentB);
		

	}
	private void crossover(GridMap parentA, GridMap parentB)
	{
		
	}
	private void sortByFitness()
	{
		// Put the maps in order from highest to lowest fitness
		Collections.sort(allMaps, new Comparator<GridMap>() {
			public int compare(GridMap g1, GridMap g2) {
				if(g1.getFitness() > g2.getFitness())
				{
					return 1;
				}
				else if(g1.getFitness() < g2.getFitness())
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
		});

	}

	private GridMap fitnessProportionateSelection()
	{
		// Sum of all fitness;
		double fitnessSum = 0;
		for(int i = 0; i < allMaps.size(); i++)
		{
			fitnessSum += allMaps.get(i).getFitness();
		}

		double[] probabilities = new double[allMaps.size()];
		double prevProbability = 0.0;
		for(int i = 0; i < allMaps.size(); i++)
		{
			probabilities[i] = prevProbability + (allMaps.get(i).getFitness() / fitnessSum);
			prevProbability = probabilities[i];
		}

		GridMap selectedParent;

		double value = new Random().nextDouble();
		System.out.printf("value: %f \n", value);

		for(int i = 0; i < allMaps.size(); i++)
		{
			if(value < probabilities[i])
			{
				selectedParent = allMaps.get(i);
				System.out.printf("selected: %f \n", probabilities[i]);
				return selectedParent;
			}
		}		

		// Fail safe - choose the most likely chosen map
		selectedParent = allMaps.get(allMaps.size());
		return selectedParent;
	}

}