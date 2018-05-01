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
		Vector<Cell> parentAMonsters = (Vector)parentA.getMonstersVector().clone();
		Vector<Cell> parentBMonsters  = (Vector)parentB.getMonstersVector().clone();
		Vector<Cell> parentATreasures = (Vector)parentA.getTreasuresVector().clone();
		Vector<Cell> parentBTreasures = (Vector)parentB.getTreasuresVector().clone();
		Vector<Cell> parentATraps = (Vector)parentA.getTrapsVector().clone();
		Vector<Cell> parentBTraps = (Vector)parentB.getTrapsVector().clone();

		Room parentRoom;
		Hallway parentHall;
		Cell parentMonster;
		Cell parentTreasure;
		Cell parentTrap;


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

		numChildGenes = (parentAMonsters.size() + parentBMonsters.size()) / 2;

		for(int i = 0; i < numChildGenes; i++)
		{
			parentSelection = getRand().nextInt(2);

			if(parentSelection == 0 && parentAMonsters.size() > 0)
			{
				parentMonster = parentAMonsters.remove(getRand().nextInt(parentAMonsters.size()));
			}
			else if(parentSelection == 1 && parentBMonsters.size() > 0)
			{
				parentMonster = parentBMonsters.remove(getRand().nextInt(parentBMonsters.size()));
			}
			else
			{
				break;
			}

			getCell(parentMonster.getX(), parentMonster.getY()).changeCellType(Globals.MONSTER);
		}

		numChildGenes = (parentATreasures.size() + parentBTreasures.size()) / 2;

		for(int i = 0; i < numChildGenes; i++)
		{
			parentSelection = getRand().nextInt(2);

			if(parentSelection == 0 && parentATreasures.size() > 0)
			{
				parentTreasure = parentATreasures.remove(getRand().nextInt(parentATreasures.size()));
			}
			else if(parentSelection == 1 && parentBTreasures.size() > 0)
			{
				parentTreasure = parentBTreasures.remove(getRand().nextInt(parentBTreasures.size()));
			}
			else
			{
				break;
			}

			getCell(parentTreasure.getX(), parentTreasure.getY()).changeCellType(Globals.TREASURE);
		}

		numChildGenes = (parentATraps.size() + parentBTraps.size()) / 2;

		for(int i = 0; i < numChildGenes; i++)
		{
			parentSelection = getRand().nextInt(2);
			if(parentSelection == 0 && parentATreasures.size() > 0)
			{
				parentTrap = parentATraps.remove(getRand().nextInt(parentATreasures.size()));
			}
			else if(parentSelection == 1 && parentBTreasures.size() > 0)
			{
				parentTrap = parentBTraps.remove(getRand().nextInt(parentBTreasures.size()));
			}
			else
			{
				break;
			}

			getCell(parentTrap.getX(), parentTrap.getY()).changeCellType(Globals.TRAP);
		}

		fillVectors();
		mutate(mutationRate);
		//fillVectors();

		


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
		Vector<Cell> monsters = getMonstersVector();
		Vector<Cell> treasures = getTreasuresVector();
		Vector<Cell> traps = getTrapsVector();
		boolean successfulMutation = false;
		
		int mutationSelection = 0;
		
		// Hallway mutation
		for(Hallway mutateHall : hallways)
		{
			
			ArrayList<Cell> hallCells = mutateHall.getCells();
			for(int i = 0; i < hallCells.size(); i++)
			{

				if(getRand().nextDouble() < mutationRate)
				{
					successfulMutation = false;
					while(successfulMutation == false)
					{
						mutationSelection = getRand().nextInt(5);

						// Reroute around
						if(mutationSelection == 0)
						{
							int originX = hallCells.get(i).getX();
							int originY = hallCells.get(i).getY();
							Direction randDir = Direction.randomDir();
							if(!outOfBounds(originX + (randDir.dx * 2), originY + (randDir.dy * 2)) )
							{
								getCell(originX + (randDir.dx * 2), originY + (randDir.dy * 2)).changeCellType(Globals.HALLWAY);
								getCell(originX + (randDir.dx * 2), originY + (randDir.dy * 2)).setHallwayAssignment(mutateHall.getNumber());
								mutateHall.add(getCell(originX + (randDir.dx * 2), originY + (randDir.dy * 2)));
							}
							

							if(randDir.getOrdinal() % 2 == 0 && !outOfBounds(originX - 1, originX + 1) && !outOfBounds(originX + (randDir.dx * 2), originY + (randDir.dy * 2)))
							{
								getCell(originX - 1, originY + (randDir.dy * 2)).changeCellType(Globals.HALLWAY);
								getCell(originX + 1, originY + (randDir.dy * 2)).changeCellType(Globals.HALLWAY);
								getCell(originX - 1, originY + randDir.dy).changeCellType(Globals.HALLWAY);
								getCell(originX + 1, originY + randDir.dy).changeCellType(Globals.HALLWAY);

								getCell(originX - 1, originY + (randDir.dy * 2)).setHallwayAssignment(mutateHall.getNumber());
								getCell(originX + 1, originY + (randDir.dy * 2)).setHallwayAssignment(mutateHall.getNumber());
								getCell(originX - 1, originY + randDir.dy).setHallwayAssignment(mutateHall.getNumber());
								getCell(originX + 1, originY + randDir.dy).setHallwayAssignment(mutateHall.getNumber());

								mutateHall.add(getCell(originX - 1, originY + (randDir.dy * 2)));
								mutateHall.add(getCell(originX + 1, originY + (randDir.dy * 2)));
								mutateHall.add(getCell(originX - 1, originY + randDir.dy));
								mutateHall.add(getCell(originX + 1, originY + randDir.dy));

								getCell(originX, originY).changeCellType(Globals.WALL);
								getCell(originX, originY).setHallwayAssignment(-1);
								
								
							}
							else if(randDir.getOrdinal() % 2 != 0 && !outOfBounds(originY - 1, originY + 1) && !outOfBounds(originX + (randDir.dx * 2), originY + (randDir.dy * 2)))
							{

								getCell(originX + (randDir.dx * 2), originY - 1).changeCellType(Globals.HALLWAY);
								getCell(originX + (randDir.dx * 2), originY + 1).changeCellType(Globals.HALLWAY);
								getCell(originX + randDir.dx, originY - 1).changeCellType(Globals.HALLWAY);
								getCell(originX + randDir.dx, originY + 1).changeCellType(Globals.HALLWAY);

								getCell(originX + (randDir.dx * 2), originY - 1).setHallwayAssignment(mutateHall.getNumber());
								getCell(originX + (randDir.dx * 2), originY + 1).setHallwayAssignment(mutateHall.getNumber());
								getCell(originX + randDir.dx, originY - 1).setHallwayAssignment(mutateHall.getNumber());
								getCell(originX + randDir.dx, originY + 1).setHallwayAssignment(mutateHall.getNumber());

								mutateHall.add(getCell(originX + (randDir.dx * 2), originY - 1));
								mutateHall.add(getCell(originX + (randDir.dx * 2), originY + 1));
								mutateHall.add(getCell(originX + randDir.dx, originY - 1));
								mutateHall.add(getCell(originX + randDir.dx, originY + 1));

							}

							successfulMutation = true;
						}
						// Add Neighbor
						else if(mutationSelection == 1)
						{
							
							Direction randDir = Direction.randomDir();
							Cell currentCell = getCell(hallCells.get(i).getX(), hallCells.get(i).getY());
							int count = 0;
							
							if(!outOfBounds(currentCell.getX() + randDir.dx, currentCell.getY() + randDir.dy))
							{
								getCell(currentCell.getX() + randDir.dx, currentCell.getY() + randDir.dy).changeCellType(Globals.HALLWAY);
								getCell(currentCell.getX() + randDir.dx, currentCell.getY() + randDir.dy).setHallwayAssignment(mutateHall.getNumber());
								mutateHall.add(getCell(currentCell.getX() + randDir.dx, currentCell.getY() + randDir.dy));
							}

							successfulMutation = true;
						}
						// Delete
						else if(mutationSelection == 2)
						{
							
							Cell currCell = hallCells.get(i);
							getCell(currCell.getX(), currCell.getY()).changeCellType(Globals.WALL);
							getCell(currCell.getX(), currCell.getY()).setHallwayAssignment(-1);
							hallCells.remove(currCell);

							successfulMutation = true;

						}
						//Context aware
						// Route To Room
						else if(mutationSelection == 3)
						{
							
							Cell origin = hallCells.get(i);

							for(Direction dir: allDir)
							{
								if(!outOfBounds(origin.getX() + (dir.dx * 3), origin.getY() + (dir.dy * 3)) && getCell(origin.getX() + (dir.dx * 3), origin.getY() + (dir.dy * 3)).getCellType() == Globals.ROOM && getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).getCellType() == Globals.WALL )
								{
									getCell(origin.getX() + (dir.dx * 2), origin.getY() + (dir.dy * 2)).changeCellType(Globals.HALLWAY);
									getCell(origin.getX() + (dir.dx * 2), origin.getY() + (dir.dy * 2)).setHallwayAssignment(mutateHall.getNumber());

									getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).changeCellType(Globals.HALLWAY);
									getCell(origin.getX() + dir.dx, origin.getY() + dir.dy).setHallwayAssignment(mutateHall.getNumber());

									hallCells.add(getCell(origin.getX() + (dir.dx * 2), origin.getY() + (dir.dy * 2)));
									hallCells.add(getCell(origin.getX() + dir.dx, origin.getY() + dir.dy));
									successfulMutation = true;
								}
							}

						}
						// Extension
						else if(mutationSelection == 4)
						{
							if(mutateHall.size() < 10)
							{
								int extensionLength = getRand().nextInt(15);
								Direction dir = Direction.randomDir();
								Cell origin = hallCells.get(i);

								for(int j = 0; j < extensionLength; j++)
								{
									if(!outOfBounds(origin.getX() + (dir.dx * j), origin.getY() + (dir.dy * j)) && getCell(origin.getX() + (dir.dx * j), origin.getY() + (dir.dy * j)).getCellType() == Globals.WALL)
									{
										getCell(origin.getX() + (dir.dx * j), origin.getY() + (dir.dy * j)).changeCellType(Globals.HALLWAY);
										getCell(origin.getX() + (dir.dx * j), origin.getY() + (dir.dy * j)).setHallwayAssignment(mutateHall.getNumber());
										hallCells.add(getCell(origin.getX() + (dir.dx * j), origin.getY() + (dir.dy * j)));

									}
									
								}
								successfulMutation = true;
							}
						}
					}
					
				}
			}
		}


		// Room mutation
		//successfulMutation = false;

		for(Room mutateRoom : rooms)
		{
			
			ArrayList<Cell> roomCells = mutateRoom.getCells();
			for(int i = 0; i < roomCells.size(); i++)
			{
				if(getRand().nextDouble() < mutationRate )
				{
					successfulMutation = false;
					while(successfulMutation == false)
					{
						mutationSelection = getRand().nextInt(3);
						

						// Change cell type
						if(mutationSelection == 0)
						{

							Cell c = roomCells.get(i);
							getCell(c.getX(), c.getY()).changeCellType(Globals.WALL);
							getCell(c.getX(), c.getY()).setRoomAssignment(-1);
							successfulMutation = true;
						
						}

						// Context aware mutations
						// Add Neighbor
						else if(mutationSelection == 1)
						{
							
							// Check to see if the cell qualifies for the mutation
							Cell c = roomCells.get(i);
							for(Direction dir : allDir)
							{
								if(!outOfBounds(c.getX() + dir.dx, c.getY() + dir.dy) && getCell(c.getX() + dir.dx, c.getY() + dir.dy).getCellType() == Globals.WALL)
								{
									getCell(c.getX() + dir.dx, c.getY() + dir.dy).changeCellType(Globals.ROOM);
									getCell(c.getX() + dir.dx, c.getY() + dir.dy).setRoomAssignment(mutateRoom.getNumber());
									roomCells.add(getCell(c.getX() + dir.dx, c.getY() + dir.dy));
									successfulMutation = true;
								}
							}

						}
						// Connect Room
						else if(mutationSelection == 2)
						{
							
							// For each direction, check if a room is nearby, if true connect them
							Cell c = roomCells.get(i);
							for(Direction dir: allDir)
							{
								if(!outOfBounds(c.getX() + (dir.dx * 3), c.getY() + (dir.dy * 3)) && getCell(c.getX() + (dir.dx * 3), c.getY() + (dir.dy * 3)).getCellType() == Globals.ROOM && getCell(c.getX() + (dir.dx * 3), c.getY() + (dir.dy * 3)).getRoomAssignment() != mutateRoom.getNumber())
								{
									
									// There is a nearby room
									getCell(c.getX() + (dir.dx * 2), c.getY() + (dir.dy * 2)).changeCellType(Globals.ROOM);
									getCell(c.getX() + (dir.dx * 2), c.getY() + (dir.dy * 2)).setRoomAssignment(mutateRoom.getNumber());

									getCell(c.getX() + dir.dx, c.getY() + dir.dy).changeCellType(Globals.ROOM);
									getCell(c.getX() + dir.dx, c.getY() + dir.dy).setRoomAssignment(mutateRoom.getNumber());

									successfulMutation = true;
								}
							}

						}
					}
					
				}
			}
		}	

		// Monster mutation
		for(Cell mutateMonster: monsters)
		{
			if(getRand().nextDouble() < mutationRate)
			{
				successfulMutation = false;
				while(successfulMutation == false)
				{
					mutationSelection = getRand().nextInt(2);

					// Shift Position
					if(mutationSelection == 0)
					{
						System.out.printf("Shift mutate \n");
						int shiftBy = getRand().nextInt(3) + 1;
						Direction shiftIn = Direction.randomDir();

						if(!outOfBounds(mutateMonster.getX() + (shiftIn.dx * shiftBy), mutateMonster.getY() + (shiftIn.dy * shiftBy)))
						{
							getCell(mutateMonster.getX(), mutateMonster.getY()).changeCellType(Globals.WALL);
							monsters.remove(mutateMonster);

							getCell(mutateMonster.getX() + (shiftIn.dx * shiftBy), mutateMonster.getY() + (shiftIn.dy * shiftBy)).changeCellType(Globals.MONSTER);
							monsters.add(getCell(mutateMonster.getX() + (shiftIn.dx * shiftBy), mutateMonster.getY() + (shiftIn.dy * shiftBy)));
						}
						successfulMutation = true;
					}
					// Context Aware
					// Move to Room Entrance
					else if(mutationSelection == 1)
					{
						
						// If monster has 2 neighbor hallways and is within reasonable range of a room
						int neighborHalls = 0;
						Direction nearbyRoomDir = null;
						int nearbyRoomDistance = 0;
						for(Direction dir: allDir)
						{
							if(!outOfBounds(mutateMonster.getX() + dir.dx, mutateMonster.getY() + dir.dy) && getCell(mutateMonster.getX() + dir.dx, mutateMonster.getY() + dir.dy).getCellType() == Globals.HALLWAY)
							{

								neighborHalls++;
							}

							for(int i = 2; i < 7; i++)
							{
								if(!outOfBounds(mutateMonster.getX() + (dir.dx * i), mutateMonster.getY() + (dir.dy * i)) && getCell(mutateMonster.getX() + (dir.dx * i), mutateMonster.getY() + (dir.dy * i)).getCellType() == Globals.ROOM)
								{
									nearbyRoomDir = dir;
									nearbyRoomDistance = i;
								}
							}

							
						}

						if(neighborHalls >= 2 && nearbyRoomDir != null)
						{
							System.out.printf("move to room mutate \n");
							getCell(mutateMonster.getX() + (nearbyRoomDir.dx * nearbyRoomDistance), mutateMonster.getY() + (nearbyRoomDir.dy * nearbyRoomDistance)).changeCellType(Globals.MONSTER);
							getCell(mutateMonster.getX() + (nearbyRoomDir.dx * nearbyRoomDistance), mutateMonster.getY() + (nearbyRoomDir.dy * nearbyRoomDistance)).setRoomAssignment(-1);
							successfulMutation = true;
						}

					
					}

				}
			}
		}

		// Treasure Mutation
		for(Cell mutateTreasure : treasures)
		{
			if(getRand().nextDouble() < mutationRate)
			{
				successfulMutation = false;
				while(!successfulMutation)
				{
					mutationSelection = getRand().nextInt(2);

					// Shift Mutation
					if(mutationSelection == 0)
					{
						Direction dir = Direction.randomDir();
						int shiftBy = getRand().nextInt(3) + 1;

						if(!outOfBounds(mutateTreasure.getX() + (dir.dx * shiftBy),mutateTreasure.getY() + (dir.dy * shiftBy)))
						{
							getCell(mutateTreasure.getX(), mutateTreasure.getY()).changeCellType(Globals.WALL);
							treasures.remove(mutateTreasure);

							getCell(mutateTreasure.getX() + (dir.dx * shiftBy),mutateTreasure.getY() + (dir.dy * shiftBy)).changeCellType(Globals.TREASURE);
							treasures.add(getCell(mutateTreasure.getX() + (dir.dx * shiftBy),mutateTreasure.getY() + (dir.dy * shiftBy)));
						}
						successfulMutation = true;
					}

				}
			}
		}
		// Trap Mutation
		for(Cell mutateTrap : traps)
		{
			if(getRand().nextDouble() < mutationRate)
			{
				successfulMutation = false;
				while(!successfulMutation)
				{
					mutationSelection = getRand().nextInt(2);

					// Shift Mutation
					if(mutationSelection == 0)
					{
						Direction dir = Direction.randomDir();
						int shiftBy = getRand().nextInt(3) + 1;

						if(!outOfBounds(mutateTrap.getX() + (dir.dx * shiftBy),mutateTrap.getY() + (dir.dy * shiftBy)))
						{
							getCell(mutateTrap.getX(), mutateTrap.getY()).changeCellType(Globals.WALL);
							traps.remove(mutateTrap);

							getCell(mutateTrap.getX() + (dir.dx * shiftBy),mutateTrap.getY() + (dir.dy * shiftBy)).changeCellType(Globals.TRAP);
							traps.add(getCell(mutateTrap.getX() + (dir.dx * shiftBy),mutateTrap.getY() + (dir.dy * shiftBy)));
						}
						successfulMutation = true;
					}
					// Context Aware
					// Move to Room Entrance
					else if(mutationSelection == 1)
					{
						
						// If monster has 2 neighbor hallways and is within reasonable range of a room
						int neighborHalls = 0;
						Direction nearbyRoomDir = null;
						int nearbyRoomDistance = 0;
						for(Direction dir: allDir)
						{
							if(!outOfBounds(mutateTrap.getX() + dir.dx, mutateTrap.getY() + dir.dy) && getCell(mutateTrap.getX() + dir.dx, mutateTrap.getY() + dir.dy).getCellType() == Globals.HALLWAY)
							{

								neighborHalls++;
							}

							for(int i = 2; i < 7; i++)
							{
								if(!outOfBounds(mutateTrap.getX() + (dir.dx * i), mutateTrap.getY() + (dir.dy * i)) && getCell(mutateTrap.getX() + (dir.dx * i), mutateTrap.getY() + (dir.dy * i)).getCellType() == Globals.ROOM)
								{
									nearbyRoomDir = dir;
									nearbyRoomDistance = i;
								}
							}

							
						}

						if(neighborHalls >= 2 && nearbyRoomDir != null)
						{
							System.out.printf("move to room mutate \n");
							getCell(mutateTrap.getX() + (nearbyRoomDir.dx * nearbyRoomDistance), mutateTrap.getY() + (nearbyRoomDir.dy * nearbyRoomDistance)).changeCellType(Globals.TRAP);
							getCell(mutateTrap.getX() + (nearbyRoomDir.dx * nearbyRoomDistance), mutateTrap.getY() + (nearbyRoomDir.dy * nearbyRoomDistance)).setRoomAssignment(-1);
							successfulMutation = true;
						}

					
					}

				}
			}
		}

		for(int i = 0; i < hallways.size(); i++)
		{
			hallways.get(i).purge();

			if(hallways.get(i).size() < 5)
			{
				hallways.get(i).erase();
				hallways.remove(hallways.get(i));
				i--;
			}
		}

		for(int i = 0; i < rooms.size(); i++)
		{
			// purge removes any cells that no longer belong in the room
			rooms.get(i).purge();

			if(rooms.get(i).size() < 3)
			{
				rooms.get(i).erase();
				rooms.remove(rooms.get(i));
				i--;
			}
		}
		
	}
	


	@Override
	public double evaluateFitness()
	{
		
		setFitness(0.0);
		double myFit = 0.0;
		

		// Should be a lot of rooms
		// Rooms should be large and non-uniform
		if(getRoomsVector().size() > 5 && getRoomsVector().size() < 10)
		{
			myFit += 500;
		}
		

		if(getHallwaysVector().size() == getRoomsVector().size())
		{
			myFit += 200;
		}
		else if(getHallwaysVector().size() > getRoomsVector().size() && getHallwaysVector().size() < getRoomsVector().size() * 3)// && getHallwaysVector().size() < getRoomsVector().size() + 10)
		{
			myFit += 500;
		}

		myFit += evaluateHallways();
		myFit += evaluateRooms();

		int numDisconnects = solveMaze();

		if(numDisconnects == 0)
		{
			System.out.printf("No disconnects \n");
			myFit += 5000;
		}
		else
		{
			myFit -= numDisconnects * - 200;
		}

		if(myFit <= 0)
		{
			myFit = 1;
		}

		setFitness(myFit);
		return myFit;

		
	}


	// Evaluate cavern's hallways
	private int solveMaze()
	{
		// Evaluate if all hallway and room cells can be reached
		// recursive backtracking algorithm
		Vector<Cell> unvisitedCells = getAllTraversableCells();
		//System.out.printf("%d \n", unvisitedCells.size());
		Cell current = unvisitedCells.get(Math.floorMod(getRand().nextInt(), unvisitedCells.size()));
		Direction nextDir = null;
		int numberOfDisconnects = 0;
		Vector<Cell> cellStack = new Vector<Cell>();
		

		cellStack.add(current);

		while(!unvisitedCells.isEmpty())
		{
			nextDir = null;
			
			
			
			for(Direction dir : allDir)
			{
				if(!outOfBounds(current.getX() + dir.dx, current.getY() + dir.dy) && unvisitedCells.contains(getCell(current.getX() + dir.dx, current.getY() + dir.dy)))
				{
					nextDir = dir;
				}
			}

			if(nextDir != null)
			{
				
				unvisitedCells.remove(current);
				current = getCell(current.getX() + nextDir.dx, current.getY() + nextDir.dy);
				cellStack.add(current);
				//unvisitedCells.remove(current);
				
			}
			else if(!cellStack.isEmpty())
			{
				// If no where to move, recurse
				current = cellStack.remove(cellStack.size() - 1);
				//getCell(current.getX(), current.getY()).changeCellType(Globals.TEST_MUTATION);
			}
			else
			{
				//System.out.printf("jumping\n");
				// If no where to move and can't recurse, choose a new unvisited cell
				numberOfDisconnects++;
				current = unvisitedCells.remove(getRand().nextInt(unvisitedCells.size()));
				//getCell(current.getX(), current.getY()).changeCellType(Globals.TEST_MUTATION_2);
				cellStack.add(current);
				
			}

		}

		return numberOfDisconnects;


	}

	private double evaluateHallways()
	{
		//System.out.printf("Eval Hallways");
		double fitness = 0;
		Vector<Hallway> hallways = getHallwaysVector();
		Vector<Room> rooms = getRoomsVector();
		int sumOfHallways = 0;
		
		
		int lowestX; 
		int highestX;
		int lowestY;
		int highestY;

		ArrayList<Integer> connectingRooms = new ArrayList<Integer>();
		ArrayList<Integer> connectingHalls = new ArrayList<Integer>();

		if(hallways.size() < rooms.size())
		{
			fitness -= 250;
		}
		


		for(Hallway hall: hallways)
		{
			connectingRooms.clear();
			connectingHalls.clear();
			lowestX = 100; 
			highestX = 0;
			lowestY = 100;
			highestY = 0;
			sumOfHallways += hall.size();
			
			for(Cell c : hall.getCells())
			{
				// Gather the number of rooms this hallway connects
				for(Direction dir: allDir)
				{
					if(!outOfBounds(c.getX() + dir.dx, c.getY() + dir.dy) && getCell(c.getX() + dir.dx, c.getY() + dir.dy).getCellType() == Globals.ROOM && !connectingRooms.contains(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getRoomAssignment()))
					{
						connectingRooms.add(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getRoomAssignment());
					}
					else if(!outOfBounds(c.getX() + dir.dx, c.getY() + dir.dy) && getCell(c.getX() + dir.dx, c.getY() + dir.dy).getCellType() == Globals.HALLWAY && getCell(c.getX() + dir.dx, c.getY() + dir.dy).getHallwayAssignment() != hall.getNumber() && !connectingHalls.contains(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getHallwayAssignment()))
					{
						connectingHalls.add(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getHallwayAssignment());
					}
				}
				if(c.getX() < lowestX)
					lowestX = c.getX();
				else if(c.getX() > highestX)
					highestX = c.getX();

				if(c.getY() < lowestY)
					lowestY = c.getY();
				else if(c.getY() > highestY)
					highestY = c.getY();
			}





			// Fitness changes
			if(hall.size() > 15)
			{
				fitness += 200;
			}
			else if(hall.size() > 10)
			{
				fitness += 100;
			}
			else
			{
				fitness -= 100;
			}


			// Add to fitness based on how many rooms and hallway this hallway connects
			if(connectingRooms.size() == 2)
			{
				fitness += 300;
			}
			else if(connectingRooms.size() == 3)
			{
				fitness += 400;
			}
			else if(connectingRooms.size() == 0)
			{
				fitness -= 500;
			}
			

			// Evaluate how narrow the hallway is
			if((highestX - lowestX > 12 && highestY - lowestY < 5) || highestY - lowestY > 12 && highestX - lowestX < 5)
			{
				// horizonal with a little deviation
				fitness += 200;
			}
			else if((highestX - lowestX > 20 && highestY - lowestY < 7) || highestY - lowestY > 20 && highestX - lowestX < 7)
			{
				fitness += 300;
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

		ArrayList<Integer> connectingHalls = new ArrayList<Integer>();
		ArrayList<Integer> connectingRooms = new ArrayList<Integer>();
		
		// Rooms should be generally large
		// Rooms should NOT overlap with one another
		// Rooms should try to seperate
		// Rooms should try to connect to hallways

		// For each room check to see how many hallways it connects to
		// If it connects to other rooms

		if(rooms.size() < 3)
		{
			fitness -= 300;
		}

		for(Room room: rooms)
		{
			connectingRooms.clear();
			connectingHalls.clear();
			lowestX = 100; 
			highestX = 0;
			lowestY = 100;
			highestY = 0;

			for(Cell c : room.getCells())
			{
				for(Direction dir: allDir)
				{
					if(!outOfBounds(c.getX() + dir.dx, c.getY() + dir.dy) && getCell(c.getX() + dir.dx, c.getY() + dir.dy).getCellType() == Globals.ROOM && getCell(c.getX() + dir.dx, c.getY() + dir.dy).getHallwayAssignment() != room.getNumber() && !connectingRooms.contains(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getRoomAssignment()))
					{
						connectingRooms.add(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getRoomAssignment());
					}
					else if(!outOfBounds(c.getX() + dir.dx, c.getY() + dir.dy) && getCell(c.getX() + dir.dx, c.getY() + dir.dy).getCellType() == Globals.HALLWAY  && !connectingHalls.contains(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getHallwayAssignment()))
					{
						connectingHalls.add(getCell(c.getX() + dir.dx, c.getY() + dir.dy).getHallwayAssignment());
					}
				}

				if(c.getX() < lowestX)
					lowestX = c.getX();
				else if(c.getX() > highestX)
					highestX = c.getX();

				if(c.getY() < lowestY)
					lowestY = c.getY();
				else if(c.getY() > highestY)
					highestY = c.getY();

				
			}

			centerX =  highestX - ((highestX - lowestX) / 2);
			centerY =  highestY - ((highestY - lowestY) / 2);

			for(Room currRoom: rooms)
			{
				if(currRoom != room)
				{
					currRoomLowX = 100;
					currRoomLowY = 100;
					currRoomHighX = 0;
					currRoomHighY = 0;

					for(Cell c: currRoom.getCells())
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

					if(Math.abs(centerX - currRoomCenterX) > 12 || Math.abs(centerY - currRoomCenterY) > 12)
					{
						// Rooms are probably pretty far apart
						fitness += 250;
					}
				}
			}
			
		}

		


		// Evaluate connections
		if(connectingRooms.size() == 0)
		{
			fitness += 150;
		}


		if(connectingHalls.size() > 0 && connectingHalls.size() < 4)
		{
			fitness += connectingHalls.size() * 150;
		}
		else if(connectingHalls.size() > 4 )
		{
			fitness += connectingHalls.size() * 50;
		}

		// Evaluate room size
		if(highestX - lowestX > 5 && highestY - lowestY > 5 && highestX - lowestX < 12 && highestY - lowestY < 12)
		{
			fitness += 150;
		}

		// Evaluate uniformity
		if((highestX - lowestX) != (highestY - lowestY))
		{
			// Perfect Square
			fitness += 150;
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