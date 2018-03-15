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
		// Iterate over each row and choose a random cross over point

		this.mutationRate = mutationRate;
		allDir = Arrays.asList(Direction.values());
		int crossoverPoint;
		int crossoverParentSelection;
		GridMap leftParent;
		GridMap rightParent;

		int rowOrColCrossOver = super.getRand().nextInt(2);

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
				
				if(rowOrColCrossOver == 0)
				{
					// Column crossover
					if(j <= crossoverPoint)
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
					if(j <= crossoverPoint)
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

		}

		fillRoomsVector();


	}


	@Override
	public void initialize()
	{
		// Lets just make sure if this gets called, it doesnt erase the map
		System.out.printf("Initialize was attempted to be called from a cavern map \n");
	}

	@Override
	public int evaluateFitness()
	{
		

		int myFit = 0;
		Cell startCell = super.getRandomHallway();
		Cell endCell = super.getRandomHallway();

		// Hallways are ok
		// Doors shouldn't exist in a cave
		// Rooms should be large
		if(solveMaze(startCell.getX(), startCell.getY(), endCell.getX(), endCell.getY()))
		{
			// Possible to reach the exit of the maze from the start - increase fitness drastically
			myFit += 10;
		}

		myFit *= evaluateStartExitDistance(startCell, endCell);

		// Should be a lot of rooms
		// Rooms should be large and non-uniform
		myFit += evaluateRooms();
		myFit += evaluateHallways();
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
	// There should only be a VERY small amount of hallways
	// Ideally, they will connect different rooms
	
	private int evaluateHallways()
	{
		int fitness = 0;
		Vector<Cell> hallways = getHallwaysVector();
		
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

		// If there are hallways, they still shouldnt touch rooms
		for(int i = 0; i < hallways.size(); i++)
		{
			// If hallways direct neighbor is room, decrease fitness
			if(getPossibleNeighborRoom(hallways.get(i)) != null)
			{
				fitness -= 10;
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
		int halfMap = getHeight() * getWidth() / 2;
		int numRoomCells = 0;
		int numConnectingRooms;

		// Should contain a lot of room cells
		// Rooms should be touching

		/*for(int i = 0; i < getWidth(); i++)
		{
			for(int j = 0; j < getHeight(); j++)
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
		}*/



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