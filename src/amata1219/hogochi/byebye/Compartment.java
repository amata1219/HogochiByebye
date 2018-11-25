package amata1219.hogochi.byebye;

import java.util.HashMap;

public class Compartment {

	private Point min, max;

	private HashMap<Direction, Region> regions = new HashMap<>();

	public Compartment(int x, int z){
		boolean xMinus = Util.isUnderZero(x);
		boolean zMinus = Util.isUnderZero(z);

		x = Util.abs(x);
		z = Util.abs(z);

		int xAddress = Util.getAddress(x);
		int zAddress = Util.getAddress(z);

		int minX = Util.getMin(xAddress);
		int minZ = Util.getMin(zAddress);

		int maxX = Util.getMax(minX);
		int maxZ = Util.getMax(minZ);

		minX = Util.applyMinus(minX, xMinus);
		minZ = Util.applyMinus(minZ, zMinus);
		maxX = Util.applyMinus(maxX, xMinus);
		maxZ = Util.applyMinus(maxZ, zMinus);

		int[] sortedX = Util.sortMinMax(minX, maxX);
		int[] sortedZ = Util.sortMinMax(minZ, maxZ);

		this.min = new Point(sortedX[0], sortedZ[0]);
		this.max = new Point(sortedX[1], sortedZ[1]);

		for(Direction direction : Direction.values())
			regions.put(direction, Util.createRegion(direction, min, max));
	}

	public Compartment(int minX, int minZ, int maxX, int maxZ){
		this.min = new Point(minX, minZ);
		this.max = new Point(maxX, maxZ);

		for(Direction direction : Direction.values())
			regions.put(direction, Util.createRegion(direction, min, max));
	}

	public boolean isIn(int x, int z){
		return Util.isIn(x, min.getX(), max.getX()) && Util.isIn(z, min.getZ(), max.getZ());
	}

	public Region getRegion(int x, int z){
		for(Region region : regions.values()){
			if(region.isIn(x, z))
				return region;
		}

		return null;
	}

	public Region getRegion(Direction direction){
		return regions.get(direction);
	}

	public Region combine(Direction d1, Direction d2){
		if(d1 == d2)
			return null;

		boolean d1Even = Util.isEven(d1.getNumber());
		boolean d2Even = Util.isEven(d2.getNumber());

		if((d1Even && d2Even) || (!d1Even && !d2Even))
			return null;

		if(d1.getNumber() > d2.getNumber()){
			Direction d3 = d1;
			d1 = d2;
			d2 = d3;
		}

		Region r1 = regions.get(d1);
		Region r2 = regions.get(d2);

		return new Region(null, r1.getMin(), r2.getMax());
	}

}
