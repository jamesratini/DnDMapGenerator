import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;
import java.lang.Math;
import java.util.Vector;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

// Class to inherit from the base GridMap
// All first generation maps will be GridMap and all future generations will be a child class
public class GridMapCavern extends GridMap
{
	double mutationRate;
	List<Direction> allDir;
	
	// Constructor for all maps after first generation
	public GridMapCavern(GridMap parentA, GridMap parentB, double mutationRate)
	{
		super(parentA.getWidth(), parentA.getHeight());
		// This constructor will be used for crossover

		this.mutationRate = mutationRate;
		
		int crossoverParentSelection;
		Vector<Hallway> parentAHallways = (Vector)parentA.getHallwaysVector().clone();
		Vector<Hallway> parentBHallways = (Vector)parentB.getHallwaysVector().clone();
		Vector<Room> parentARooms = (Vector)parentA.getRoomsVector().clone();
		Vector<Room> parentBRooms = (Vector)parentB.getRoomsVector().clone();
		Room parentRoom;
		Hallway parentHall;


		// Hallway crossover
		int numChildGenes = (parentA.getHallwaysVector().size() + parentB.getHallwaysVector().size() ) / 2;
		int parentSelection;

		for(int i = 0; i < numChildGenes; i++)
		{
			parentSelection = getRand().nextInt(2);

			if(parentSelection == 0 && parentAHallways.size() > 0)
			{
				// ParentA
				parentHall = parentAHallways.remove(getRand().nextInt(parentAHallways.size()));


			}
			else if(parentSelection == 1 && parentBHallways.size() > 0)
			{
				// ParentB
				parentHall = parentBHallways.remove(getRand().nextInt(parentBHallways.size()));
			}
			else
			{
				break;
			}
		

			// allTiles cells are all null at this point
			// Loop through parents room and assign corresponding cells to ROOM
			for(Cell c : parentHall.getCells())
			{
				// Mutation occurs here
				super.getCell(c.getX(), c.getY()).changeCellType(c.getCellType());
			}
		}

		// Room Crossover
		// number of child rooms = average of 2 parents
		// choose a room randomly from parents
		numChildGenes = (parentA.getRoomsVector().size() + parentB.getRoomsVector().size()) / 2;
		

		for(int i = 0; i < numChildGenes; i++)
		{
			parentSelection = getRand().nextInt(2);

			if(parentSelection == 0 && parentARooms.size() > 0)
			{
				// ParentA
				
				parentRoom = parentARooms.remove(getRand().nextInt(parentARooms.size()));


			}
			else if(parentSelection == 1 && parentBRooms.size() > 0)
			{
				// ParentB
				
				parentRoom = parentBRooms.remove(getRand().nextInt(parentBRooms.size()));
				//parentRoom = mutateRoom(parentRoom);
			}
			else
			{
				break;
			}
		

			// allTiles cells are all null at this point
			// Loop through parents room and assign corresponding cells to ROOM
			for(Cell c : parentRoom.getCells())
			{
				// Mutation occurs here
				super.getCell(c.getX(), c.getY()).changeCellType(c.getCellType());
			}
		}

		


		/*int rowOrColCrossOver = super.getRand().nextInt(2);

		for(int i = 0; i < super.getWidth(); i++)
		{
			// Choose a crossover point
			// Choose the parent to be the "left"
			// Combine the row or col of each parent
			crossoverPoint = super.getRand().nextInt(super.getHeight());
			crossoverParentSelection = super.getRand().nextInt(2);
			

			if(crossoverParentSelection == 0)
			{
				leftParent = parentA;
				rightParent = parentB;
			}
			else
			{
				leftParent = parentB;
				rightParent = parentA;
			}

			Cell currCell = null;

			for(int j = 0; j < super.getHeight(); j++)
			{
				
				if(rowOrColCrossOver >= 0)
				{
					// Column crossover

				
					if(getRand().nextInt(getHeight()) <= crossoverPoint)
					{
						// Mutation occurs here
						
						super.getCell(i, j).changeCellType(mutate(leftParent.getCell(i, j), mutationRate).getCellType());
					}
					else
					{
						// Mutation occurs here
						super.getCell(i, j).changeCellType(mutate(rightParent.getCell(i, j), mutationRate).getCellType());
					}
					
					
					assignToVector(super.getCell(i,j));
					
				}
				else
				{
					//Row crossover
					
					/*if(getRand().nextInt(getHeight()) <= crossoverPoint)
					{
						// Mutation occurs here
						super.getCell(j, i).changeCellType(mutate(leftParent.getCell(j, i), mutationRate).getCellType());
					}
					else
					{
						// Mutation occurs here
						super.getCell(j, i).changeCellType(mutate(rightParent.getCell(j, i), mutationRate).getCellType());
					}
					
					assignToVector(super.getCell(j,i));
				}

				

			}

		}*/

		fillVectors();


	}


