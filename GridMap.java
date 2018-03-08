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
	private Vector<RoomBlock> rooms;
	private Cell allTiles[][];
	private int gridWidth;
	private int gridHeight;
	private Random rand;
	private int geneticAttempts;
	private int geneticRoomMin;
	private int geneticRoomMax;
	private int geneticDirectionFavor;
	private Direction lastDir;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private double fitness;
	private String name;


	public GridMap(int width, int height)
	{
		gridWidth = width;
		gridHeight = height;
		allTiles = new Cell[gridWidth][gridHeight];
		rand = new Random();
		rooms = new Vector<RoomBlock>(0, 1);

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

		geneticAttempts = 1000;
		geneticRoomMax = 24;
		geneticRoomMin = 3;
		geneticDirectionFavor = rand.nextInt(25);
		lastDir = null;

		
	}
	public GridMap(GridMap parentA, GridMap parentB)
	{
		// This constructor will be used for crossover
	}

	
	public void initialize()
	{

	      designateRooms();
	      expandMaze();
	      
	      //ImageIO.write(bi, "JPEG", new File(".\\test.JPG"));
	      
		      
    	
	}
	public void draw(String name)
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

		    drawCells(ig2, width, height, gridWidth, gridHeight);

		    drawGrid(ig2, width / gridWidth, height / gridHeight);

		    ImageIO.write(bi, "PNG", new File(".\\Maps\\" + name + ".PNG"));

		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		  
	      
	}
	public String getName()
	{
		return name;
	}
	private Cell getRandomHallway()
	{
		Cell returnCell = allTiles[rand.nextInt(gridWidth)][rand.nextInt(gridHeight)];
		while(returnCell.getCellType() != Globals.HALLWAY)
		{
			returnCell = allTiles[rand.nextInt(gridWidth)][rand.nextInt(gridHeight)];
		}

		return returnCell;
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

	private void designateRooms()
	{
		// Attempt to draw N rooms
		// Rooms can not overlap
		// When a room is drawn, the appropriate cells in allTiles are notified and switch types
		// A RoomBlock is also created
		
		int roomSizeWidth;
		int roomSizeHeight;

		int startWidth; 
		int startHeight;
		
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
					
					boolean overlap = false;

					for(RoomBlock room : rooms)
					{
						// If the current room overlaps with any other rooms, try again
						if(room.doOverlap(startWidth, startHeight, roomSizeWidth, roomSizeHeight))
						{
							overlap = true;
							break;
						}
						
					}

					// If there is no overlap with other rooms, create the new room
					if(!overlap)
					{
						for(int j = 0; j < roomSizeWidth; j++)
						{
							for(int k = 0; k < roomSizeHeight; k++)
							{
								// Change cell type
								allTiles[startWidth + j][startHeight + k].changeCellType(Globals.ROOM);

							}
						}

						rooms.add(new RoomBlock(startWidth,  startWidth + roomSizeWidth,startHeight, startHeight + roomSizeHeight));
					}


				}	
			}		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private Vector<Cell> designatePotentialDoors()
	{
		Vector<Cell> doors = new Vector<Cell>();
		// Randomly place doors through out the maze
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				if(allTiles[i][j].getCellType() == Globals.WALL)
				{
					// % chance to turn cell into door?
					int percentChance = rand.nextInt(50);
					//System.out.printf("%d \n", percentChance);
					if(rand.nextInt(100) < percentChance)
					{
						allTiles[i][j].changeCellType(Globals.DOOR);
						doors.add(allTiles[i][j]);
					}
				}
			}
		}

		return doors;

	}
	
	private void expandMaze()
	{
		// recursive backtracking algorithm
		Vector<Cell> unvisitedCells = getUnvisitedCells();
		Cell current = unvisitedCells.get(Math.floorMod(rand.nextInt(), unvisitedCells.size()));
		Cell nextCell = null;
		Cell finish = null;
		Vector<Cell> cellStack = new Vector<Cell>();

		cellStack.add(current);

		while(!unvisitedCells.isEmpty())
		{
			
			allTiles[current.getX()][current.getY()].changeCellType(Globals.HALLWAY);
			
			// Select an unvisited cell(defined by WALL)
			nextCell = getNextMove(current);

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

	private Cell getNextMove(Cell current)
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
		// step through all cells and return a vector of cells that are HALLWAY and arent at 0,0(starting point)
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

	private boolean noRoomCollision(int posX, int posY)
	{
		// Returns true if the position wouldn't collide with any rooms
		boolean retVal = true;

		for(RoomBlock room : rooms)
		{
			// For each room, check for collision
			if(room.checkIfCollision(posX, posY))
			{
				retVal = false;
				break;
			}
		}
		return retVal;
	}
	private boolean outOfBounds(int posX, int posY)
	{
		boolean retVal = false;
		// Returns true if the X or Y fall out of bounds
		if(posX < 0 || posX >= gridWidth || posY < 0 || posY >= gridHeight)
		{
			
			retVal = true;
		}

		return retVal;
	}
		
	public double evaluateFitness()
	{
		fitness = 0;
		Cell startCell = getRandomHallway();
		Cell endCell = getRandomHallway();

		startX = startCell.getX();
		startY = startCell.getY();
		endX = endCell.getX();
		endY = endCell.getY();
		
		// First iteration
			// Evalute placement of start and end cells
			// Is the maze a complete maze
		// Second iteration
			// Place doors through the maze
				// Doors should connect HALLWAY to ROOM or ROOM to ROOM(Secret room. Rare?)
				// update Rooms to keep track of how many doors they have
			// Update maze pathing to be able to move through rooms (Might make it almost impossible to fail?)

		if(solveMaze())
		{
			// Possible to reach the exit of the maze from the start - increase fitness drastically
			fitness += 10;
		}
		fitness *= evaluateStartExitDistance(startCell, endCell);

		Vector<Cell> allDoors = designatePotentialDoors();
		fitness += evaluateDoorPlacement(allDoors);
		fitness += evaluateRooms();

		if(fitness <= 0)
		{
			fitness = 1;
		}

		return fitness;
	}
	private int evaluateRooms()
	{
		// Iterate through all rooms
		// Check how many doors room has on it's border
			// Resonable amount for it's size?

		return 0;
	}
	private int evaluateDoorPlacement(Vector<Cell> doors)
	{
		// For each door check if it connects HALLWAY to ROOM or ROOM to ROOM
			// decrease fitness for each door that exists that doesnt do either of those
		int fitness = 0;
		for(Cell door : doors)
		{
			if(!outOfBounds(door.getX() - 1, door.getY() - 1) && !outOfBounds(door.getX() + 1, door.getY() + 1))
			{
				if(!properDoorPlacement(door))
				{
					fitness -= 2;	
				}
				else
				{
					fitness += 2;
				}
				
			}
		}
		return fitness;
	}
	private boolean properDoorPlacement(Cell door)
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
	private boolean solveMaze()
	{
		// recursive backtracking to find a possible solution to the maze

		return exploreMaze(startX, startY);
		
	}
	private boolean exploreMaze(int currX, int currY)
	{
		
		// Accept - current cell is the exit --> return true
		if(currX == endX && currY == endY)
		{
			// Found the exit
			return true;
		}
		// Reject - current cell is a wall, OOB, or already visited --> return false
		else if(outOfBounds(currX, currY) || (allTiles[currX][currY].getCellType() != Globals.HALLWAY && allTiles[currX][currY].getCellType() != Globals.SOLUTION_PATH) || allTiles[currX][currY].getVisisted())
		{
			// Ran into a wall, out of bounds, or somewhere we've already been
			return false;
		}
		

		// Passed all tests so move further
		// Set this cell as visited so we don't return
		allTiles[currX][currY].setVisited(true);
		boolean result;
		// Try to go down
		result = exploreMaze(currX, currY + 1);
		if(result)
		{
			allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go right
		result = exploreMaze(currX + 1, currY);
		if(result)
		{
			allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go up
		result = exploreMaze(currX, currY - 1);
		if(result)
		{
			allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// Try to go left
		result = exploreMaze(currX - 1, currY);
		if(result)
		{
			allTiles[currX][currY].changeCellType(Globals.SOLUTION_PATH);
			return true;
		}

		// If can't move anywhere, this cell is not part of a solution, time to recurse
		//allTiles[currX][currY].changeCellType(Globals.TRIED_PATH);

		// go back
		return false;
	}
	private double evaluateStartExitDistance(Cell start, Cell end)
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
	public double getFitness()
	{
		return fitness;
	}
	private void drawCells(Graphics2D pencil, int imgW, int imgH, int gridW, int gridH)
	{
		// Draw Rooms
		
		// Multiply X & Y by img width/height and grid width/height in order to fit the pictures resolution
		pencil.setColor(new Color(40, 27, 132));
		for(RoomBlock room: rooms)
		{
			pencil.fillRect(room.getLowX() * (imgW / gridWidth) + 1, room.getLowY() * (imgH / gridHeight) + 1, (room.getHighX() - room.getLowX()) * (imgW / gridWidth) - 1, (room.getHighY() - room.getLowY()) * (imgH / gridHeight) - 1);
			
		}
		
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
				else if(allTiles[i][j].getCellType() == Globals.WALL)
				{
					pencil.setColor(Color.BLACK);
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				else if( allTiles[i][j].getCellType() == Globals.DOOR)
				{
					pencil.setColor(new Color(244, 199, 100));
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				else if(allTiles[i][j].getCellType() == Globals.SOLUTION_PATH)
				{
					pencil.setColor(new Color(0, 230, 0));
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
				else if (allTiles[i][j].getCellType() == Globals.TRIED_PATH)
				{
					pencil.setColor(new Color(0, 0, 230));
					pencil.fillRect(i * (imgW / gridWidth), j * (imgH / gridHeight), (imgW / gridW), (imgH / gridH));
				}
			}
		}


	}



	
}
