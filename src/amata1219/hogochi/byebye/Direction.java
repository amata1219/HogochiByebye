package amata1219.hogochi.byebye;

public enum Direction {

	NORTH_EAST(3),
	NORTH_WEST(2),
	SOUTH_EAST(2),
	SOUTH_WEST(1);

	private final int n;

	private Direction(int n){
		this.n = n;
	}

	public int getNumber(){
		return n;
	}

}
