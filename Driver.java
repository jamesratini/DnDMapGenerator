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
		int numGens = 1;

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

				GridMap roomsTestGrid = pop.getGridMap(pop.getPopSize() - 1);
				System.out.printf("Map Name: %s",roomsTestGrid.getName());
				Vector<Room> roomsTestVector = roomsTestGrid.getRoomsVector();
				ArrayList<Cell> cells;
				cells = roomsTestVector.get(roomsTestVector.size() - 1).getCells();
				System.out.printf("num: %d \n", roomsTestVector.get(roomsTestVector.size() - 1).getNumber());
				for(Cell zz: cells)
				{
					System.out.printf("x: %d, y: %d \n", zz.getX(), zz.getY());
				}
			
			pop = pop.generateNextGen(5);
		}
		
		// Test rooms
	

		
		
		
	}
}