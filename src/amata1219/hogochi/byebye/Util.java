package amata1219.hogochi.byebye;

import org.bukkit.Location;

public class Util {

	public static String toString(Location location){
		return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
	}

	public static Location toLocation(String str){
		String[] locs = str.split(",");
		return new Location(HogochiByebye.getPlugin().getServer().getWorld(locs[0]), Integer.valueOf(locs[1]),
				Integer.valueOf(locs[2]), Integer.valueOf(locs[3]));
	}

	public static boolean isNumber(String str){
		try{
			Long.valueOf(str);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
