import java.util.Random;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public enum Direction
{
	NORTH(0, 0, -1),
	EAST(1, 1, 0),
	SOUTH(2, 0, 1),
	WEST(3, -1, 0);

	public int dx, dy, ordinal;
	public boolean horizontalMove, verticalMove;
	public Direction opposite;
	private static final Random RAND = new Random();
	private static final List<Direction> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();

	private Direction(int ordinal, int x, int y)
	{
		dx = x;
		dy = y;
		this.ordinal = ordinal;
		horizontalMove = dx != 0;
		verticalMove = dy != 0;
	}

	public int getOrdinal()
	{
		return ordinal;
	}

	public static Direction randomDir()
	{
		return VALUES.get(RAND.nextInt(SIZE));
	}
}