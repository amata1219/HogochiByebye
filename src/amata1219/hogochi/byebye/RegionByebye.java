package amata1219.hogochi.byebye;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionByebye {

	private static RegionByebye rb;

	private HashMap<String, Long> sales = new HashMap<>();

	private RegionByebye(){

	}

	public static void load(){
		rb = new RegionByebye();

		HogochiByebye plugin = HogochiByebye.getPlugin();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("Regions");
		if(section == null)
			return;

		section.getKeys(false).forEach(id -> rb.sales.put(id, plugin.getConfig().getLong("Regions." + id)));
	}

	public static void save(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		rb.sales.forEach((k, v) -> plugin.getConfig().set("Regions." + k, String.valueOf(v)));

		plugin.saveConfig();
		plugin.reloadConfig();
	}

	public static void buy(Player player, ProtectedRegion region){
		region.getOwners().removeAll();
		region.getMembers().removeAll();

		region.getOwners().addPlayer(player.getUniqueId());

		rb.sales.remove(region.getId());
	}

	public static void sell(ProtectedRegion region, long price){
		rb.sales.put(region.getId(), price);
	}

	public static void withdraw(ProtectedRegion region){
		rb.sales.remove(region.getId());
	}

	public static ProtectedRegion combineRegions(Player player, ProtectedRegion pr1, ProtectedRegion pr2){
		BlockVector minVector = pr1.getMinimumPoint();
		BlockVector maxVector = pr2.getMaximumPoint();

		Compartment cpm = new Compartment(minVector.getBlockX(), minVector.getBlockZ());

		Region r1 = cpm.getRegion(minVector.getBlockX(), minVector.getBlockZ());
		Region r2 = cpm.getRegion(maxVector.getBlockX(), maxVector.getBlockZ());

		System.out.println("MinMin: " + minVector.getBlockX() + " : " + minVector.getBlockZ());
		System.out.println("MinMax: " + pr1.getMaximumPoint().getBlockX() + " : " + pr1.getMaximumPoint().getBlockZ());
		System.out.println("MinMax: " + pr2.getMinimumPoint().getBlockX() + " : " + pr2.getMinimumPoint().getBlockZ());
		System.out.println("MaxMax: " + maxVector.getBlockX() + " : " + maxVector.getBlockZ());

		Region region = cpm.combine(r1.getDirection(), r2.getDirection());
		System.out.println(r1.getDirection() + " : " + r2.getDirection());

		if(region == null)
			return null;

		DefaultDomain members = pr1.getMembers();
		Map<Flag<?>, Object> flags = pr1.getFlags();

		Util.removeProtectedRegion(pr1);
		Util.removeProtectedRegion(pr2);

		ProtectedRegion protectedRegion = Util.createProtectedRegion(IdType.USER, region);

		protectedRegion.getOwners().addPlayer(player.getUniqueId());

		protectedRegion.setMembers(members);
		protectedRegion.setFlags(flags);

		return protectedRegion;
	}

	public static ProtectedRegion[] splitLargeRegion(Player player, ProtectedRegion pr, boolean alongX){
		BlockVector minVector = pr.getMinimumPoint();

		Compartment cpm = new Compartment(minVector.getBlockX(), minVector.getBlockZ());

		ProtectedRegion[] regions = new ProtectedRegion[2];

		if(alongX){
			regions[0] = Util.createProtectedRegion(IdType.USER, cpm.getRegion(Direction.NORTH_WEST).getMin(), cpm.getRegion(Direction.NORTH_EAST).getMax());
			regions[1] = Util.createProtectedRegion(IdType.USER, cpm.getRegion(Direction.SOUTH_WEST).getMin(), cpm.getRegion(Direction.SOUTH_EAST).getMax());
		}else{
			regions[0] = Util.createProtectedRegion(IdType.USER, cpm.getRegion(Direction.SOUTH_WEST).getMin(), cpm.getRegion(Direction.NORTH_WEST).getMax());
			regions[1] = Util.createProtectedRegion(IdType.USER, cpm.getRegion(Direction.SOUTH_EAST).getMin(), cpm.getRegion(Direction.NORTH_EAST).getMax());
		}

		regions[0].getOwners().addPlayer(player.getUniqueId());
		regions[0].setMembers(pr.getMembers());
		regions[0].setFlags(pr.getFlags());

		regions[1].getOwners().addPlayer(player.getUniqueId());
		regions[1].setMembers(pr.getMembers());
		regions[1].setFlags(pr.getFlags());

		return regions;
	}

	public static ProtectedRegion[] splitSmallRegion(Player player, ProtectedRegion pr, boolean adminRegion){
		BlockVector minVector = pr.getMinimumPoint();
		BlockVector maxVector = pr.getMaximumPoint();

		Compartment cpm = new Compartment(minVector.getBlockX(), minVector.getBlockZ());

		Region r1 = cpm.getRegion(minVector.getBlockX(), minVector.getBlockZ());
		Region r2 = cpm.getRegion(maxVector.getBlockX(), maxVector.getBlockZ());

		DefaultDomain members = pr.getMembers();
		Map<Flag<?>, Object> flags = pr.getFlags();

		Util.removeProtectedRegion(pr);

		ProtectedRegion[] regions = new ProtectedRegion[2];

		regions[0] = Util.createProtectedRegion(adminRegion ? IdType.ADMIN : IdType.USER, r1);
		regions[0].getOwners().addPlayer(player.getUniqueId());
		regions[0].setMembers(members);
		regions[0].setFlags(flags);

		regions[1] = Util.createProtectedRegion(adminRegion ? IdType.ADMIN : IdType.USER, r2);
		regions[1].getOwners().addPlayer(player.getUniqueId());
		regions[1].setMembers(members);
		regions[1].setFlags(flags);

		return regions;
	}

	public static boolean isExistProtectedRegion(int x, int z){
		return new Compartment(x, z).getRegion(x, z).isProtected();
	}

	public static ProtectedRegion getProtectedRegion(int x, int z){
		Location location = Util.toLocation(x, z, true);

		for(ProtectedRegion region : HogochiByebye.getPlugin().getWorldGuardPlugin().getRegionManager(Bukkit.getWorld("main_flat")).getApplicableRegions(location)){
			if(region.getId().startsWith("mainflatroad_"))
				continue;

			return region;
		}

		return null;
	}

	public static boolean isOwner(Player player, ProtectedRegion region){
		return region.getOwners().contains(player.getUniqueId());
	}

	public static boolean canBuild(Player player, Location location){
		return !HogochiByebye.getPlugin().getWorldGuardPlugin().canBuild(player, location);
	}

	public static boolean canBuild(Player player, int x, int z){
		return canBuild(player, Util.toLocation(x, z, true));
	}

	public static boolean isBuyable(ProtectedRegion region){
		return rb.sales.containsKey(region.getId()) || isAdminRegion(region);
	}

	public static boolean isSellable(ProtectedRegion region){
		return !rb.sales.containsKey(region.getId());
	}

	//is X x Z
	public static boolean is50x50(ProtectedRegion region){
		return getWidth(region) == 50 && getDepth(region) == 50;
	}

	public static boolean is25x50(ProtectedRegion region){
		return getWidth(region) == 25 && getDepth(region) == 50;
	}

	public static boolean is50x25(ProtectedRegion region){
		return getWidth(region) == 50 && getDepth(region) == 25;
	}

	public static boolean is25x25(ProtectedRegion region){
		return getWidth(region) == 25 && getDepth(region) == 25;
	}

	//X
	public static int getWidth(ProtectedRegion region){
		int[] sortedX = Util.sortMinMax(Util.abs(region.getMinimumPoint().getBlockX()), Util.abs(region.getMaximumPoint().getBlockX()));
		return sortedX[1] - sortedX[0] + 1;
	}

	//Z
	public static int getDepth(ProtectedRegion region){
		int[] sortedZ = Util.sortMinMax(Util.abs(region.getMinimumPoint().getBlockZ()), Util.abs(region.getMaximumPoint().getBlockZ()));
		return sortedZ[1] - sortedZ[0] + 1;
	}

	public static long getPrice(ProtectedRegion region){
		if(!isBuyable(region))
			return -1;

		if(isAdminRegion(region))
			return -1;

		return rb.sales.get(region.getId());
	}

	public static long getNeedTickets(ProtectedRegion region){
		if(!isAdminRegion(region))
			return -1;

		int i = 160;

		if(is25x50(region) || is50x25(region))
			return i * 2;
		else if(is50x50(region))
			return i * 4;

		return i;
	}

	public static boolean isAdminRegion(ProtectedRegion region){
		return region.getId().startsWith(IdType.ADMIN.getString());
	}

	public static Direction[] getDirections(ProtectedRegion region){
		int x = region.getMinimumPoint().getBlockX();
		int z = region.getMinimumPoint().getBlockZ();

		Direction[] directions = null;
		if(is25x25(region)){
			Compartment cpm = new Compartment(region);
			for(Direction d : Direction.values()){
				Region r = cpm.getRegion(d);
				Point m1 = r.getMin();
				Point m2 = r.getMax();
				System.out.println(d.toString() + ": " + "Min(" + m1.getX() + ", " + m1.getZ() + "), Max(" + m2.getX() + ", " + m2.getZ() + ")");
			}

			directions = new Direction[]{cpm.getRegion(x, z).getDirection()};
		}else if(is50x50(region)){
			directions = Direction.values();
		}
		if(directions != null)
			return directions;

		if(is25x50(region)){
			if(new Compartment(region).getRegion(x, z).getDirection() == Direction.SOUTH_WEST)
				return new Direction[]{Direction.SOUTH_WEST, Direction.NORTH_WEST};
			else
				return new Direction[]{Direction.SOUTH_EAST, Direction.NORTH_EAST};
		}else{
			if(new Compartment(region).getRegion(x, z).getDirection() == Direction.SOUTH_WEST)
				return new Direction[]{Direction.SOUTH_WEST, Direction.SOUTH_EAST};
			else
				return new Direction[]{Direction.NORTH_WEST, Direction.NORTH_EAST};
		}
	}

}
