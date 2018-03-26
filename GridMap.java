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

public class GridMap
{

	private Cell allTiles[][];
	private int gridWidth;
	private int gridHeight;
	private Random rand;
	private Direction lastDir;
	private double fitness;
	private Vector<Cell> doors;
	private Vector<Cell> walls;
	private Vector<Hallway> hallways;
	private Vector<Room> rooms;
	private int numCellTypes;
	private String name;


	// Constructor for first generation maps
	public GridMap(int width, int height)
	{
		gridWidth = width;
		gridHeight = height;
		allTiles = new Cell[gridWidth][gridHeight];
		rand = new Random();

		hallways = new Vector<Hallway>();
		doors = new Vector<Cell>();
		walls = new Vector<Cell>();
		rooms = new Vector<Room>();
		Globals g = new Globals();
		numCellTypes = g.getNumTypes();

		// Create new Cells in allTiles
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				// Create a new cell at the new position
				if(i % 2 == 1 || j % 2 == 1)
				{
					allTiles[i][j] = new Cell(i, j, true);
				}
				else
				{
					allTiles[i][j] = new Cell(i, j, false);	
				}
				
			}
		}
		
	}

	public void initialize()
	{
		
		
		
		lastDir = null;

		// Precedurally generate rooms, maze, and doors
		designateRooms();
	    expandMaze();
	    designateDoors();

	    // vectors for evaluation
	    fillVectors();
	    

	   
		

		
	}

	

	// -- GETTERS AND SETTERS
	public Random getRand()
	{
		return rand;
	}
	protected Cell getCell(int i, int j)
	{

		return allTiles[i][j];
	}
	public void setCell(int i, int j, Cell c)
	{
		allTiles[i][j] = c;
	}
	protected int getNumCellTypes()
	{
		return numCellTypes;
	}

	public int getWidth()
	{
		return gridWidth;
	}
	public int getHeight()
	{
		return gridHeight;
	}

	public String getName()
	{
		return name;
	}
	public double getFitness()
	{
		return fitness;
	}
	protected void setFitness(double fit)
	{
		fitness = fit;
	}
	protected Vector<Hallway> getHallwaysVector()
	{
		return hallways;
	}
	protected Vector<Cell> getDoorsVector()
	{
		return doors;
	}
	protected Vector<Cell> getWallsVector()
	{
		return walls;
	}
	protected Vector<Room>getRoomsVector()
	{
		return rooms;
	}
	protected void addRoom(Room r)
	{
		rooms.add(r);
	}

	// -- GET SPECIFIC CELL OR CELL ARRAY FROM ALLTILES
	protected Cell getRandomHallway()
	{
		Cell returnCell = allTiles[rand.nextInt(gridWidth)][rand.nextInt(gridHeight)];
		while(returnCell.getCellType() != Globals.HALLWAY)
		{
			returnCell = allTiles[rand.nextInt(gridWidth)][rand.nextInt(gridHeight)];
		}

		return returnCell;
	}

	

	private Cell getNextMove(Cell current, int geneticDirectionFavor)
	{
		Cell nextMove = null;
		Direction nextDir = null;
		List<Direction> allDir = Arrays.asList(Direction.values());
		Vector<Direction> potentialDirections = new Vector<Direction>();
		
		// Determine which directions are viable moves
		for(Direction dir : allDir)
		{
			
			// If the direction points to a cell not out of bounds and a cell that is a wall
			if(!outOfBounds(current.getX() + dir.dx, current.getY() + dir.dy)
				&& !outOfBounds(current.getX() + (dir.dx * 2), current.getY() + (dir.dy * 2))
				&& (allTiles[current.getX() + (dir.dx * 2)][current.getY() + (dir.dy * 2)].getCellType() != Globals.ROOM && allTiles[current.getX() + (dir.dx * 2)][current.getY() + (dir.dy * 2)].getCellType() != Globals.HALLWAY)
				&& noRoomCollision(current.getX() + (dir.dx * 2), current.getY() + (dir.dy * 2)))
			{
				
				potentialDirections.add(dir);
			}
		}
		
		if(lastDir != null && potentialDirections.contains(lastDir) && Math.abs(rand.nextInt(100)) < geneticDirectionFavor)
		{
			// Some variable (genetic algorithm gene) that causes a favor for previous direction
			
			nextMove = allTiles[current.getX() + (lastDir.dx)][current.getY() + (lastDir.dy)];
		}
		else if(potentialDirections.size() != 0)
		{
			// Else, a random direction among the potential ones
			nextDir = potentialDirections.get(Math.floorMod(rand.nextInt(), potentialDirections.size()));
			lastDir = nextDir;

			// Set next cell to current cell + chosen direction
			nextMove = allTiles[current.getX() + (nextDir.dx)][current.getY() + (nextDir.dy)];
			
		}

		return nextMove;
	}
	private Vector<Cell> getUnvisitedCells()
	{
		Vector<Cell> temp = new Vector<Cell>();
		
		for(int i = 0; i < allTiles.length; i++)
		{
			for(int j = 0; j < allTiles.length; j++)
			{
				if(allTiles[i][j].getCellType() == Globals.BLOCKED)
				{
					temp.add(allTiles[i][j]);
				}
			}
		}

		return temp;
	}
	protected Cell getPossibleNeighborRoom(Cell cell)
	{
		// Check neighbor cells to see if any are a room
		// If they are, get their room assignemtn
		// If they are, but have no room assignment, start a new room

		int x = cell.getX();
		int y = cell.getY();
		if(!outOfBounds(x, y - 1) && allTiles[x][y - 1] != null && allTiles[x][y - 1].getCellType() == Globals.ROOM && allTiles[x][y - 1].getRoomAssignment() >= 0 && allTiles[x][y - 1].getRoomAssignment() < rooms.size())
		{
			return allTiles[x][y - 1];
		}
		else if(!outOfBounds(x + 1, y) && allTiles[x + 1][y] != null && allTiles[x + 1][y].getCellType() == Globals.ROOM && allTiles[x + 1][y].getRoomAssignment() >= 0 && allTiles[x + 1][y].getRoomAssignment() < rooms.size())
		{
			return allTiles[x + 1][y];
		}
		else if(!outOfBounds(x, y + 1) && allTiles[x][y + 1] != null && allTiles[x][y + 1].getCellType() == Globals.ROOM && allTiles[x][y+1].getRoomAssignment() >= 0 && allTiles[x][y + 1].getRoomAssignment() < rooms.size())
		{
			return allTiles[x][y + 1];
		}
		else if(!outOfBounds(x-1, y) && allTiles[x - 1][y] != null && allTiles[x - 1][y].getCellType() == Globals.ROOM && allTiles[x-1][y].getRoomAssignment() >= 0 && allTiles[x - 1][y].getRoomAssignment() < rooms.size())
		{
			return allTiles[x - 1][y];
		}
		else
		{
			return null;
		}
	}
	

	// -- FOR CREATION
	protected void fillVectors()
	{
		for(int i = 0; i < gridWidth; i++)
	    {
	    	for(int j = 0; j <gridHeight; j++)
	    	{
	    		if(allTiles[i][j].getCellType() == Globals.ROOM && allTiles[i][j].getRoomAssignment() == -1)
				{
					// Start a new room here
					
					rooms.add(new Room(rooms.size()));
					
					roomFill(allTiles[i][j].getX(), allTiles[i][j].getY());
				}
				else if(allTiles[i][j].getCellType() == Globals.HALLWAY && allTiles[i][j].getHallwayAssignment() == -1)
				{
					// Start new hallway
					hallways.add(new Hallway(hallways.size()));
					hallFill(allTiles[i][j].getX(), allTiles[i][j].getY());
					
				}
				else
				{
					assignToVector(allTiles[i][j]);	
				}
	    		
	    	}
	    }
	    //System.out.printf("Num rooms: %d num hallways: %d\n", rooms.size(), hallways.size());
	}
	
	private boolean hallFill(int locationX, int locationY)
	{
		// Reject - current cell is a wall, OOB, or already visited --> return false
		if(outOfBounds(locationX, locationY) || allTiles[locationX][locationY].getCellType() != Globals.HALLWAY || allTiles[locationX][locationY].getHallwayAssignment() > -1)
		{
			// Ran into a wall, out of bounds, or somewhere we've already been
			return false;
		}
		

		// Passed all tests so move further
		// Set this cells roomAssignment and add it to the proper room
		allTiles[locationX][locationY].setHallwayAssignment(hallways.size() - 1);
		hallways.get(hallways.size() - 1).add(allTiles[locationX][locationY]);



		boolean result;
		// Try to go up
		result = hallFill(locationX,locationY - 1);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go right
		result = hallFill(locationX + 1,locationY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go down
		result = hallFill(locationX,locationY + 1);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go left
		result = hallFill(locationX - 1,locationY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// go back
		return false;
	}
	private boolean roomFill(int locationX, int locationY)
	{

		
		// Reject - current cell is a wall, OOB, or already visited --> return false
		if(outOfBounds(locationX, locationY) || allTiles[locationX][locationY].getCellType() != Globals.ROOM|| allTiles[locationX][locationY].getRoomAssignment() > -1)
		{
			// Ran into a wall, out of bounds, or somewhere we've already been
			return false;
		}
		

		// Passed all tests so move further
		// Set this cells roomAssignment and add it to the proper room
		allTiles[locationX][locationY].setRoomAssignment(rooms.size() - 1);
		rooms.get(rooms.size() - 1).add(allTiles[locationX][locationY]);



		boolean result;
		// Try to go up
		result = roomFill(locationX,locationY - 1);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go right
		result = roomFill(locationX + 1,locationY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go down
		result = roomFill(locationX,locationY + 1);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go left
		result = roomFill(locationX - 1,locationY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// go back
		return false;
	}
	protected void assignToVector(Cell cellToAssign)
	{
		//  hallways, door, wall, room, trap, etc get assigned to proper vector

		
		if(cellToAssign.getCellType() == Globals.DOOR)
		{
			doors.add(cellToAssign);
		}
		else if(cellToAssign.getCellType() == Globals.WALL)
		{
			walls.add(cellToAssign);
		}
		else if(cellToAssign.getCellType() == Globals.BLOCKED)
		{
			
		}
		else
		{
			
			// Likely a room. the rooms vector is handled in fillRoomsVector()
		}
	}
	
	private void designateRooms()
	{
		// TODO: add room cells to a ROOM object for later comparison

		// Attempt to draw N rooms
		// Rooms can not overlap
		// When a room is drawn, the appropriate cells in allTiles are notified and switch types
		// A RoomBlock is also created
		
		int roomSizeWidth;
		int roomSizeHeight;

		int startWidth; 
		int startHeight;

		int geneticAttempts = 50;
		int geneticRoomMax = 12;
		int geneticRoomMin = 3;
		
		try
		{
			// Decide rooms based on tiles.
			for(int i = 0; i < geneticAttempts; i++)
			{
				
				roomSizeWidth = geneticRoomMin + 2 * (rand.nextInt(geneticRoomMax - geneticRoomMin) / 3);
				roomSizeHeight = geneticRoomMin + 2 * (rand.nextInt(geneticRoomMax - geneticRoomMin) / 3);

				// pick start origin
				startWidth = 2 * (rand.nextInt(gridWidth));
				startHeight = 2 * (rand.nextInt(gridHeight));

				// Check if any overlap with existing rooms
				// For each room in rooms, check if any overlap exists
				if(startWidth > 0 && (startWidth + roomSizeWidth + 1) <= allTiles.length
					&& startHeight > 0 && (startHeight + roomSizeHeight + 1) <= allTiles[0].length)
				{
					

			
					for(int j = 0; j < roomSizeWidth; j++)
					{
						for(int k = 0; k < roomSizeHeight; k++)
						{
							// Change cell type
							allTiles[startWidth + j][startHeight + k].changeCellType(Globals.ROOM);

						}
					}

					

				}	
			}




		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	protected void roomAssignment(Cell cell)
	{
		// Check around cell and see if there is an adjust cell has a room
		// If it does, assign cell to that room
		// If not, cell will start its own room
		Cell neighbor = getPossibleNeighborRoom(cell);
		

		
		if(neighbor != null)
		{
			//System.out.printf("Neighbor Position: (%d, %d) Myself: (%d, %d), NeighborRoomAssignment: %d \n", neighbor.getX(), neighbor.getY(), cell.getX(), cell.getY(), neighbor.getRoomAssignment());
			//System.out.printf("Assigning to room: %d \n",neighbor.getRoomAssignment());
			cell.setRoomAssignment(neighbor.getRoomAssignment());
			rooms.get(neighbor.getRoomAssignment()).add(cell);
		}
		else
		{
			// Start new room assignment
			rooms.add(new Room(rooms.size()));
			cell.setRoomAssignment(rooms.size() - 1);
			rooms.get(cell.getRoomAssignment()).add(cell);
			//System.out.printf("Starting room %d, roomsSize: %d\n", cell.getRoomAssignment(), rooms.size());

		}
	}

	private void designateDoors()
	{
		
		// Randomly place doors through out the maze
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				if(allTiles[i][j].getCellType() == Globals.WALL)
				{
					// % chance to turn cell into door?
					int percentChance = rand.nextInt(15);
			
					if(rand.nextInt(100) < percentChance)
					{
						allTiles[i][j].changeCellType(Globals.DOOR);
					}
				}
			}
		}


	}
	
	private void expandMaze()
	{
		// recursive backtracking algorithm
		Vector<Cell> unvisitedCells = getUnvisitedCells();
		Cell current = unvisitedCells.get(Math.floorMod(rand.nextInt(), unvisitedCells.size()));
		Cell nextCell = null;
		Cell finish = null;
		Vector<Cell> cellStack = new Vector<Cell>();
		int geneticDirectionFavor = rand.nextInt(25);

		cellStack.add(current);

		while(!unvisitedCells.isEmpty())
		{
			
			allTiles[current.getX()][current.getY()].changeCellType(Globals.HALLWAY);
			
			// Select an unvisited cell(defined by WALL)
			nextCell = getNextMove(current, geneticDirectionFavor);

			if(nextCell != null)
			{
				// Remove wall between current cell and next cell
				
				allTiles[nextCell.getX()][nextCell.getY()].changeCellType(Globals.HALLWAY);
				current = allTiles[current.getX() + ((nextCell.getX() - current.getX()) * 2)][current.getY() + ((nextCell.getY() - current.getY()) * 2)];
				cellStack.add(current);
				unvisitedCells.remove(current);
				
			}
			else if(!cellStack.isEmpty())
			{
				// If no where to move, recurse
				current = cellStack.remove(cellStack.size() - 1);
			}
			else
			{
				// If no where to move and can't recurse, choose a new unvisited cell
				current = unvisitedCells.remove(rand.nextInt(unvisitedCells.size()));
				while(!noRoomCollision(current.getX(), current.getY()) && !unvisitedCells.isEmpty())
				{
					
					current = unvisitedCells.remove(rand.nextInt(unvisitedCells.size()));
				}
				cellStack.add(current);
				
			}
		}

		
	}
	
	// -- FOR ERROR CHECKING
	private boolean noRoomCollision(int posX, int posY)
	{
		// Returns true if the position wouldn't collide with any rooms
		boolean retVal = true;

		if(allTiles[posX][posY].getCellType() == Globals.ROOM)
		{
			retVal = false;
		}
		return retVal;
	}
	protected boolean outOfBounds(int posX, int posY)
	{
		boolean retVal = false;
		// Returns true if the X or Y fall out of bounds
		if(posX < 0 || posX >= gridWidth || posY < 0 || posY >= gridHeight)
		{
			
			retVal = true;
		}

		return retVal;
	}


	// -- FITNESS FUNCTION
		
	protected Cell mutate(Cell cellForMutation, double rate)
	{
		double chance = rand.nextDouble();

		if(chance < rate)
		{
			// Mutate
			cellForMutation.changeCellType(rand.nextInt(numCellTypes));
		} 
		

		return cellForMutation;
	}
	protected Cell forceMutate(Cell cellForMutation)
	{
		
			// Mutate
		cellForMutation.changeCellType(rand.nextInt(numCellTypes));

		
		
		

		return cellForMutation;
	}



	public double evaluateFitness()
	{
		
		
		
		fitness = 0;
		Cell startCell = getRandomHallway();
		Cell endCell = getRandomHallway();

		// Hallways are ok
		// Doors shouldn't exist in a cave
		// Rooms should be large
		if(solveMaze(startCell.getX(), startCell.getY(), endCell.getX(), endCell.getY()))
		{
			// Possible to reach the exit of the maze from the start - increase fitness drastically
			fitness += 10;
		}

		fitness += evaluateStartExitDistance(startCell, endCell);

		// Should be a lot of rooms
		// Rooms should be large and non-uniform
		fitness += evaluateRooms();
		fitness += evaluateHallways();
		fitness += evaluateDoors();

		if(fitness <= 0)
		{
			fitness = 1;
		}

		return fitness;
	}
	private double evaluateWalls()
	{
		// Walls can be short clumps inside rooms. Act as pillars in the cave
		// Walls should be fairly abundant

		
		double fitness = 0;

		if(walls.size() < 50)
		{
			fitness -= walls.size();
		}
		else if(walls.size() > 50 && walls.size() < 100)
		{
			fitness += 20;
		}
		else
		{
			fitness -= walls.size();
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


		return fitness;


	}
	private double evaluateHallways()
	{
		double fitness = 0;
		
		// Caverns should rarely ever have hallways
		if(hallways.size() == 0 || hallways.size() < 25)
		{
			fitness += 20;
		}
		else if(hallways.size() > 25 && hallways.size() < 100)
		{
			fitness += 10;
		}
		else
		{
			fitness -= hallways.size();
		}

	

		return fitness;
	}
	private double evaluateRooms()
	{
		double fitness = 0;
		int halfMap = gridHeight * gridWidth / 2;
		int numRoomCells = 0;
		int numConnectingRooms;

		// Should contain a lot of room cells
		// Rooms should be touching

		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				if(allTiles[i][j].getCellType() == Globals.ROOM)
				{
					numRoomCells++;
				}
			}
		}

		int difference = (gridHeight * gridWidth) - numRoomCells;
		if(difference > halfMap)
		{
			fitness += 30;
		}



		return fitness;
	}
	private double evaluateDoors()
	{
		// Doors shouldn't exist in a cave
		// The less doors, the better fitness
		
		double fitness = 0;
		
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

	// -- FITNESS FUNCTION HELPER FUNCTIONS
	protected int wallInRoom(Cell wall)
	{
		int x = wall.getX();
		int y = wall.getY();
		int surroundRoomCount = 0;
		int surroundWallCount = 0;

		if(!outOfBounds(x, y - 1) && allTiles[x][y - 1].getCellType() == Globals.ROOM)
		{
			surroundRoomCount++;
		}

		if(!outOfBounds(x + 1, y) && allTiles[x + 1][y] != null && allTiles[x + 1][y].getCellType() == Globals.ROOM)
		{
			surroundRoomCount++;
		}

		if(!outOfBounds(x, y + 1) && allTiles[x][y + 1] != null && allTiles[x][y+1].getCellType() == Globals.ROOM)
		{
			surroundRoomCount++;
		}
		
		if(!outOfBounds(x-1, y) && allTiles[x-1][y].getCellType() == Globals.ROOM)
		{
			surroundRoomCount++;
		}
		

		if(surroundRoomCount == 4)
		{
			// return value to signify the wall is surrounded by 4 rooms
			return 0;
		}

		if(!outOfBounds(x, y - 1) && allTiles[x][y - 1].getCellType() == Globals.WALL)
		{
			surroundWallCount++;
		}

		if(!outOfBounds(x + 1, y) && allTiles[x + 1][y] != null && allTiles[x + 1][y].getCellType() == Globals.WALL)
		{
			surroundWallCount++;
		}

		if(!outOfBounds(x, y + 1) && allTiles[x][y + 1] != null && allTiles[x][y+1].getCellType() == Globals.WALL)
		{
			surroundWallCount++;
		}
		
		if(!outOfBounds(x-1, y) && allTiles[x-1][y].getCellType() == Globals.WALL)
		{
			surroundWallCount++;
		}

		if(surroundWallCount == 1 && surroundRoomCount == 3)
		{
			// return value to signify wall is in a room and connected to another wall
			return 1;
		}
		
		else
		{
			// dont want this wall here
			return 2;
		}
	}

	// All types of maps will use mutate, so keep in in base class

	// NOT USED IN CAVERN GENERATION
	private boolean evaluateProperDoorPlacement(Cell door)
	{
		int leftOfDoor = door.getX() - 1;
		int rightOfDoor = door.getX() + 1;
		int belowDoor = door.getY() + 1;
		int aboveDoor = door.getY() -1;
		if((allTiles[leftOfDoor][door.getY()].getCellType() == Globals.HALLWAY || allTiles[leftOfDoor][door.getY()].getCellType() == Globals.ROOM) && allTiles[rightOfDoor][door.getY()].getCellType() == Globals.ROOM)
		{
			return true;
		}
		else if((allTiles[door.getX()][aboveDoor].getCellType() == Globals.HALLWAY || allTiles[door.getX()][aboveDoor].getCellType() == Globals.ROOM) && allTiles[door.getX()][belowDoor].getCellType() == Globals.ROOM)
		{
			return true;
		}
		else if(allTiles[rightOfDoor][door.getY()].getCellType() == Globals.HALLWAY && allTiles[leftOfDoor][door.getY()].getCellType() == Globals.ROOM)
		{
			return true;
		}
		else if(allTiles[door.getX()][belowDoor].getCellType() == Globals.HALLWAY && allTiles[door.getX()][aboveDoor].getCellType() == Globals.ROOM)
		{
			return true;
		}

		return false;
	}
	protected boolean solveMaze(int startX, int startY, int endX, int endY)
	{
		// recursive backtracking to find a possible solution to the maze

		return exploreMaze(startX, startY, endX, endY);
		
	}
	private boolean exploreMaze(int currX, int currY, int endX, int endY)
	{
		
		// Accept - current cell is the exit --> return true
		if(currX == endX && currY == endY)
		{
			// Found the exit
			return true;
		}
		// Reject - current cell is a wall, OOB, or already visited --> return false
		else if(outOfBounds(currX, currY) || (allTiles[currX][currY].getCellType() != Globals.HALLWAY && allTiles[currX][currY].getCellType() != Globals.ROOM ) || allTiles[currX][currY].getVisisted())
		{
			// Ran into a wall, out of bounds, or somewhere we've already been
			return false;
		}
		

		// Passed all tests so move further
		// Set this cell as visited so we don't return
		allTiles[currX][currY].setVisited(true);
		boolean result;
		// Try to go down
		result = exploreMaze(currX, currY + 1, endX, endY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go right
		result = exploreMaze(currX + 1, currY, endX, endY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go up
		result = exploreMaze(currX, currY - 1, endX, endY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go left
		result = exploreMaze(currX - 1, currY, endX, endY);
		if(result)
		{
			//allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// If can't move anywhere, this cell is not part of a solution, time to recurse
		//allTiles[currX][currY].changeCellType(Globals.TRIED_PATH);

		// go back
		return false;
	}
	protected double evaluateStartExitDistance(Cell start, Cell end)
	{
		double fitnessAdjustment = 0;
		int xDiff = Math.abs(start.getX() - end.getX());
		int yDiff = Math.abs(start.getY() - end.getY());

		if(xDiff < 3 && yDiff < 3)
		{
			// Start and Exit are too close
			// Severely gimp the fitness score
			fitnessAdjustment = -1;
		}
		else if(xDiff < 10 && yDiff < 10)
		{
			// Start and Exit are a bit too close
			// Reduce the fitness score so it would be very rare for this map to mate
			fitnessAdjustment = 1.3;
		}
		else if(xDiff < 25 && yDiff < 25)
		{
			// Start and Exit are closer to a good distance
			// Increase fitness score, but only slightly
			fitnessAdjustment = 1.5;
		}
		else if(xDiff < 40 && yDiff < 40)
		{
			// Good
			// Increase fitness score
			fitnessAdjustment = 1.8;
		}
		else
		{
			// Really good
			// Generously increase fitness score
			fitnessAdjustment = 2.1;
		}


		return fitnessAdjustment;

	}

	// -- DRAWING

	public void Draw(String name)
	{
		  // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
	      // into integer pixels
		try
		{
			int width = 600, height = 600;
		    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		    this.name = name;
		    Graphics2D ig2 = bi.createGraphics();

		    ig2.setColor(Color.BLACK);
		    ig2.fillRect(0, 0, width, height);

		    //drawCells(ig2, width, height, gridWidth, gridHeight);
		    drawComplete(ig2, width, height, gridWidth, gridHeight);

		    drawGrid(ig2, width / gridWidth, height / gridHeight);

		    ImageIO.write(bi, "PNG", new File(".\\Maps\\" + name + ".PNG"));

		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		  
	      
	}
	
	private void drawGrid(Graphics2D pencil, int width, int height)
	{
		pencil.setColor(Color.GRAY);
	    
	    for(int i = 0; i < gridWidth; i++)
	    {
	    	pencil.drawLine(i * width, 0, i * width, height * gridHeight);
	    }
	    for(int i = 0; i < gridHeight; i++)
		{
			pencil.drawLine(0, i * height, height * gridWidth, i * height);	
		}
	}

	private void drawComplete(Graphics2D pencil, int imgW, int imgH, int gridW, int gridH)
	{
		pencil.setColor(Color.WHITE);
		for(int i = 0; i < gridW; i++)
		{
			for(int j = 0; j < gridH; j++)
			{
				 if(allTiles[i][j].getCellType() == Globals.WALL || allTiles[i][j].getCellType() == Globals.BLOCKED)
				{
					pencil.setColor(Color.BLACK);
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				else
				{
					pencil.setColor(new Color(105, 105, 105));
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
			}
		}
	}

	
	private void drawCells(Graphics2D pencil, int imgW, int imgH, int gridW, int gridH)
	{
		// Draw Rooms
		
		// Multiply X & Y by img width/height and grid width/height in order to fit the pictures resolution
		
	int testColor = rand.nextInt(255);
		
		// Draw Hallways
		for(int i = 0; i < gridW; i++)
		{
			for(int j = 0; j < gridH; j++)
			{
				if(allTiles[i][j].getCellType() == Globals.HALLWAY)
				{
					pencil.setColor(Color.WHITE);
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				else if(allTiles[i][j].getCellType() == Globals.WALL || allTiles[i][j].getCellType() == Globals.BLOCKED)
				{
					pencil.setColor(Color.BLACK);
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				else if(allTiles[i][j].getCellType() == Globals.ROOM)
				{
					try
					{
						pencil.setColor(Color.BLUE);
						pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						System.out.printf("%d\n", allTiles[i][j].getRoomAssignment());
					}
					
				}
				else if( allTiles[i][j].getCellType() == Globals.DOOR)
				{
					//pencil.setColor(Color.WHITE);
					pencil.setColor(Color.GRAY);
					//pencil.setColor(new Color(40, 27, 132));
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				else if(allTiles[i][j].getCellType() == Globals.TEST_MUTATION)
				{
					pencil.setColor(Color.PINK);
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				
				else
				{
					// cell is either blocked or has some other type(shouldnt happen)
					pencil.setColor(Color.RED);
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}

			
			}
		}
		for(Room r : rooms)
		{
			// testing room assignment
			for(Cell c: r.getCells())
			{
				if(c.getCellType() == Globals.ROOM)
				{
					pencil.setColor(new Color(0,255,0));
					pencil.fillRect(c.getX() * (imgW / gridWidth), c.getY() * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));	
				}
				

			}

		}


	}



	
}
