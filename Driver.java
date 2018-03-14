import java.util.*;

public class Driver
{
	public static void main(String args[])
	{
		// Generate population 
		int x = 50; //args.length >= 1 ? (Integer.parseInt(args[0])) : 50;
		int y = 50; //args.length == 2 ? (Integer.parseInt(args[1])) : 50;

		// Must be included args
		int mapType = 1; // 1 = cavern
		int numGens = 2;

		Population pop = new Population(20, x, y, 0.001);
		pop.initialize();
		

		for(int j = 0; j < numGens; j++)
		{
			System.out.printf("Starting generation %d out of %d \n", j, numGens);
			
			
			for(GridMap map : pop.getMaps())
			{
				// Evaluate each maps fitness then draw the map
				// Depending on which map the user selects, the fitness function changes
				//AQ.add(() -> {
					 map.evaluateFitness();
				///});	
			}
			
			//AQ.finish();

			for(int i = 0; i < pop.getPopSize(); i++)
			{
				pop.getGridMap(i).Draw(Integer.toString((j * pop.getPopSize() + 1) + i));
			}
			
			pop = pop.generateNextGen(5);
		}
		
		// Test rooms
		GridMap roomsTestGrid = pop.getGridMap(pop.getPopSize() - 1);
		Vector<Room> roomsTestVector = roomsTestGrid.getRoomsVector();
		ArrayList<Cell> cells;
		cells = roomsTestVector.get(0).getCells();
		System.out.printf("num: %d \n", roomsTestVector.get(0).getNumber());
		for(Cell zz: cells)
		{
			//System.out.printf("x: %d, y: %d \n", zz.getX(), zz.getY());
		}

		
		
		
	}
}