package amata1219.hogochi.byebye;

import java.util.HashMap;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Compartment {

	private Point min, max;

	private HashMap<Direction, Region> regions = new HashMap<>();

	public Compartment(int x, int z){
		System.out.println("XZ: " + x + ", " + z);
		x = Util.minus(x);
		z = Util.minus(z);

		System.out.println("XZ: " + x + ", " + z);

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

		int[] sortedX = Util.sortMinMax(minX, maxX);
		int[] sortedZ = Util.sortMinMax(minZ, maxZ);

		minX = Util.applyMinus(sortedX[0], xMinus);
		minZ = Util.applyMinus(sortedZ[0], zMinus);
		maxX = Util.applyMinus(sortedX[1], xMinus);
		maxZ = Util.applyMinus(sortedZ[1], zMinus);

		minX = Util.minus(minX);
		minZ = Util.minus(minZ);
		maxX = Util.minus(maxX);
		maxZ = Util.minus(maxZ);

		this.min = new Point(minX, minZ);
		this.max = new Point(maxX, maxZ);

		for(Direction direction : Direction.values())
			regions.put(direction, Util.createRegion(direction, min, max));

		System.out.println(min.getX() + ", " + min.getZ() + ", " + max.getX() + ", " + max.getZ());
	}

	public Compartment(int minX, int minZ, int maxX, int maxZ){
		this.min = new Point(minX, minZ);
		this.max = new Point(maxX, maxZ);

		for(Direction direction : Direction.values())
			regions.put(direction, Util.createRegion(direction, min, max));
	}

	public Compartment(ProtectedRegion region){
		int x = Util.minus(region.getMinimumPoint().getBlockX());
		int z = Util.minus(region.getMinimumPoint().getBlockZ());

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

		int[] sortedX = Util.sortMinMax(minX, maxX);
		int[] sortedZ = Util.sortMinMax(minZ, maxZ);

		minX = Util.applyMinus(sortedX[0], xMinus);
		minZ = Util.applyMinus(sortedZ[0], zMinus);
		maxX = Util.applyMinus(sortedX[1], xMinus);
		maxZ = Util.applyMinus(sortedZ[1], zMinus);

		minX = Util.minus(minX);
		minZ = Util.minus(minZ);
		maxX = Util.minus(maxX);
		maxZ = Util.minus(maxZ);

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
		System.out.println("COMBINE: " + 1);

		if(d1 == d2)
			return null;

		System.out.println("COMBINE: " + 2);

		boolean d1Even = Util.isEven(d1.getNumber());
		boolean d2Even = Util.isEven(d2.getNumber());

		if((d1Even && d2Even) || (!d1Even && !d2Even))
			return null;

		System.out.println("COMBINE: " + 3);

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
