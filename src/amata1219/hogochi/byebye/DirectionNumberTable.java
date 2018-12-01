package amata1219.hogochi.byebye;

import java.util.HashMap;

public class DirectionNumberTable {

	private Direction mainDirection;

	private HashMap<Integer, Integer> numbers = new HashMap<>();
	private HashMap<Integer, Direction> directions = new HashMap<>();

	public DirectionNumberTable(Direction mainDirection){
		this.mainDirection = mainDirection;

		switch(mainDirection){
		case NORTH_EAST:
			numbers.put(5, 3);
			numbers.put(6, 4);
			numbers.put(14, 1);
			numbers.put(15, 2);

			directions.put(5, Direction.NORTH_WEST);
			directions.put(6, Direction.NORTH_EAST);
			directions.put(14, Direction.SOUTH_WEST);
			directions.put(15, Direction.SOUTH_EAST);
			break;
		case NORTH_WEST:
			numbers.put(5, 4);
			numbers.put(6, 3);
			numbers.put(14, 2);
			numbers.put(15, 1);

			directions.put(5, Direction.NORTH_EAST);
			directions.put(6, Direction.NORTH_WEST);
			directions.put(14, Direction.SOUTH_EAST);
			directions.put(15, Direction.SOUTH_WEST);
			break;
		case SOUTH_EAST:
			numbers.put(5, 1);
			numbers.put(6, 2);
			numbers.put(14, 3);
			numbers.put(15, 4);

			directions.put(5, Direction.SOUTH_WEST);
			directions.put(6, Direction.SOUTH_EAST);
			directions.put(14, Direction.NORTH_WEST);
			directions.put(15, Direction.NORTH_EAST);
			break;
		case SOUTH_WEST:
			numbers.put(5, 2);
			numbers.put(6, 1);
			numbers.put(14, 4);
			numbers.put(15, 3);

			directions.put(5, Direction.SOUTH_EAST);
			directions.put(6, Direction.SOUTH_WEST);
			directions.put(14, Direction.NORTH_EAST);
			directions.put(15, Direction.NORTH_WEST);
			break;
		default:
			break;
		}
	}

	public Direction getMainDirection(){
		return mainDirection;
	}

	public int getNumber(int slotNumber){
		return numbers.get(slotNumber);
	}

	public Direction getDirection(int slotNumber){
		return directions.get(slotNumber);
	}

}
