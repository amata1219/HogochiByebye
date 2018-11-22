package amata1219.hogochi.byebye;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionByebye implements RegionByebyeAPI {

	private HashMap<String, Long> sales = new HashMap<>();

	private final int roadWidth = 10;
	private final int regionWidth = 50;

	public RegionByebye(){
		load();
	}

	public void load(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		for(String key : plugin.getConfig().getKeys(false)){
			if(!key.equals("Regions"))
				continue;

			for(String id : plugin.getConfig().getKeys(false)){
				sales.put(id, plugin.getConfig().getLong(id));
			}
		}
	}

	public RegionManager getRegionManager(){
		return HogochiByebye.getPlugin().getWorldGuardPlugin().getRegionManager(Bukkit.getWorld("main_flat"));
	}

	public void unload(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		sales.forEach((k, v) -> plugin.getConfig().set("Regions." + k, String.valueOf(v)));

		plugin.saveConfig();
		plugin.reloadConfig();
	}

	@Override
	public void buy(Player player, ProtectedRegion region){
		region.getOwners().removeAll();
		region.getMembers().removeAll();
		region.getOwners().addPlayer(player.getUniqueId());

		sales.remove(region.getId());
	}

	@Override
	public void sell(ProtectedRegion region, long price){
		sales.put(region.getId(), price);
	}

	@Override
	public void withdrawSale(ProtectedRegion region){
		sales.remove(region.getId());
	}

	@Override
	public ProtectedRegion combineLargeRegions(Player player, ProtectedRegion region, ProtectedRegion target){
		Location loc = toLoc(region.getMinimumPoint(), true);
		boolean[] minus = isMinus(loc);
		Location location = correct(loc, minus);
		DefaultDomain owners = region.getOwners(), members = region.getMembers();
		Map<Flag<?>, Object> flags = region.getFlags();
		removeRegion(region);
		removeRegion(target);
		ProtectedRegion newRegion = createRegion(player, getMainCorners(getMainAddress(location)), minus);
		newRegion.setOwners(owners);
		newRegion.setMembers(members);
		newRegion.setFlags(flags);

		return newRegion;
	}

	@Override
	public ProtectedRegion combineSmallRegions(Player player, ProtectedRegion region, ProtectedRegion target){
		int n = regionWidth / 2 - 1;
		Location rl = toLoc(region.getMinimumPoint(), true);
		boolean[] rminus = isMinus(rl);
		Location rloc = correct(rl, rminus);
		int[] corners = getMainCorners(getMainAddress(rloc));
		int[] rsc = getSubCorners(abs(rloc.getBlockX()), abs(rloc.getBlockZ()), corners);
		int ra = getSubAddress(rsc[0], rsc[1], corners[0] + n, corners[1] + n);
		Location tl = toLoc(target.getMinimumPoint(), false);
		Location tloc = correct(tl, isMinus(tl));
		int[] tsc = getSubCorners(abs(tloc.getBlockX()), abs(tloc.getBlockZ()), getMainCorners(getMainAddress(tloc)));
		int ta = getSubAddress(tsc[0], tsc[1], corners[0] + n, corners[1] + n);
		DefaultDomain owners = region.getOwners(), members = region.getMembers();
		Map<Flag<?>, Object> flags = region.getFlags();
		removeRegion(region);
		removeRegion(target);
		ProtectedRegion newRegion = null;
		if(ra == 1){
			if(ta == 2 || ta == 3){
				newRegion = createRegion(player, new int[]{rsc[0], rsc[1], tsc[0] + n, tsc[1] + n}, rminus);
			}
		}else if(ra == 2){
			if(ta == 1){
				newRegion = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}else if(ta == 4){
				newRegion = createRegion(player, new int[]{rsc[0], rsc[1], tsc[0] + n, tsc[1] + n}, rminus);
			}
		}else if(ra == 3){
			if(ta == 1){
				newRegion = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}else if(ta == 4){
				newRegion = createRegion(player, new int[]{rsc[0], rsc[1], tsc[0] + n, tsc[1] + n}, rminus);
			}
		}else if(ra == 4){
			if(ta == 2){
				newRegion = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}else if(ta == 3){
				newRegion = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}
		}
		if(newRegion != null){
			newRegion.setOwners(owners);
			newRegion.setMembers(members);
			newRegion.setFlags(flags);
		}

		return newRegion;
	}

	@Override
	public ProtectedRegion[] splitLargeRegion(Player player, ProtectedRegion region, boolean isX){
		Location minl = toLoc(region.getMinimumPoint(), true);
		boolean[] minMinus = isMinus(minl);
		Location minLoc = correct(minl, minMinus);
		int[] corners = getMainCorners(getMainAddress(minLoc));
		int[] minLocs = new int[]{corners[0], corners[1]};
		int[] maxLocs = new int[]{corners[2], corners[3]};
		DefaultDomain owners = region.getOwners(), members = region.getMembers();
		Map<Flag<?>, Object> flags = region.getFlags();
		removeRegion(region);
		int n = regionWidth - 1;
		int sn = regionWidth / 2 - 1;
		ProtectedRegion newRegion1 = null;
		ProtectedRegion newRegion2 = null;
		if(isX){
			newRegion1 = createRegion(player, new int[]{minLocs[0], minLocs[1], minLocs[0] + n, minLocs[1] + sn}, minMinus);
			newRegion2 = createRegion(player, new int[]{maxLocs[0] - n, maxLocs[1] - sn, maxLocs[0], maxLocs[1]}, minMinus);
		}else{
			newRegion1 = createRegion(player, new int[]{minLocs[0], minLocs[1], minLocs[0] + sn, minLocs[1] + n}, minMinus);
			newRegion2 = createRegion(player, new int[]{maxLocs[0] - sn, maxLocs[1] - n, maxLocs[0], maxLocs[1]}, minMinus);
		}
		newRegion1.setOwners(owners);
		newRegion1.setMembers(members);
		newRegion1.setFlags(flags);
		newRegion2.setOwners(owners);
		newRegion2.setMembers(members);
		newRegion2.setFlags(flags);

		return new ProtectedRegion[]{newRegion1, newRegion2};
	}

	@Override
	public ProtectedRegion[] splitSmallRegion(Player player, ProtectedRegion region, boolean setAdminRegion){
		//boolean isX = abs(abs(region.getMaximumPoint().getBlockX()) - abs(region.getMinimumPoint().getBlockX())) == claimWidth / 2 - 1;
		boolean isX = is25x50(region);
		Location minl = toLoc(region.getMinimumPoint(), true);
		boolean[] minMinus = isMinus(minl);
		Location minLoc = correct(minl, minMinus);
		Location maxl = toLoc(region.getMaximumPoint(), true);
		boolean[] maxMinus = isMinus(maxl);
		Location maxLoc = correct(maxl, maxMinus);
		int[] minsc = getSubCorners(abs(minLoc.getBlockX()), abs(minLoc.getBlockZ()), getMainCorners(getMainAddress(minLoc)));
		int[] maxsc = getSubCorners(abs(maxLoc.getBlockX()), abs(maxLoc.getBlockZ()), getMainCorners(getMainAddress(maxLoc)));
		DefaultDomain owners = region.getOwners(), members = region.getMembers();
		Map<Flag<?>, Object> flags = region.getFlags();
		removeRegion(region);
		ProtectedRegion newRegion1 = null;
		ProtectedRegion newRegion2 = null;
		if(setAdminRegion){
			if(isX){
				newRegion1 = createRegion("admin_" + System.nanoTime(), player, new int[]{minsc[0], minsc[1], minsc[2], minsc[3]}, minMinus);
				newRegion2 = createRegion("admin_" + System.nanoTime(), player, new int[]{maxsc[0], maxsc[1], maxsc[2], maxsc[3]}, maxMinus);
			}else{
				newRegion1 = createRegion("admin_" + System.nanoTime(), player, new int[]{minsc[0], minsc[1], minsc[2], minsc[3]}, minMinus);
				newRegion2 = createRegion("admin_" + System.nanoTime(), player, new int[]{maxsc[0], maxsc[1], maxsc[2], maxsc[3]}, maxMinus);
			}
		}else{
			if(isX){
				newRegion1 = createRegion(player, new int[]{minsc[0], minsc[1], minsc[2], minsc[3]}, minMinus);
				newRegion2 = createRegion(player, new int[]{maxsc[0], maxsc[1], maxsc[2], maxsc[3]}, maxMinus);
			}else{
				newRegion1 = createRegion(player, new int[]{minsc[0], minsc[1], minsc[2], minsc[3]}, minMinus);
				newRegion2 = createRegion(player, new int[]{maxsc[0], maxsc[1], maxsc[2], maxsc[3]}, maxMinus);
			}
		}

		newRegion1.setOwners(owners);
		newRegion1.setMembers(members);
		newRegion1.setFlags(flags);
		newRegion2.setOwners(owners);
		newRegion2.setMembers(members);
		newRegion2.setFlags(flags);

		return new ProtectedRegion[]{newRegion1, newRegion2};
	}

	@Override
	public boolean isExistRegionByLocation(Location location){
		for(ProtectedRegion region  :getRegionManager().getApplicableRegions(location)){
			if(region.getId().startsWith("mainflatroad_"))
				continue;
			else
				return true;
		}
		return false;
	}

	@Override
	public ProtectedRegion getProtectedRegion(Location location){
		for(ProtectedRegion region : getRegionManager().getApplicableRegions(location)){
			if(region.getId().startsWith("mainflatroad_"))
				continue;
			else
				return region;
		}

		return null;
	}

	@Override
	public boolean isOwner(Player player, ProtectedRegion region){
		return region.getOwners().getUniqueIds().contains(player.getUniqueId());
	}

	@Override
	public boolean canBuild(Player player, Location location){
		return !HogochiByebye.getPlugin().getWorldGuardPlugin().canBuild(player, location);
	}

	@Override
	public boolean isBuyable(ProtectedRegion region){
		return sales.containsKey(region.getId()) || isAdminRegion(region);
	}

	@Override
	public boolean isSellable(ProtectedRegion region){
		return !sales.containsKey(region.getId());
	}

	//is X x Z
	@Override
	public boolean is50x50(ProtectedRegion region){
		return getWidth(region) == 50 && getDepth(region) == 50;
	}

	@Override
	public boolean is25x50(ProtectedRegion region){
		return getWidth(region) == 25 && getDepth(region) == 50;
	}

	@Override
	public boolean is50x25(ProtectedRegion region){
		return getWidth(region) == 50 && getDepth(region) == 25;
	}

	@Override
	public boolean is25x25(ProtectedRegion region){
		return getWidth(region) == 25 && getDepth(region) == 25;
	}

	//X
	public int getWidth(ProtectedRegion region){
		BlockVector s = region.getMinimumPoint(), l = region.getMaximumPoint();
		return abs(l.getBlockX()) - abs(s.getBlockX());
	}

	//Z
	@Override
	public int getDepth(ProtectedRegion region){
		BlockVector s = region.getMinimumPoint(), l = region.getMaximumPoint();
		return abs(l.getBlockZ()) - abs(s.getBlockZ());
	}

	@Override
	public long getPrice(ProtectedRegion region){
		if(!isBuyable(region))
			return -1;

		if(isAdminRegion(region))
			return -1;

		return sales.get(region.getId());
	}

	@Override
	public boolean isAdminRegion(ProtectedRegion region){
		return region.getId().startsWith("admin_");
	}

	@Override
	public int getSubAddress(Location location){
		return getSubAddress(location.getBlockX(), location.getBlockZ());
	}

	@Override
	public int getSubAddress(int x, int z){
		int[] mainCorners = getMainAddress(x, z);
		int n = regionWidth / 2 - 1;
		int cx = mainCorners[0] + n;
		int cz = mainCorners[1] + n;
		return getSubAddress(x, z, cx, cz);
	}

	/*public void create(Player player, Location loc){
		boolean[] minus = isMinus(loc);
		int[] corners = getSubCorners(abs(loc.getBlockX()), abs(loc.getBlockZ()), getMainCorners(getMainAddress(loc)));
		createRegion(player, corners, minus);
	}*/

	public Location toLocation(int x, int z, boolean isMin){
		return new Location(Bukkit.getWorld("main_flat"), x, isMin ? 0 : 255, z);
	}

	public Location toLoc(BlockVector v, boolean isZero){
		return new Location(Bukkit.getWorld("main_flat"), v.getX(), isZero ? 0 : 255, v.getZ());
	}

	public Location correct(Location loc, boolean[] minus){
		boolean a = minus[0], b = minus[1];
		if(a && b){
			loc.setX(loc.getX() + 1);
			loc.setZ(loc.getZ() + 1);
		}else if(a && !b){
			loc.setX(loc.getX() + 1);
		}else if(!a && b){
			loc.setZ(loc.getZ() + 1);
		}
		return loc;
	}

	public int[] toLocation(BlockVector v){
		return new int[]{v.getBlockX(), v.getBlockZ()};
	}

	public Vector toVector(Location loc){
		return  BukkitUtil.toVector(loc);
	}

	public BlockVector toBlockVector(Location loc){
		return BukkitUtil.toVector(loc.getBlock());
	}

	public String createID(int[] corners){
		return corners[0] + "," + corners[1] + "," + corners[2] + "," + corners[3];
	}

	public ProtectedRegion getRegion(Location loc){
		ApplicableRegionSet set = getRegionManager().getApplicableRegions(loc);
		if(set.size() == 0){
			return null;
		}
		for(ProtectedRegion region : set.getRegions()){
			return region;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public void visible(Player player, ProtectedRegion region){
		BlockVector sb = region.getMinimumPoint();
		Location s = new Location(Bukkit.getWorld("main_flat"), sb.getX(), 62, sb.getZ());
		BlockVector lb = region.getMaximumPoint();
		Location l = new Location(Bukkit.getWorld("main_flat"), lb.getX(), 62, lb.getZ());
		player.sendBlockChange(s, Material.IRON_BLOCK, (byte) 0);
		player.sendBlockChange(l, Material.IRON_BLOCK, (byte) 0);
	}

	public ProtectedRegion createRegion(Player player, int[] corners){
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(createID(corners), toBlockVector(toLocation(corners[0], corners[1], true)), toBlockVector(toLocation(corners[2], corners[3], false)));
		region.getOwners().addPlayer(player.getUniqueId());
		getRegionManager().addRegion(region);
		visible(player, region);
		return region;
	}

	public ProtectedRegion createRegion(Player player, int[] crs, boolean[] minus){
		boolean a = minus[0], b = minus[1];
		if(a && b){
			crs[0]++;
			crs[2]++;
			crs[1]++;
			crs[3]++;
		}else if(a && !b){
			crs[0]++;
			crs[2]++;
		}else if(!a && b){
			crs[1]++;
			crs[3]++;
		}
		int[] corners = minmax(setMinus(crs, minus));
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(createID(corners), toBlockVector(toLocation(corners[0], corners[1], true)), toBlockVector(toLocation(corners[2], corners[3], false)));
		region.getOwners().addPlayer(player.getUniqueId());
		getRegionManager().addRegion(region);
		visible(player, region);
		return region;
	}

	public ProtectedRegion createRegion(String id, Player player, int[] crs, boolean[] minus){
		boolean a = minus[0], b = minus[1];
		if(a && b){
			crs[0]++;
			crs[2]++;
			crs[1]++;
			crs[3]++;
		}else if(a && !b){
			crs[0]++;
			crs[2]++;
		}else if(!a && b){
			crs[1]++;
			crs[3]++;
		}
		int[] corners = minmax(setMinus(crs, minus));
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, toBlockVector(toLocation(corners[0], corners[1], true)), toBlockVector(toLocation(corners[2], corners[3], false)));
		region.getOwners().addPlayer(player.getUniqueId());
		getRegionManager().addRegion(region);
		visible(player, region);
		return region;
	}

	public void removeRegion(ProtectedRegion region){
		getRegionManager().removeRegion(region.getId());
	}

	@Override
	public int[] getMainAddress(Location loc){
		return getMainAddress(loc.getBlockX(), loc.getBlockZ());
	}

	@Override
	public int[] getMainAddress(int x, int z){
		int m = roadWidth / 2 + 1;
		int n = roadWidth + regionWidth;
		return new int[]{(abs(x) - m) / n, (abs(z) - m) / n};
	}

	public int[] getMainCorners(int[] address){
		int x = address[0], z = address[1];
		int m = roadWidth / 2;
		return new int[]{(roadWidth + regionWidth) * x + m, (roadWidth + regionWidth) * z + m, (roadWidth + regionWidth) * x + m + regionWidth - 1, (roadWidth + regionWidth) * z + m + regionWidth - 1};
	}

	public int getSubAddress(int x, int z, int cx, int cz){
		int i = 1;
		if(x > cx)i += 1;
		if(z > cz)i += 2;
		return i;
	}

	public int[] getSubCorners(int x, int z, int[] mainCorners){
		//minx, minz, maxx, maxz
		int n = regionWidth / 2 - 1;
		int cx = mainCorners[0] + n;
		int cz = mainCorners[1] + n;
		switch(getSubAddress(x, z, cx, cz)){
		case 1:
			return set(mainCorners[0], mainCorners[1], cx, cz);
		case 2:
			return set(cx + 1, mainCorners[1], cx + 25, cz);
		case 3:
			return set(mainCorners[0], cz + 1, cx, cz + 25);
		case 4:
			return set(cx + 1, cz + 1, mainCorners[2], mainCorners[3]);
		}
		return null;
	}

	public int[] set(int minx, int minz, int maxx, int maxz){
		return new int[]{minx, minz, maxx, maxz};
	}

	public int[] setMinus(int[] corners, boolean[] minus){
		boolean a = minus[0], b = minus[1];
		if(!a && !b){
			return corners;
		}else if(a && !b){
			corners[0] = -corners[0];
			corners[2] = -corners[2];
		}else if(!a && b){
			corners[1] = -corners[1];
			corners[3] = -corners[3];
		}else if(a && b){
			corners[0] = -corners[0];
			corners[1] = -corners[1];
			corners[2] = -corners[2];
			corners[3] = -corners[3];
		}
		return corners;
	}

	public int[] minmax(int[] corners){
		int minx = corners[0];
		int minz = corners[1];
		int maxx = corners[2];
		int maxz = corners[3];
		if(minx > maxx){
			corners[0] = maxx;
			corners[2] = minx;
		}
		if(minz > maxz){
			corners[1] = maxz;
			corners[3] = minz;
		}
		return corners;
	}

	public int abs(int i){
		return Math.abs(i);
	}

	public boolean[] isMinus(Location loc){
		return new boolean[]{isMinus(loc.getX()), isMinus(loc.getZ())};
	}

	public boolean isMinus(double d){
		return d < 0;
	}

}
