package amata1219.hogochi.byebye;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Point {

	private int x, z;

	public Point(int x, int z){
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getAbsoluteX(){
		return Util.abs(x);
	}

	public void setX(int x) {
		this.x = x;
	}

	public boolean isMinusX(){
		return x < 0;
	}

	public int getZ() {
		return z;
	}

	public int getAbsoluteZ(){
		return Util.abs(z);
	}

	public void setZ(int z) {
		this.z = z;
	}

	public boolean isMinusZ(){
		return z < 0;
	}

	public Location getLocation(boolean isMax){
		return new Location(Bukkit.getWorld("main_flat"), x, isMax ? 255 : 0, z);
	}

}
