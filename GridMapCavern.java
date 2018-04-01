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
		System.out.printf("Next Map\n");
		
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
		
			/*if(getRand().nextDouble() < mutationRate)
			{
				parentHall = mutateHall(parentHall);
			}*/

			// allTiles cells are all null at this point
			// Loop through parents room and assign corresponding cells to ROOM
			for(Cell c : parentHall.getCells())
			{
				// Mutation occurs here
				if(getCell(c.getX(), c.getY()).getCellType() == Globals.WALL || getCell(c.getX(), c.getY()).getCellType() == Globals.BLOCKED)
				{
					getCell(c.getX(), c.getY()).changeCellType(c.getCellType());	
				}
				
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
			/*if(getRand().nextDouble() < mutationRate && parentRoom.getCells().size() > 0)
			{
				parentRoom = mutateRoom(parentRoom);
			}*/
		

			// allTiles cells are all null at this point
			// Loop through parents room and assign corresponding cells to ROOM
			for(Cell c : parentRoom.getCells())
			{
				// On super constructor all cells are basic WALL or BLOCKED
				// Mutation occurs here
				if(getCell(c.getX(), c.getY()).getCellType() == Globals.WALL || getCell(c.getX(), c.getY()).getCellType() == Globals.BLOCKED || getCell(c.getX(), c.getY()).getCellType() == Globals.HALLWAY )
				{
					getCell(c.getX(), c.getY()).changeCellType(c.getCellType());	
				}
				
			}
		}

		fillVectors();
		mutate(mutationRate);

		


	}


	@Override
	public void initialize()
	{
		// Lets just make sure if this gets called, it doesnt erase the map
		System.out.printf("Initialize was attempted to be called from a cavern map \n");
	}

	public void mutate(double mutateRate)
	{
		Vector<Room> rooms = getRoomsVector();
		Vector<Hallway> hallways = getHallwaysVector();
		
		// Hallway mutation
		for(Hallway mutateHall : hallways)
		{
			if(getRand().nextDouble() < mutateRate && hallways.size() != 0)
			{
				// Choose hallway to mutate
				
				 
				int mutationSelection = getRand().nextInt(2);

				/*if(mutationSelection == 7)
				{
					// Fill Mutation
					Cell origin;
					System.out.printf("Hall Fill\n");
					// Choose a starting cell randomly in the hallway
					origin = mutateHall.getRandomCell();
					

					// Check all directions
					// If x or y + 1 == wall/blocked but x or y + 2 is a hallway, set x or y + 1 to a hallway
					for(Direction dir : allDir)
					{
						while((!outOfBounds(origin.getX() + dir.dx, origin.getY() + dir.dy) && !outOfBounds(origin.getX() + dir.dx * 2, origin.getY() + dir.dy *2)) && (getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).getCellType() == Globals.WALL || getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).getCellType() == Globals.BLOCKED) && getCell(origin.getX() + dir.dx * 2, origin.getY() + dir.dy *2).getCellType() == Globals.HALLWAY)
						{
							getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).changeCellType(Globals.HALLWAY);
							getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).setHallwayAssignment(origin.getHallwayAssignment());
							mutateHall.add(getCell(origin.getX() + dir.dx, origin.getY() + dir.dy));
							origin = getCell(origin.getX() + dir.dx * 2, origin.getY() + dir.dy * 2);
						}
					}
					
					
				}*/
				if(mutationSelection == 0)// || mutationSelection == 1 || mutationSelection == 2)
				{
					// Burst Mutation
					//System.out.printf("Hall Burst\n");

					// Choose origin cell and set all surrounding cells to hallways no matter what
					Cell origin = mutateHall.getRandomCell();
					int width = getRand().nextInt(2) - 2;
					int height = getRand().nextInt(2) - 2;
					//for(int x = 0; x < 2; x ++)
					//{
						origin = mutateHall.getRandomCell();
						for(int i = width; i < Math.abs(width); i++)
						{
							for(int j = height; j < Math.abs(height); j++)
							{
								if(!outOfBounds(origin.getX() + i, origin.getY() + j))
								{
									getCell(origin.getX() + i, origin.getY() + j).changeCellType(Globals.HALLWAY);
									getCell(origin.getX() + i, origin.getY() + j).setHallwayAssignment(origin.getHallwayAssignment());
									mutateHall.add(getCell(origin.getX() + i, origin.getY() + j));
								}
							}
						}
					//}
					
				}
				else if(mutationSelection ==3 )
				{
					// Iterate each cell in the vector
					// Chance to mutate it
					//System.out.printf("Hall Standard Mutation\n");
					ArrayList<Cell> cells = mutateHall.getCells();
					for(int i = 0; i < cells.size(); i++)
					{

						
						if(getRand().nextDouble() < 0.20)
						{

						
							//getCell(cells.get(i).getX(), cells.get(i).getY()).changeCellType(Globals.WALL);
							//getCell(cells.get(i).getX(), cells.get(i).getY()).setHallwayAssignment(-1);
							//cells.remove(cells.get(i));

							// Accounting for the fact .remove shifts to the left
							//i--;
						}
					}
				}
				else if(mutationSelection == 1)// || mutationSelection == 4 || mutationSelection == 5)
				{
					// Reroute towards random room
					//System.out.printf("Reroute Hall");

					// Choose a random room
					Room chosenRoom = rooms.get(getRand().nextInt(rooms.size()));
					Cell roomBorder = chosenRoom.chooseBorderCell();
					Cell hallStart = mutateHall.getRandomCell();

					int xDiff = roomBorder.getX() - hallStart.getX();
					int yDiff = roomBorder.getY() - hallStart.getY();

					ArrayList<Cell> hallCells = mutateHall.getCells();

					// Remove all cells from the hall except starting cell
					if(getRand().nextInt(2) == 0)
					{
						while(hallCells.size() > 0)
						{
							getCell(hallCells.get(0).getX(), hallCells.get(0).getY()).changeCellType(Globals.WALL);
							getCell(hallCells.get(0).getX(), hallCells.get(0).getY()).setHallwayAssignment(-1);
							hallCells.remove(hallCells.get(0));
						
						}
					}
					

					// Start from hallStart and create a hallway to the room
					Direction xDir;
					Direction yDir;
					if(xDiff <= 0)
					{
						xDir = Direction.WEST;
					}
					else
					{
						xDir = Direction.EAST;
					}

					if(yDiff <= 0)
					{
						yDir = Direction.NORTH;
					}
					else
					{
						yDir = Direction.SOUTH;
					}

					int crossSection = (int)Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
					int xMove = 0;
					int yMove = 0;
					Cell currCell;// = getCell(hallStart.getX() + (xDir.dx * xDiff), hallStart.getY() + (yDir.dy * yDiff));;
					for(int i = 0; i < crossSection; i++)
					{
						if(!outOfBounds(hallStart.getX() + ((xDir.dx *2) + xDiff), hallStart.getY() + ((yDir.dy * 2) + yDiff)) && getCell(hallStart.getX() + (xDir.dx + xDiff), hallStart.getY() + (yDir.dy + yDiff)).getCellType() != Globals.ROOM && getCell(hallStart.getX() + (xDir.dx + xDiff), hallStart.getY() + (yDir.dy + yDiff)).getCellType() != Globals.HALLWAY)
						{
							currCell = getCell(hallStart.getX() + (xDir.dx + xDiff), hallStart.getY() + (yDir.dy + yDiff));

							if(xDiff != 0)
							{
								getCell(currCell.getX() + xDir.dx, currCell.getY() + xDir.dy).changeCellType(Globals.HALLWAY);
								getCell(currCell.getX() + xDir.dx, currCell.getY() + xDir.dy).setHallwayAssignment(mutateHall.getNumber());
								mutateHall.add(getCell(currCell.getX() + xDir.dx, currCell.getY() + xDir.dy));
							}


							if(yDiff != 0)
							{
								getCell(currCell.getX() + yDir.dx, currCell.getY() + yDir.dy).changeCellType(Globals.HALLWAY);
								getCell(currCell.getX() + yDir.dx, currCell.getY() + yDir.dy).setHallwayAssignment(mutateHall.getNumber());
								mutateHall.add(getCell(currCell.getX() + yDir.dx, currCell.getY() + yDir.dy));
								
							}
							

							
							getCell(currCell.getX(), currCell.getY()).setHallwayAssignment(mutateHall.getNumber());
							getCell(currCell.getX(), currCell.getY()).changeCellType(Globals.HALLWAY);
							
							
							mutateHall.add(getCell(currCell.getX(), currCell.getY()));

							//currCell = getCell(hallStart.getX() + (xDir.dx * xDiff), hallStart.getY() + (yDir.dy * yDiff));
							

						}
						else if(!outOfBounds(hallStart.getX() + ((xDir.dx *2) + xDiff), hallStart.getY() + ((yDir.dy * 2) + yDiff)) && getCell(hallStart.getX() + (xDir.dx + xDiff), hallStart.getY() + (yDir.dy + yDiff)).getCellType() == Globals.HALLWAY && getCell(hallStart.getX() + (xDir.dx + xDiff), hallStart.getY() + (yDir.dy + yDiff)).getHallwayAssignment() != hallStart.getHallwayAssignment())
						{
							// If this hallway collides with another, stop all together
							break;
						}
						xDiff = xDiff != 0 ?  (xDiff < 0 ? xDiff + 1 : xDiff - 1) : xDiff;
						yDiff = yDiff != 0 ?  (yDiff < 0 ? yDiff + 1 : yDiff - 1) : yDiff;
						
						
					}

					

				}
				/*else if(mutationSelection == 4)
				{
					// Dead end removal

					// Iterate through cells in hallway
					// If cell is attached to 3 walls, remove and recurse until no more dead end
					System.out.printf("Dead End Removal\n");
					int wallCount;
					ArrayList<Cell> allCells = mutateHall.getCells();
					//Cell nextCell;
					
					for(int i = 0; i < allCells.size(); i++)
					{
						wallCount = 0;
						for(Direction dir : allDir)
						{
							if(!outOfBounds(allCells.get(i).getX() + dir.dx, allCells.get(i).getY() + dir.dy) && getCell(allCells.get(i).getX() + dir.dx, allCells.get(i).getY() + dir.dy).getCellType() == Globals.WALL)
							{
								wallCount++;
							}
							/*else if(!outOfBounds(allCells.get(i).getX() + dir.dx, allCells.get(i).getY() + dir.dy) && getCell(allCells.get(i).getX() + dir.dx, allCells.get(i).getY() + dir.dy).getCellType() == Globals.HALLWAY)
							{
								nextCell = getCell(allCells.get(i).getX() + dir.dx, allCells.get(i).getY() + dir.dy);
							}
						}

						if(wallCount == 3 || wallCount == 4)
						{
							allCells.get(i).setHallwayAssignment(-1);
							allCells.get(i).changeCellType(Globals.WALL);
							allCells.remove(allCells.get(i));
							i = -1;// Remove shifts everything to the left, so reducing variable i makes sure no cells are skipped
						}
					}
				}*/

				

			}
		}
		

		
		
		// Room mutation
		for(Room mutateRoom : rooms)
		{
			if(getRand().nextDouble() < mutateRate && rooms.size() != 0)
			{
				
				int mutationSelection = getRand().nextInt(5);

				// Room Expansion
				if(mutationSelection == 0 || mutationSelection == 1)
				{
					
					// Room expansion mutation
					//System.out.printf("Room Expansion\n");
					// Choose center cell
					Cell c = mutateRoom.chooseBorderCell();
					
					// Define the area we will be expanding
					int width = getRand().nextInt(4) + 1;
					int height = getRand().nextInt(4) + 1;

					for(int i = c.getX() - width; i < c.getX() + width; i ++)
					{
						for(int j = c.getY() - height; j < c.getY() + height; j++)
						{
							// For each cell within the expansion range
							// Check if the cell would be out of bounds, if false - change to a room;

							if(!outOfBounds(i, j))
							{
								getCell(i, j).changeCellType(Globals.ROOM);
								getCell(i, j).setRoomAssignment(c.getRoomAssignment());
								mutateRoom.add(getCell(i, j));

								
							} 

						}
					}

				}
				// Room Carving
				else if( mutationSelection == 2 || mutationSelection == 4)
				{
					
				
					// Choose random room tile and select carve zone - 
					Cell carveStart = mutateRoom.getRandomCellGeneral();

					
					int width = getRand().nextInt(3) + 1;
					int height = getRand().nextInt(3) + 1;


					//carveOrigin = mutateRoom.getRandomCellGeneral();
					for(int i = carveStart.getX() - width; i < carveStart.getX() + width; i++)
					{
						for(int j = carveStart.getY() - height; j < carveStart.getY() + height; j++)
						{
							if(!outOfBounds(i, j))
							{
								getCell(i, j).changeCellType(Globals.WALL);
								getCell(i,j).setRoomAssignment(-1);
								mutateRoom.remove(i, j);	
							}
						}
					}
				}
				// Room Shift
				else if(mutationSelection == 3)
				{
					
					// Choose a direction and move the whole room that way by X cells
					Direction randDir = Direction.randomDir();
					ArrayList<Cell> roomCells = mutateRoom.getCells();
					Cell currCell;
					int shiftBy = getRand().nextInt(5) + 1;

					for(int i = 0; i < roomCells.size(); i ++)
					{
						currCell = roomCells.get(i);
						if(!outOfBounds(currCell.getX() + (shiftBy * randDir.dx), currCell.getY() + (shiftBy * randDir.dy)) && currCell.getCellType() == Globals.ROOM)// && !roomCells.contains(getCell(currCell.getX() + (shiftBy * randDir.dx), currCell.getY() + (shiftBy * randDir.dy))))
						{
							getCell(currCell.getX() + (randDir.dx * shiftBy), currCell.getY() + (randDir.dy * shiftBy)).changeCellType(Globals.ROOM);
							getCell(currCell.getX() + (randDir.dx * shiftBy), currCell.getY() + (randDir.dy * shiftBy)).setRoomAssignment(mutateRoom.getNumber());
							getCell(currCell.getX(), currCell.getY()).changeCellType(Globals.WALL);
							getCell(currCell.getX(), currCell.getY()).setRoomAssignment(-1);
							roomCells.remove(roomCells.get(i));
							roomCells.add(i,getCell(currCell.getX() + (randDir.dx * shiftBy), currCell.getY() + (randDir.dy * shiftBy)));
							
						}
					}

					mutateRoom.removeNonRooms();
				}
			}
		}
		
		
		
		

		// By default remove hallways of size 1

		for(int i = 0; i < hallways.size(); i++)
		{
			if(hallways.get(i).size() < 3)
			{
				hallways.get(i).purge();
				hallways.remove(hallways.get(i));
				i--;
			}
		}
		for(int i = 0; i < rooms.size(); i++)
		{
			if(rooms.get(i).size() < 4)
			{
				rooms.get(i).purge();
				rooms.remove(rooms.get(i));
				i--;
			}
		}

		
		
	}
	


	@Override
	public double evaluateFitness()
	{
		

		double myFit = 0.0;
		

		// Should be a lot of rooms
		// Rooms should be large and non-uniform
		if(getRoomsVector().size() > 10 && getRoomsVector().size() < 15)
		{
			myFit += 1000;
		}
		else if(getRoomsVector().size() >= 5)
		{
			myFit += 400;
		}
		else if(getRoomsVector().size() < 5)
		{
			myFit -= 500;
		}
		myFit += evaluateHallways();
		myFit += evaluateRooms();
		
		//myFit += evaluateDoors();

		if(myFit <= 0)
		{
			myFit = 1;
		}

		setFitness(myFit);
		return myFit;

		
	}

	// Evaluate cavern's walls
	// Walls can exist in the middle of large rooms and act as a "support beam" type structure
	// Walls should line all the rooms that make up the cavern
	
	private double evaluateWalls()
	{
		// Walls can be short clumps inside rooms. Act as pillars in the cave
		// Walls should be fairly abundant

		Vector<Cell> walls = getWallsVector();
		double fitness = 0;

		if(walls.size() < 800)
		{
			fitness -= 200;
		}
		else if(walls.size() > 1000 )
		{
			fitness += 900;
		}
		else if(walls.size() > 2000)
		{
			fitness += 500;
		}
		else
		{
			fitness += 100;
		}

		// If a wall is surrounded by rooms or all rooms and 1 other wall, thats good
		for(Cell wall : walls)
		{
			// 3 possibilities
			// surrounded by rooms / surrounded by rooms + 1 or 2 walls / not touching rooms
			switch(wallInRoom(wall))
			{
				case 0: fitness += 30;
						break;

				case 1: fitness += 30;
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
	
	
	private double evaluateHallways()
	{
		double fitness = 0;
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
		Cell roomOneCell;
		Cell roomTwoCell;
		int diffX;
		int diffY;

		int roomCount;
		int hallCount;
		int wallCount;

		int firstRoomNum;
		int secondRoomNum;
		int lowestX; 
		int highestX;
		int lowestY;
		int highestY;
		
		if(hallways.size() <= 1)
		{
			fitness -= 1000;
		}
		else if(hallways.size() == rooms.size())
		{
			fitness += 800;
		}
		else if(hallways.size() - rooms.size() > 0 && hallways.size() - rooms.size() < 2)
		{
			fitness += 400;
		}


		for(Hallway hall: hallways)
		{
			firstRoomNum = -1;
			secondRoomNum = -1;
			lowestX = 100; 
			highestX = 0;
			lowestY = 100;
			highestY = 0;

			for(Cell currCell : hall.getCells())
			{
				
				roomCount = 0;
				hallCount = 0;
				wallCount = 0;

				// Determine if current cell falls between 2 rooms
				// The more cells that are between 2 rooms, the higher the fitness
				if(rooms.size() > 0)
				{
					Room r = rooms.get(getRand().nextInt(rooms.size()));
					
					roomOneCell = rooms.get(getRand().nextInt(rooms.size())).getRandomCellGeneral();
					roomTwoCell = rooms.get(getRand().nextInt(rooms.size())).getRandomCellGeneral();

					while(roomOneCell == roomTwoCell)
					{
						roomTwoCell = rooms.get(getRand().nextInt(rooms.size())).getRandomCellGeneral();
					}
					
					// Determine if this cell is between two rooms
					if((Math.abs(roomOneCell.getX() - currCell.getX()) < 3 && Math.abs(roomTwoCell.getX() - currCell.getX()) < 3) && (Math.abs(roomOneCell.getY() - currCell.getY()) < 3 && Math.abs(roomTwoCell.getY() - currCell.getY()) < 3))
					{
						// Current cell is roughly between the two rooms
						fitness += 20;
					}		
				}
				else
				{
					// this map doesnt have any rooms somehow, destroy fitness
					fitness -= 1000000;
				}

				// Check if the hallway connects 2 rooms together
				// Check each cell, if it touches a room, get that room number, then check other cells and see if they touch a room with a different room number
				for(Direction dir : allDir)
				{
					if(!outOfBounds(currCell.getX() + dir.dx, currCell.getY() + dir.dy) && getCell(currCell.getX() + dir.dx, currCell.getY() + dir.dy).getCellType() == Globals.ROOM && firstRoomNum == -1)
					{
						firstRoomNum = getCell(currCell.getX() + dir.dx, currCell.getY() + dir.dy).getRoomAssignment();
					}
					else if(!outOfBounds(currCell.getX() + dir.dx, currCell.getY() + dir.dy) && getCell(currCell.getX() + dir.dx, currCell.getY() + dir.dy).getCellType() == Globals.ROOM && firstRoomNum != -1)
					{
						secondRoomNum = getCell(currCell.getX() + dir.dx, currCell.getY() + dir.dy).getRoomAssignment();
						break;
					}
				}

				// Check if hallway isn't in the shape of an actual hallway
				// If the hallways lowest point (highest Y) and highest point (lowest Y) aren't different enough AND if the highest X and lowest X aren't different enough
				if(currCell.getX() < lowestX)
					lowestX = currCell.getX();
				else if(currCell.getX() > highestX)
					highestX = currCell.getX();

				if(currCell.getY() < lowestY)
					lowestY = currCell.getY();
				else if(currCell.getY() > highestY)
					highestY = currCell.getY();

				

								
					
				

				// Check if currCell is a room border
				// Scan surrounding cells to see if theyre also a hallway

				/*for(Direction dir: allDir)
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
					fitness += 50;
				}
				else if(hallCount == 4)
				{
					fitness += 60;
				}
				else if(wallCount == 4 || wallCount == 3)
				{
					fitness -= 200;
				}
				else if(wallCount == 2 && hallCount == 2)
				{
					fitness -= 50;
				}*/

			}

			if(hall.size() > 0)
			{

				
				// Check hallway width
				Cell hallCell = hall.getCells().get(getRand().nextInt(hall.getCells().size()));
				Direction randomDir = Direction.randomDir();
				int width = 0;
				while(!outOfBounds(hallCell.getX() + randomDir.dx, hallCell.getY() + randomDir.dy) && getCell(hallCell.getX() + randomDir.dx, hallCell.getY() + randomDir.dy).getHallwayAssignment() == hallCell.getHallwayAssignment())
				{
					width++;
					hallCell = getCell(hallCell.getX() + randomDir.dx, hallCell.getY() + randomDir.dy);
				}

				if(width >= 5)
				{
					//Probably moving in length
				}
				else if(width <= 4)
				{
					// Hopefully width
					fitness += 50;
				}
			}
			else
			{
				System.out.printf("Hallways with size 0??\n");
			}
			
			
			if(firstRoomNum != -1 && secondRoomNum != -1 && firstRoomNum != secondRoomNum)
			{
				// The hallway must connect atleast 2 rooms
				fitness += 2000;
				break;
			}

			if( highestX - lowestX > 5 && highestY - lowestY > 5 && getCell(highestX - (getRand().nextInt(highestX - lowestX) + 1), lowestY + (getRand().nextInt(highestY - lowestY) + 1)).getCellType() != Globals.HALLWAY && getCell(lowestX  + (getRand().nextInt(highestX - lowestX) + 1), highestY - (getRand().nextInt(highestY - lowestY) + 1)).getCellType() != Globals.HALLWAY)
			{
				// Hallway is narrow and long
				fitness += 500;
			}
			else if(highestX - lowestX <= 5 && highestY - lowestY <= 5)
			{
				// Hall is narrow and short
				fitness += 500;
			}
			else
			{
				fitness -= 200;
			}

		}


		return fitness;
	}

	// Evaluate cavern's rooms
	// Rooms should be large and non-uniform
	// Some rooms should be large in width and height, more rooms should be more narrow almost like a hallways
		// So the players have areas to explore and arent just in a massive room
	
	private double evaluateRooms()
	{
		int lowestX = 100;
		int lowestY = 100;
		int highestX = 0;
		int highestY = 0;
		int centerX;
		int centerY;
		int currRoomCenterX = 0;
		int currRoomCenterY = 0;
		int retVal = 0;
		int currRoomLowX = 100;
		int currRoomLowY = 100;
		int currRoomHighX = 0;
		int currRoomHighY = 0;
		int wallContact = 0;
		int hallContact = 0;
		int roomContact = 0;
		double fitness = 0;
		Vector<Room> rooms = getRoomsVector();
		Cell neighborCell;
		
		// Rooms should be generally large
		// Rooms should NOT overlap with one another
		// Rooms should try to seperate
		// Rooms should try to connect to hallways

		for(Room evalRoom: rooms)
		{
			lowestX = 100;
			lowestY = 100;
			highestX = 0;
			highestY = 0;
			wallContact = 0;
			hallContact = 0;
			roomContact = 0;

			// Evaluate evalRooms likely center point and surroundings
			for(Cell c : evalRoom.getCells())
			{
				if(c.getX() < lowestX)
					lowestX = c.getX();

				if(c.getX() > highestX)
					highestX = c.getX();

				if(c.getY() < lowestY)
					lowestY = c.getY();

				if(c.getY() > highestY)
					highestY = c.getY();

				for(Direction dir: allDir)
				{
					if(!outOfBounds(c.getX() + dir.dx, c.getY() + dir.dy))
					{
						neighborCell = getCell(c.getX() + dir.dx, c.getY() + dir.dy);
						if(neighborCell.getCellType() == Globals.HALLWAY)
						{
							hallContact++;
							break;
						}
						else if(neighborCell.getCellType() == Globals.WALL || neighborCell.getCellType() == Globals.BLOCKED)
						{
							wallContact++;
							break;
						}
						else if(neighborCell.getCellType() == Globals.ROOM && neighborCell.getRoomAssignment() != evalRoom.getNumber())
						{
							roomContact++;
							break;
						}
					}
					
				}

			}

			centerX =  highestX - ((highestX - lowestX) / 2);
			centerY =  highestY - ((highestY - lowestY) / 2);

			if(evalRoom.size() < 90)
				fitness -= 500;
			else
				fitness+= 500;
			
			fitness += evalRoom.evaluateSize();
			
			// Evaluate each rooms distance from each other

			for(Room currentRoom : rooms)
			{
				if(evalRoom != currentRoom)
				{
					currRoomLowX = 100;
					currRoomLowY = 100;
					currRoomHighX = 0;
					currRoomHighY = 0;

					for(Cell c: currentRoom.getCells())
					{
						if(c.getX() < currRoomLowX)
							currRoomLowX = c.getX();

						if(c.getX() > currRoomHighX)
							currRoomHighX = c.getX();

						if(c.getY() < currRoomLowY)
							currRoomLowY = c.getY();

						if(c.getY() > currRoomHighY)
							currRoomHighY = c.getY();
					}

					currRoomCenterX = currRoomHighX - ((currRoomHighX - currRoomLowX) / 2);
					currRoomCenterY = currRoomHighY - ((currRoomHighY - currRoomLowY) / 2);

					if(Math.abs(centerX - currRoomCenterX) > 9 || Math.abs(centerY - currRoomCenterY) > 9)
					{
						// Rooms are probably pretty far apart
						fitness += 300;
					}
					else
					{
						fitness -= 100;
					}
				}
			}

			if(hallContact >= 15)
				fitness -= 100;
			else if(hallContact < 15)
				fitness += 100;

			if(wallContact - ((highestX - lowestX) + (highestY - lowestY)) > 0)
				fitness += 150;
			
			if(roomContact != 0)
				fitness -= 200;
		}
		return fitness;
	}

	// Evaluate cavern's doors
	// The only time doors should exist in a cave is if intelligent monsters live in the cave; even then it should be fairly rare

	private double evaluateDoors()
	{
		// Doors shouldn't exist in a cave
		// The less doors, the better fitness
		
		double fitness = 0;
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