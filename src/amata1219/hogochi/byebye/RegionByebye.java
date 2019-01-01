package amata1219.hogochi.byebye;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class RegionByebye {

	private static RegionByebye rb;

	public static final int DEFAULT_VALUE = 250;

	private HashMap<String, Long> sales = new HashMap<>();
	private Clipboard ne, se, sw, nw;

	private RegionByebye(){

	}

	public static void load(){
		rb = new RegionByebye();

		HogochiByebye plugin = HogochiByebye.getPlugin();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("Regions");
		if(section != null)
			section.getKeys(false).forEach(id -> rb.sales.put(id, plugin.getConfig().getLong("Regions." + id)));

		rb.ne = read(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_ne.schematic"));
		rb.se = read(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_se.schematic"));
		rb.sw = read(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_sw.schematic"));
		rb.nw = read(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_nw.schematic"));

		/*
		 * try{
			rb.ne = ClipboardFormat.SCHEMATIC.load(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_ne.schematic"));
			rb.se = ClipboardFormat.SCHEMATIC.load(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_se.schematic"));
			rb.sw = ClipboardFormat.SCHEMATIC.load(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_sw.schematic"));
			rb.nw = ClipboardFormat.SCHEMATIC.load(new File(HogochiByebye.getPlugin().getDataFolder() + File.separator + "flat_nw.schematic"));
		}catch(IOException e){
			e.printStackTrace();
		}
		 */
	}

	private static Clipboard read(File file){
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try(ClipboardReader reader = format.getReader(new FileInputStream(file))){
			return reader.read();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void save(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		rb.sales.forEach((k, v) -> plugin.getConfig().set("Regions." + k, v));

		plugin.saveConfig();
		plugin.reloadConfig();
	}

	public static RegionByebye getInstance(){
		return rb;
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

		Region r1 = alongX ? cpm.combine(table.getDirection(5), table.getDirection(14)) : cpm.combine(table.getDirection(5), table.getDirection(6));
		Region r2 = alongX ? cpm.combine(table.getDirection(6), table.getDirection(15)) : cpm.combine(table.getDirection(14), table.getDirection(15));

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
			Region region = cpm.getRegion(direction);
			ProtectedRegion r = Util.createProtectedRegion(adminRegion ? IdType.ADMIN : IdType.USER, region);

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

		Point min = cpm.getRegion(cpm.getDirections(pr).get(0)).getMin();

		int x = min.getX(), z = min.getZ();

		switch(cpm.getDirectionNumberTable().getMainDirection()){
		case NORTH_EAST:
			paste(rb.ne, x, z);
			//rb.ne.paste(Util.getMainFlat()), new Vector(min.getX(), 255, min.getZ()), false, true, (Transform) null);
			break;
		case NORTH_WEST:
			paste(rb.nw, x, z);
			//rb.nw.paste(Util.getMainFlat()), new Vector(min.getX(), 255, min.getZ()), false, true, (Transform) null);
			break;
		case SOUTH_EAST:
			paste(rb.se, x, z);
			//rb.se.paste(Util.getMainFlat()), new Vector(min.getX(), 255, min.getZ()), false, true, (Transform) null);
			break;
		case SOUTH_WEST:
			paste(rb.sw, x, z);
			//rb.sw.paste(Util.getMainFlat()), new Vector(min.getX(), 255, min.getZ()), false, true, (Transform) null);
			break;
		default:
			break;
		}

		//rb.flat.paste(Util.getMainFlat()), new Vector(min.getX(), 255, min.getZ()), false, true, (Transform) null);
		//1: bedrock, 2~61: dirt, 62: grass, 63~255: air
	}

	private static void paste(Clipboard clipboard, int x, int z){
		try(EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(Util.getMainFlat(), -1)){
			Operation operation = new ClipboardHolder(clipboard)
					.createPaste(session)
					.to(BlockVector3.at(x, 255, z))
					.ignoreAirBlocks(false)
					.build();

			Operations.complete(operation);
		} catch (WorldEditException e) {
			e.printStackTrace();
		}
	}

	public static boolean isExistProtectedRegion(int x, int z){
		Compartment cpm = new Compartment(x, z);

		Region region = cpm.getRegion(x, z);
		if(region == null)
			return false;

		return region.isProtected();
	}

	public static ProtectedRegion getProtectedRegion(int x, int z){
		for(ProtectedRegion region : WorldGuard.getInstance().getPlatform().getRegionContainer().get(Util.getMainFlat()).getApplicableRegions(BlockVector3.at(x, 255, z))){
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
		LocalPlayer local = HogochiByebye.getPlugin().getWorldGuardPlugin().wrapPlayer(player);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query  = container.createQuery();
		return query.testState(new com.sk89q.worldedit.util.Location(new BukkitWorld(location.getWorld()), location.getBlockX(), location.getBlockY(), location.getBlockZ()), local, Flags.BUILD);
		//return !HogochiByebye.getPlugin().getWorldGuardPlugin().canBuild(player, location);
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

	public static int getNeedTickets(ProtectedRegion region){
		if(isAdminRegion(region))
			return 0;

		int i = DEFAULT_VALUE;

		if(is25x50(region) || is50x25(region))
			return i * 2;
		else if(is50x50(region))
			return i * 4;
		else
			return i;
	}

	public static boolean isAdminRegion(ProtectedRegion region){
		return region.getId().startsWith(IdType.ADMIN.getString());
	}

}
