package amata1219.hogochi.byebye;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionByebye {

	private static RegionByebye rb;

	private HashMap<String, Long> sales = new HashMap<>();
	private Schematic flat;

	private RegionByebye(){

	}

	public static void load(){
		rb = new RegionByebye();

		HogochiByebye plugin = HogochiByebye.getPlugin();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("Regions");
		if(section == null)
			return;

		section.getKeys(false).forEach(id -> rb.sales.put(id, plugin.getConfig().getLong("Regions." + id)));

		try {
			rb.flat = ClipboardFormat.SCHEMATIC.load(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat.schematic"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		rb.sales.forEach((k, v) -> plugin.getConfig().set("Regions." + k, v));

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

	public static ProtectedRegion combineRegions(ProtectedRegion pr1, ProtectedRegion pr2){
		System.out.println(pr1.getId() + ":");
		System.out.println("min:(" + pr1.getMinimumPoint().getBlockX() + ", " + pr1.getMinimumPoint().getBlockZ() + "), max:("
				+ pr1.getMaximumPoint().getBlockX() + ", " + pr1.getMaximumPoint().getBlockZ() + ")");
		System.out.println(pr2.getId() + ":");
		System.out.println("min:(" + pr2.getMinimumPoint().getBlockX() + ", " + pr2.getMinimumPoint().getBlockZ() + "), max:("
				+ pr2.getMaximumPoint().getBlockX() + ", " + pr2.getMaximumPoint().getBlockZ() + ")");

		Compartment cpm = new Compartment(pr1);

		ProtectedRegion region = null;

		if((is25x50(pr1) && is25x50(pr2)) || (is50x25(pr1) && is50x25(pr2)))
			region = Util.createProtectedRegion(IdType.USER, cpm.getMin(), cpm.getMax());
		else
			region = Util.createProtectedRegion(IdType.USER, cpm.combine(cpm.getDirections(pr1).get(0), cpm.getDirections(pr2).get(0)));

		region.setOwners(pr1.getOwners());
		region.setMembers(pr1.getMembers());
		region.setFlags(pr1.getFlags());

		Util.removeProtectedRegion(pr1, pr2);

		return region;
	}

	public static ProtectedRegion[] splitLargeRegion(ProtectedRegion pr, boolean alongX){
		if(!is50x50(pr))
			return null;

		ProtectedRegion[] regions = new ProtectedRegion[2];

		Compartment cpm = new Compartment(pr);
		DirectionNumberTable table = cpm.getDirectionNumberTable();

		Region r1 = alongX ? cpm.combine(table.getDirection(5), table.getDirection(6)) : cpm.combine(table.getDirection(5), table.getDirection(14));
		Region r2 = alongX ? cpm.combine(table.getDirection(14), table.getDirection(15)) : cpm.combine(table.getDirection(6), table.getDirection(15));

		Point min1 = r1.getMin(), max1 = r1.getMax();
		System.out.println("min:(" + min1.getX() + ", " + min1.getZ() + "), max:(" + max1.getX() + ", " + max1.getZ() + ")");
		Point min2 = r2.getMin(), max2 = r2.getMax();
		System.out.println("min:(" + min2.getX() + ", " + min2.getZ() + "), max:(" + max2.getX() + ", " + max2.getZ() + ")");

		regions[0] = Util.createProtectedRegion(IdType.USER, r1);
		regions[1] = Util.createProtectedRegion(IdType.USER, r2);

		regions[0].setOwners(pr.getOwners());
		regions[1].setOwners(pr.getOwners());;
		regions[0].setMembers(pr.getMembers());
		regions[1].setMembers(pr.getMembers());
		regions[0].setFlags(pr.getFlags());
		regions[1].setFlags(pr.getFlags());

		Util.removeProtectedRegion(pr);

		return regions;
	}

	public static ProtectedRegion[] splitSmallRegion(ProtectedRegion pr, boolean adminRegion){
		if(is25x25(pr) || is50x50(pr))
			return null;

		Compartment cpm = new Compartment(pr);

		ProtectedRegion[] regions = new ProtectedRegion[2];

		for(Direction direction : cpm.getDirections(pr)){
			System.out.println("DirectionCheck: " + direction.name());
			Region region = cpm.getRegion(direction);
			Point min = region.getMin(), max = region.getMax();
			System.out.println("min:(" + min.getX() + ", " + min.getZ() + "), max:(" + max.getX() + ", " + max.getZ() + ")");
			ProtectedRegion r = Util.createProtectedRegion(adminRegion ? IdType.ADMIN : IdType.USER, cpm.getRegion(direction));

			if(!adminRegion){
				r.setOwners(pr.getOwners());
				r.setMembers(pr.getMembers());
				r.setFlags(pr.getFlags());
			}

			if(regions[0] == null)
				regions[0] = r;
			else
				regions[1] = r;
		}

		Util.removeProtectedRegion(pr);

		return regions;
	}

	public static void flatten(ProtectedRegion pr){
		if(!is25x25(pr))
			return;

		Compartment cpm = new Compartment(pr);

		/*AffineTransform transform = new AffineTransform();

		switch(cpm.getDirectionNumberTable().getMainDirection()){
		case NORTH_EAST:
			break;
		case NORTH_WEST:
			transform.rotateX(270);
			break;
		case SOUTH_EAST:
			transform.rotateX(90);
			break;
		case SOUTH_WEST:
			transform.rotateX(180);
			break;
		default:
			return;
		}*/

		Point min = cpm.getRegion(cpm.getDirections(pr).get(0)).getMin();

		rb.flat.paste(new BukkitWorld(Bukkit.getWorld("main_flat")), new Vector(min.getX(), 255, min.getZ()), false, true, (Transform) null);
		//rb.flat.paste(new BukkitWorld(Bukkit.getWorld("main_flat")), new Vector(min.getX(), 255, min.getZ()), false, true, transform);
	}

	public static boolean isExistProtectedRegion(int x, int z){
		Compartment cpm = new Compartment(x, z);

		Region region = cpm.getRegion(x, z);
		if(region == null)
			return false;

		return region.isProtected();
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

}
