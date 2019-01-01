package amata1219.hogochi.byebye;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Region {

	private Direction direction;

	private Point min, max;

	public Region(Direction direction, int minX, int minZ, int maxX, int maxZ){
		this.direction = direction;

		this.min = new Point(minX, minZ);
		this.max = new Point(maxX, maxZ);
	}

	public Region(Direction direction, Point min, Point max){
		this.direction = direction;

		this.min = min;
		this.max = max;
	}

	public Direction getDirection(){
		return direction;
	}

	public Point getMin() {
		return min;
	}

	public void setMin(Point min) {
		this.min = min;
	}

	public Point getMax() {
		return max;
	}

	public void setMax(Point max) {
		this.max = max;
	}

	public boolean isIn(int x, int z){
		return Util.isIn(x, min.getX(), max.getX()) && Util.isIn(z, min.getZ(), max.getZ());
	}

	public boolean isProtected(){
		for(ProtectedRegion region : WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(Bukkit.getWorld("main_flat"))).getApplicableRegions(BlockVector3.at(min.getX(), 255, min.getZ()))){
			if(region.getId().startsWith("mainflatroad_"))
				continue;

			return true;
		}

		return false;
	}

	public ProtectedRegion getProtectedRegion(){
		return RegionByebye.getProtectedRegion(min.getX(), min.getZ());
	}

}
