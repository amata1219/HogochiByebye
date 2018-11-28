package amata1219.hogochi.byebye;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Util {

	public static final int ROAD_WIDTH = 12;
	public static final int COMPARTMENT_ONE_SIDE = 50;
	public static final int REGION_ONE_SIDE = 25;

	public static boolean isUnderZero(int n){
		return n < 0;
	}

	public static int abs(int n){
		return n > -0 ? n : -n;
	}

	public static int applyMinus(int absoluteN, boolean nMinus){
		return nMinus ? -absoluteN : absoluteN;
	}

	public static boolean isEven(int n){
		return n % 2 == 0;
	}

	public static int getAddress(int n){
		return (n - 6) / (ROAD_WIDTH + COMPARTMENT_ONE_SIDE);
	}

	public static int getMin(int address){
		return address * (ROAD_WIDTH + COMPARTMENT_ONE_SIDE) + 6;
	}

	public static int getMax(int min){
		return min + (COMPARTMENT_ONE_SIDE - 1);
	}

	public static int plusOne(int n){
		return n > -0 ? n + 1 : n - 1;
	}

	public static int minusOne(int n){
		return n > -0 ? n - 1 : n + 1;
	}

	public static boolean isIn(int n, int lowerLimit, int upperLimit){
		return lowerLimit <= n && n <= upperLimit;
	}

	public static int[] sortMinMax(int n1, int n2){
		int n3 = n1;

		n1 = n1 < n2 ? n1 : n2;
		n2 = n1 > n2 ? n3 : n2;

		return new int[]{n1, n2};
	}

	public static Location toLocation(int x, int z, boolean isMax){
		return new Location(Bukkit.getWorld("main_flat"), x, isMax ? 255 : 0, z);
	}

	public static BlockVector toBlockVector(int x, int y, int z){
		return BlockVector.toBlockPoint(x, y, z);
	}

	public static BlockVector toBlockVector(Point p, boolean isMax){
		return BlockVector.toBlockPoint(p.getX(), isMax ? 255 : 0, p.getZ());
	}

	public static Direction toDirection(int n){
		switch(n){
		case 1:
			return Direction.SOUTH_WEST;
		case 2:
			return Direction.SOUTH_EAST;
		case 3:
			return Direction.NORTH_WEST;
		case 4:
			return Direction.NORTH_EAST;
		default:
			return null;
		}
	}

	public static Region createRegion(Direction direction, Point min, Point max){
		int minX = 0, minZ = 0, maxX = 0, maxZ = 0;

		switch(direction){
		case NORTH_EAST:
			minX = max.getAbsoluteX() - (REGION_ONE_SIDE - 1);
			minZ = max.getAbsoluteZ() - (REGION_ONE_SIDE - 1);

			maxX = max.getAbsoluteX();
			maxZ = max.getAbsoluteZ();
			break;
		case NORTH_WEST:
			minX = min.getAbsoluteX() + REGION_ONE_SIDE;
			minZ = min.getAbsoluteZ();

			maxX = max.getAbsoluteX();
			maxZ = max.getAbsoluteZ() - REGION_ONE_SIDE;
			break;
		case SOUTH_EAST:
			minX = min.getAbsoluteX();
			minZ = min.getAbsoluteZ() + REGION_ONE_SIDE;

			maxX = max.getAbsoluteZ() - REGION_ONE_SIDE;
			maxZ = max.getAbsoluteZ();
			break;
		case SOUTH_WEST:
			minX = min.getAbsoluteX();
			minZ = min.getAbsoluteZ();

			maxX = min.getAbsoluteX() + (REGION_ONE_SIDE - 1);
			maxZ = min.getAbsoluteZ() + (REGION_ONE_SIDE - 1);
			break;
		default:
			return null;
		}

		int[] sortedX = Util.sortMinMax(minX, maxX);
		int[] sortedZ = Util.sortMinMax(minZ, maxZ);

		return new Region(direction, sortedX[0], sortedZ[0], sortedX[1], sortedZ[1]);
	}

	public static ProtectedRegion createProtectedRegion(IdType id, Region r){
		return createProtectedRegion(id, r.getMin(), r.getMax());
	}

	public static ProtectedRegion createProtectedRegion(IdType id, Point p1, Point p2){
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(id.getString() + System.nanoTime(), toBlockVector(p1, false), toBlockVector(p2, true));

		HogochiByebye.getPlugin().getWorldGuardPlugin().getRegionManager(Bukkit.getWorld("main_flat")).addRegion(region);
		return region;
	}

	public static void removeProtectedRegion(ProtectedRegion region){
		removeProtectedRegion(region.getId());
	}

	public static void removeProtectedRegion(String id){
		HogochiByebye.getPlugin().getWorldGuardPlugin().getRegionManager(Bukkit.getWorld("main_flat")).removeRegion(id);
	}

}
