import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

public class Population
{
	// Array of GridMaps
	private ArrayList<GridMap> allMaps;
	private double mutationRate;
	private int popSize;
	private int mapWidth;
	private int mapHeight;
	private int elitismSelectionMargin;

	// First and only first population constructor
	public Population(int size, int mapWidth, int mapHeight, double mutation) 
	{
		popSize = size;
		mutationRate = mutation;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		allMaps = new ArrayList<GridMap>(popSize);
		

	}

	public void initialize()
	{
		for(int i = 0; i < popSize; i++)
		{
			// Starting Population
			// Type of map (Cavern, Castle, etc) will be decided here
			// Cavern as default for now

			// TODO: user selects type of map (determines fitness function)
			allMaps.add(i, new GridMap(mapWidth, mapHeight));
			allMaps.get(i).initialize();
		}
	}

	public ArrayList<GridMap> getMaps()
	{
		return allMaps;
	}

	private GridMap crossover(GridMap parentA, GridMap parentB)
	{
		// Crossover will need to be much more complex for maps
		// I mean come on, this is gonna be insane
		// First iteration : basic cross over
		// Treat each row in the maps like chromosomes and one point cross over
		GridMap offspring = new GridMapCavern(parentA, parentB, getMutationRate());
		return offspring;
	}
	public GridMap getGridMap(int index)
	{
		return allMaps.get(index);
	}
	private void addGridMap(GridMap map)
	{
		allMaps.add(map);
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
			//System.out.printf("prob: %f \n", probabilities[i]);
			prevProbability = probabilities[i];
		}

		GridMap selectedParent;

		double value = new Random().nextDouble();

		for(int i = 0; i < allMaps.size(); i++)
		{
			if(value < probabilities[i])
			{
				selectedParent = allMaps.get(i);
				return selectedParent;
			}
		}		

		// Fail safe - choose the most likely chosen map
		selectedParent = allMaps.get(allMaps.size());
		return selectedParent;
	}
	public int getPopSize()
	{
		return popSize;
	}
	public int getMapWidth()
	{
		return mapWidth;
	}
	public int getMapHeight()
	{
		return mapHeight;
	}
	public double getMutationRate()
	{
		return mutationRate;
	}
	public int getElitismSelectionMargin()
	{
		return elitismSelectionMargin;
	}
	public Population generateNextGen(int elitismIndex)
	{
		// Elitism selection - pass the top X of this population to the new generation
		Population newPop = new Population(popSize, getMapWidth(), getMapHeight(), getMutationRate());

		// sort THIS population's maps by fitness
		sortByFitness();
		

		// allMaps is sorted in descending order, so highest fitness maps are located at the end
		for(int i = allMaps.size() - 1; i > allMaps.size() - elitismIndex - 1; i--)
		{
			newPop.addGridMap(allMaps.get(i));
		}

		// Fill new population with this populations children
		for(int i = elitismIndex; i < allMaps.size(); i++)
		{
			
			newPop.addGridMap(produceChildMap());
			
			
		}

		
		

		return newPop;

	}

	private GridMap produceChildMap()
	{


		// Choose a Map A and Map B for mating
		// TODO: Dont allot duplicate parents
		GridMap parentA = fitnessProportionateSelection();
		GridMap parentB = fitnessProportionateSelection();

		while(parentA == parentB)
		{
			parentB = fitnessProportionateSelection();
		}

		// Create offspring
		GridMap child = crossover(parentA, parentB);

		return child;
	}

}
