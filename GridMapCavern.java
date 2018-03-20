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
import java.util.ArrayList;
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
		
		allDir = Arrays.asList(Direction.values());	
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
		
			if(getRand().nextDouble() < mutationRate)
			{
				parentHall = mutateHall(parentHall);
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

			// Mutation occurance
			// 
			// NOTE: mutateRoom adds mutated cells to the room
			//		 then the loop below will 
			//		 then fillVectors below will make sure the mutated cells are added to the room for future generations
			if(getRand().nextDouble() < mutationRate && parentRoom.getCells().size() > 0)
			{
				parentRoom = mutateRoom(parentRoom);
			}
		

			// allTiles cells are all null at this point
			// Loop through parents room and assign corresponding cells to ROOM
			for(Cell c : parentRoom.getCells())
			{
				// On super constructor all cells are basic WALL or BLOCKED
				// Mutation occurs here
				getCell(c.getX(), c.getY()).changeCellType(c.getCellType());
			}
		}

		fillVectors();

		


	}


	@Override
	public void initialize()
	{
		// Lets just make sure if this gets called, it doesnt erase the map
		System.out.printf("Initialize was attempted to be called from a cavern map \n");
	}


	
	public Room mutateRoom(Room r)
	{
		// Expand
		// Contract
		// Mutate cells within normally
		// "Carve" - remove a small chunk from the room
			// Enough carves may make it look more natural cave like?
		// Relocation - change where the room exists and make sure it doesnt overlap
		// De-uniformation
			// Find room borders
			// Chance to turn border cell into a wall

		// Option 2
		// mutate normally in an area around the room

		int mutationSelection = getRand().nextInt(4);

		if(mutationSelection == 0 || mutationSelection == 1)
		{
			//System.out.printf("Room Expansion Mutation\n");
			// Room expansion mutation

			// Choose center cell
			Cell c = r.chooseBorderCell();
			//System.out.printf("Selected border cell (%d,%d)\n", c.getX(), c.getY());

			// Define the area we will be expanding
			int width = getRand().nextInt(3) + 1;
			int height = getRand().nextInt(3) + 1;

			//System.out.printf("mutation area %d x %d \n", width, height);

			for(int i = c.getX() - width; i < c.getX() + width; i ++)
			{
				for(int j = c.getY() - height; j < c.getY() + height; j++)
				{
					// For each cell within the expansion range
					// Check if the cell would be out of bounds, if false - change to a room;

					if(!outOfBounds(i, j))
					{
						getCell(i, j).changeCellType(Globals.ROOM);
						
					} 

				}
			}

		}
		/*else if(mutationSelection == 1)
		{
			// Room contraction mutation
			//System.out.printf("Room Contraction Mutatiopn\n");

			// Choose center cell (will be near a border of the room)
			Cell border = r.chooseBorderCell();
			//System.out.printf("Selected border cell (%d,%d)\n", border.getX(), border.getY());

			// Define contraction area (very small)
			int width = getRand().nextInt(2) + 1;
			int height = getRand().nextInt(2) + 1;

			// For each cell within the contraction area, set to WALL and remove from the room


			for(Cell c: r.getCells())
			{
				// If cell is in the area, remove it
				if(c.getX() > border.getX() - width && c.getX() < border.getX() + width && c.getY() > border.getY() - height && border.getY() < border.getY() + height)
				{
					
					getCell(c.getX(), c.getY()).changeCellType(Globals.WALL);
					getCell(c.getX(), c.getY()).setRoomAssignment(-1);
					c.changeCellType(Globals.WALL);
				}
			}
		}*/
		else if( mutationSelection == 2)
		{
			// Room carve mutation
			//System.out.printf("Carve Mutation\n");

			// Choose random room tile and select carve zone - Similar to contract but carve can choose tiles in the center of the room
			Cell carveOrigin = r.getRandomCellGeneral();

			//System.out.printf("Selected carve origin (%d, %d)\n", carveOrigin.getX(), carveOrigin.getY());
			int width = getRand().nextInt(2) + 1;
			int height = getRand().nextInt(2) + 1;

			//System.out.printf("mutation area %d x %d \n", width, height);

			for(int i = carveOrigin.getX() - width; i < carveOrigin.getX() + width; i++)
			{
				for(int j = carveOrigin.getY() - height; j < carveOrigin.getY() + height; j++)
				{
					if(!outOfBounds(i, j))
					{
						getCell(i, j).changeCellType(Globals.WALL);
						getCell(i,j).setRoomAssignment(-1);
						
					}
				}
			}
		}
		else if (mutationSelection == 3)
		{
			// Grow hallways out from the room
			int[] centerCell = r.getCenter();

			Direction dir = Direction.randomDir();

			int length = getRand().nextInt(30);

			for(int i = 0; i < length; i++)
			{
				if(!outOfBounds(centerCell[0] + (i * dir.dx), centerCell[1] + (i * dir.dy)) && getCell(centerCell[0] + (i * dir.dx), centerCell[1] + (i * dir.dy)).getCellType() != Globals.ROOM)
				{
					getCell(centerCell[0] + (i * dir.dx), centerCell[1] + (i * dir.dy)).changeCellType(Globals.TEST_MUTATION);
					getCell(centerCell[0] + (i * dir.dx), centerCell[1] + (i * dir.dy)).setHallwayAssignment(-1);
				}
				else
				{
					break;
				}
			}


		}
		/*else if(mutationSelection == 4)
		{
			System.out.printf("Relocation\n");
			// Relocation
			//int numberTests = 7;
			//Cell[] checkCell = new Cell[numberTests];
			boolean safeToRelocate = true;
			int xMovement = getRand().nextInt(30) - 15;
			int yMovement = getRand().nextInt(30) - 15;

			// Mini test to see if the room will overlap with another
				// N random cells - check if cell X and Y are in other room


			if(safeToRelocate == true)
			{
				System.out.printf("relocation successful\n");
				// Relocation would, hopefully, not overlap with other rooms
				ArrayList<Cell> roomCells = r.getCells();
				for(int i = 0; i < roomCells.size();i++)
				{
					if(!outOfBounds(roomCells.get(i).getX() + xMovement, roomCells.get(i).getY() + yMovement))
					{
						getCell(roomCells.get(i).getX() + xMovement, roomCells.get(i).getY() + yMovement).changeCellType(Globals.TEST_MUTATION);
						roomCells.get(i).changeCellType(Globals.RELOCATED);
						roomCells.remove(roomCells.get(i));
					}
				}
			}
		}*/

		return r;
	}

	public Hallway mutateHall(Hallway h)
	{
		// Split
			// Split the hallway into 2
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

		int mutationSelection = getRand().nextInt(3);

		if(mutationSelection == 1)
		{
			// Fill Mutation
			System.out.printf("Fill Mutation Go \n");

			Cell origin;
			
			// Choose a starting cell randomly in the hallway
			for(int i = 0; i < getRand().nextInt(3) + 1; i++)
			{
				origin = h.getRandomCell();
				

				// Check all directions
					// If x or y + 1 == wall/blocked but x or y + 2 is a hallway, set x or y + 1 to a hallway
				for( Direction dir : allDir)
				{
					if((!outOfBounds(origin.getX() + dir.dx, origin.getY() + dir.dy) && !outOfBounds(origin.getX() + dir.dx * 2, origin.getY() + dir.dy *2)) && (getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).getCellType() == Globals.WALL || getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).getCellType() == Globals.BLOCKED) && h.contains(origin.getX() + (dir.dx * 2), origin.getY() + (dir.dy * 2)))
					{
						getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).changeCellType(Globals.HALLWAY);
						getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).setHallwayAssignment(-1);
						//h.add(getCell(origin.getX() + dir.dx, origin.getY() + dir.dy));
					}
				}
			}
			
		}
		else if(mutationSelection == 2 || mutationSelection == 0)
		{
			// Burst Mutation
			System.out.printf("Burst Mutation Go\n");

			// Choose origin cell and set all surrounding cells to hallways no matter what
			Cell origin = h.getRandomCell();
			int width = -3;
			int height = -3;
			for(int i = width; i < Math.abs(width); i++)
			{
				for(int j = height; j < Math.abs(height); j++)
				{
					if(!outOfBounds(origin.getX() + i, origin.getY() + j))
					{
						getCell(origin.getX() + i, origin.getY() + j).changeCellType(Globals.HALLWAY);
						getCell(origin.getX() + i, origin.getY() + j).setHallwayAssignment(-1);
						//h.add(getCell(origin.getX() + i, origin.getY() + j));
					}
				}
			}
		}


		
		return h;
	}

	@Override
	public int evaluateFitness()
	{
		

		int myFit = 0;

		// Should be a lot of rooms
		// Rooms should be large and non-uniform
		if(getRoomsVector().size() > 10 && getRoomsVector().size() < 15)
		{
			myFit += 100;
		}
		else if(getRoomsVector().size() > 5)
		{
			myFit +=40;
		}
		else if(getRoomsVector().size() < 5)
		{
			myFit -= 50;
		}
		myFit += evaluateHallways();
		myFit += evaluateRooms();
		
		//myFit += evaluateDoors();

		if(myFit <= 0)
		{
			myFit = 1;
		}

		return myFit;

		
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

		int roomCount = 0;
		int hallCount = 0;
		int wallCount = 0;
		
		// 4 nested loops? Must be a better way
		for(Hallway hall: hallways)
		{
			for(Cell currCell : hall.getCells())
			{
				
				roomCount = 0;
				hallCount = 0;
				wallCount = 0;
				if(rooms.size() > 0)
				{
					centerCellOne = rooms.get(getRand().nextInt(rooms.size())).getCenter();
				
					centerCellTwo = rooms.get(getRand().nextInt(rooms.size())).getCenter();
					while(centerCellOne == centerCellTwo)
					{
						centerCellTwo = rooms.get(getRand().nextInt(rooms.size())).getCenter();
					}

					// Determine if this cell is between two rooms
					if((Math.abs(centerCellOne[0] - currCell.getX()) < 3 && Math.abs(centerCellTwo[0] - currCell.getX()) < 3) && (Math.abs(centerCellOne[1] - currCell.getY()) < 3 && Math.abs(centerCellTwo[1] - currCell.getY()) < 3))
					{
						// Current cell is roughly between the two rooms
						fitness += 100;
					}		
				}
				else
				{
					// this map doesnt have any rooms somehow, destroy fitness
					fitness -= 10000;
				}
				

								
					
				

				// Check if currCell is a room border
				// Scan surrounding cells to see if theyre also a hallway

				
				for(Direction dir: allDir)
				{
					if(!outOfBounds(currCell.getX() + dir.dx, currCell.getY() + dir.dy) && getCell(currCell.getX() + dir.dx, currCell.getY() + dir.dy).getCellType() == Globals.ROOM)
					{
						roomCount++;
					}
					if(!outOfBounds(currCell.getX() + dir.dx, currCell.getY() + dir.dy) && getCell(currCell.getX() + dir.dx, currCell.getY() + dir.dy).getCellType() == Globals.HALLWAY)
					{
						hallCount++;
					}
					if(!outOfBounds(currCell.getX() + dir.dx, currCell.getY() + dir.dy) && getCell(currCell.getX() + dir.dx, currCell.getY() + dir.dy).getCellType() == Globals.WALL)
					{
						wallCount++;
					}
				}

				if(roomCount == 1 && (hallCount == 3 || hallCount == 2))
				{
					fitness += 20;
				}
				else if(hallCount == 4)
				{
					fitness += 40;
				}
				else if(wallCount == 4 || wallCount == 3)
				{
					fitness -= 50;
				}

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