	@Override
	public void initialize()
	{
		// Lets just make sure if this gets called, it doesnt erase the map
		System.out.printf("Initialize was attempted to be called from a cavern map \n");
	}

	@Override
	public Cell mutate(Cell cellForMutation, double rate)
	{
		
		double chance = getRand().nextDouble();
		int cellTypeChosen = getRand().nextInt(getNumCellTypes());

		if(chance < rate)
		{
			// Forced mutation - Ensure this cavern doesn't contain hallways or doors
			while(cellTypeChosen == 1 || cellTypeChosen == 3)
			{
				cellTypeChosen = getRand().nextInt(getNumCellTypes());
			}
			// Mutate
			cellForMutation.changeCellType(getRand().nextInt(getNumCellTypes()));
		} 
		

		return cellForMutation;
	}
	public Room mutateRoom(Room r)
	{
		// Expand
		// Contract
		// Mutate cells within normally
		// "Carve" - remove a small chunk from the room
			// Enough carves may make it look more natural cave like?

		// Option 2
		// mutate normally in an area around the room

		return r;
	}

	public Hallway mutateHall(Hallway h)
	{
		// "Fill"
			// If a wall or blocked is between two hallways, make it a hallways
				// Hopefully this will result in hallways expanding
		// Burst
			// Choose cell and set all surrounding cells to hallway no matter what
		// Contract
			// Set self and surroundings(maybe) to wall or blocked
		// Normal mutation
			// Change cell type randomly

		// Option 2
		// Choose center cell and mutate normally in an area around it

		return h;
	}

	@Override
	public int evaluateFitness()
	{
		

		int myFit = 0;

		// Should be a lot of rooms
		// Rooms should be large and non-uniform
		myFit += evaluateHallways();
		myFit += evaluateRooms();
		
		//myFit += evaluateDoors();

		if(myFit <= 0)
		{
			myFit = 1;
		}

		return getRand().nextInt(1000);

		
	}

	// Evaluate cavern's walls
	// Walls can exist in the middle of large rooms and act as a "support beam" type structure
	// Walls should line all the rooms that make up the cavern
	
	private int evaluateWalls()
	{
		// Walls can be short clumps inside rooms. Act as pillars in the cave
		// Walls should be fairly abundant

		Vector<Cell> walls = getWallsVector();
		int fitness = 0;

		if(walls.size() < 50)
		{
			fitness -= 0;
		}
		else if(walls.size() > 50 && walls.size() < 100)
		{
			fitness += 20;
		}
		else
		{
			fitness -= 0;
		}

		// If a wall is surrounded by rooms or all rooms and 1 other wall, thats good
		for(Cell wall : walls)
		{
			// 3 possibilities
			// surrounded by rooms / surrounded by rooms + 1 or 2 walls / not touching rooms
			switch(wallInRoom(wall))
			{
				case 0: fitness += 15;
						break;

				case 1: fitness += 10;
						break;

				case 2: fitness -= 2;
			}

		}

		// Ideally, walls should surround rooms
		// If a wall touches a room on one side and all other sides are walls - increase fitness
		// If a wall touches a room on one side and some other sides are walls - sorta increase fitness
		for(Cell wall : walls)
		{
			switch(wallSurroundings(wall))
			{
				// Wall in room or surrounded by walls
				case 0: case 1: fitness += 25; break;
				// Wall borders a room
				case 2: fitness += 20; break;
				// Wall exists somewhere, but not in an ideal spot
				case 3: fitness += 5; break;
				// Wall exists
				case 4: break;

			}
			
		}

		return fitness;


	}

