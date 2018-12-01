package amata1219.hogochi.byebye;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Compartment {

	private DirectionNumberTable table;
	private Point min, max;

	private HashMap<Direction, Region> regions = new HashMap<>();

	public Compartment(int x, int z){
		x = Util.minus(x);
		z = Util.minus(z);

		table = new DirectionNumberTable(Util.toMainDirection(x, z));

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

	public Compartment(int minX, int minZ, int maxX, int maxZ){
		table = new DirectionNumberTable(Util.toMainDirection(minX, minZ));

		this.min = new Point(minX, minZ);
		this.max = new Point(maxX, maxZ);

		for(Direction direction : Direction.values())
			regions.put(direction, Util.createRegion(direction, min, max));
	}

	public Compartment(ProtectedRegion region){
		int x = Util.minus(region.getMinimumPoint().getBlockX());
		int z = Util.minus(region.getMinimumPoint().getBlockZ());

		table = new DirectionNumberTable(Util.toMainDirection(x, z));

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

	public DirectionNumberTable getDirectionNumberTable(){
		return table;
	}

	public Point getMin(){
		return min;
	}

	public Point getMax(){
		return max;
	}

	public List<Direction> getDirections(ProtectedRegion pr){
		List<Direction> list = new ArrayList<>();

		for(Direction direction : Direction.values()){
			Region region = getRegion(direction);
			if(!region.isProtected())
				continue;

			if(pr.getId().equals(region.getProtectedRegion().getId()))
				list.add(direction);
		}

		return list;
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
		System.out.println(d1.name() + " : " + d2.name());

		int w1 = d1.getNumber(), w2 = d2.getNumber();

		if(d1 == d2 || Util.isEven(w1) == Util.isEven(w2))
			return null;

		Direction d3 = d2;

		d2 = w1 > w2 ? d1 : d2;
		d1 = w1 > w2 ? d3 : d1;

		Region r1 = getRegion(d1), r2 = getRegion(d2);

		return new Region(null, r1.getMin(), r2.getMax());
	}

}