	// Evaluate cavern's hallways
	
	
	private int evaluateHallways()
	{
		int fitness = 0;
		Vector<Hallway> hallways = getHallwaysVector();
		Vector<Room> rooms = getRoomsVector();
		
		// Hallways are supposed to connect rooms
			// for each hallways see if it relatively between 2 rooms
			// If a hallway cell is bordering a room, good
		// Hallways shouldnt be 1 wide in a cave
			// Scan area around a cell and see if the hall is "wide"
				// width of 2 to 5 is probably ideal and gives room for variety
		// Hallways should be relatively straight
			// If can go from room A to room B without turning maybe too straight?
		int[] centerCellOne;
		int[] centerCellTwo;
		int diffX;
		int diffY;
		
		// 4 nested loops? Must be a better way
		for(Hallway hall: hallways)
		{
			for(Cell currCell : hall.getCells())
			{
				for(Room roomOne: rooms)
				{
					centerCellOne = roomOne.getCenter();
					for(Room roomTwo : rooms)
					{
						centerCellTwo = roomTwo.getCenter();

						// Determine if this cell is between two rooms
						if((Math.abs(centerCellOne[0] - currCell.getX()) < 3 && Math.abs(centerCellTwo[0] - currCell.getX()) < 3) || (Math.abs(centerCellOne[1] - currCell.getY()) < 3 && Math.abs(centerCellTwo[1] - currCell.getY()) < 3))
						{
							// Current cell is roughly between the two rooms
							fitness += 10;
						}

								
					}
				}

				// Check if currCell is a room border
				// Scan surrounding cells to see if theyre also a hallway
				// 	
			}
			
			
		}
		return fitness;
	}

	// Evaluate cavern's rooms
	// Rooms should be large and non-uniform
	// Some rooms should be large in width and height, more rooms should be more narrow almost like a hallways
		// So the players have areas to explore and arent just in a massive room
	
	private int evaluateRooms()
	{
		int fitness = 0;
		Vector<Room> rooms = getRoomsVector();
		
		// Rooms should be generally large
		// Rooms should NOT overlap with one another
		// Rooms should try to seperate
		// Rooms should try to connect to hallways

		for(Room r: rooms)
		{
			fitness += r.evaluateSize();
			//fitness += r.evalauteOverlap(rooms);
			fitness += r.evaluateDistance(rooms);
		}
		
		


		return fitness;
	}

	// Evaluate cavern's doors
	// The only time doors should exist in a cave is if intelligent monsters live in the cave; even then it should be fairly rare

	private int evaluateDoors()
	{
		// Doors shouldn't exist in a cave
		// The less doors, the better fitness
		
		int fitness = 0;
		Vector<Cell> doors = getDoorsVector();
		
		if(doors.size() == 0 || doors.size() < 10)
		{
			fitness += 10;
		}
		else if(doors.size() > 10 && doors.size() < 25)
		{
			fitness += 2;
		}
		else
		{
			fitness -= doors.size();
		}

		return fitness;
	}

	// FITNESS FUNCTION HELPERS
	private int wallSurroundings(Cell wall)
	{
		// if wall touches a room on one side
		int wallX = wall.getX();
		int wallY = wall.getY();
		int wallCount = 0;
		int roomCount = 0;

		for(Direction dir : allDir)
		{
			if(!outOfBounds(wallX + dir.dx, wallY + dir.dy) && super.getCell(wallX + dir.dx, wallY + dir.dy).getCellType() == Globals.ROOM)
			{
				roomCount++;
			}
			else if(!outOfBounds(wallX + dir.dx, wallY + dir.dy) && super.getCell(wallX + dir.dx, wallY + dir.dy).getCellType() == Globals.WALL)
			{
				wallCount++;
			}


		}

		if(((roomCount == 4 || roomCount == 3) && (wallCount == 0 || wallCount == 1)))
		{
			// Wall in the middle of a room, possibly next to another wall
			// Acceptable 
			return 0;
		}
		else if(wallCount == 4)
		{
			// Surrounded by other walls, good, in a cavern everything surrounding the rooms should be a wall
			return 1;
		}
		else if(roomCount == 1)
		{
			// Wall borders a room, perfect
			return 2;
		}
		else if(roomCount == 0 && wallCount == 0)
		{
			// Wall exists somewhere, but not in a good spot
			// The wall should be there though, dont punish a cavern for having walls
			return 3;
		}
		else
		{

			return 4;
		}
		
	}
}