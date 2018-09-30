package amata1219.main.flat.assist;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

public class RegionAssist {

	private MainFlatAssist plugin;

	private World world;

	private int roadWidth = 10;
	private int claimWidth = 50;

	public RegionAssist(MainFlatAssist plugin){
		this.plugin = plugin;
		world = plugin.getServer().getWorld("world");
	}

	public RegionManager getRegionManager(){
		return plugin.getWorldGuardPlugin().getRegionManager(world);
	}

	public boolean isExistRegion(Location loc){
		return getRegionManager().getApplicableRegions(loc).size() > 0;
	}

	public boolean canBuild(Player player, Location loc){
		return !plugin.getWorldGuardPlugin().canBuild(player, loc);
	}

	public boolean isExistPlayerRegion(Player player, Location loc){
		return isExistRegion(loc) && canBuild(player, loc);
	}

	public void create(Player player, Location loc){
		if(isExistRegion(loc))return;
		boolean[] minus = isMinus(loc);
		int[] corners = getSubCorners(abs(loc.getBlockX()), abs(loc.getBlockZ()), getMainCorners(getMainAddress(loc)));
		createRegion(player, corners, minus);
	}

	public void sell(Player player){

	}
	public Location toLocation(int x, int z, boolean isMin){
		return new Location(world, x, isMin ? 0 : 255, z);
	}

	public Location toLoc(BlockVector v, boolean isZero){
		return new Location(world, v.getX(), isZero ? 0 : 255, v.getZ());
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
		Location s = new Location(world, sb.getX(), 62, sb.getZ());
		BlockVector lb = region.getMaximumPoint();
		Location l = new Location(world, lb.getX(), 62, lb.getZ());
		player.sendBlockChange(s, Material.IRON_BLOCK, (byte) 0);
		player.sendBlockChange(l, Material.IRON_BLOCK, (byte) 0);
	}

	public ProtectedRegion createRegion(Player player, int[] corners){
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(createID(corners), toBlockVector(toLocation(corners[0], corners[1], true)), toBlockVector(toLocation(corners[2], corners[3], false)));
		region.getOwners().addPlayer(player.getUniqueId());
		getRegionManager().addRegion(region);
		visible(player, region);
		Location rloc = toLoc(region.getMinimumPoint(), true);
		System.out.println(rloc.getBlockX() + ", " + rloc.getBlockY() + ", " + rloc.getBlockZ());
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

	public void removeRegion(ProtectedRegion region){
		getRegionManager().removeRegion(region.getId());
	}

	public void combineLargeRegion(Player player, ProtectedRegion region, ProtectedRegion target){
		Location l = toLoc(region.getMinimumPoint(), true);
		boolean[] minus = isMinus(l);
		Location location = correct(l, minus);
		DefaultDomain owners = region.getOwners(), members = region.getMembers();
		Map<Flag<?>, Object> flags = region.getFlags();
		removeRegion(region);
		removeRegion(target);
		ProtectedRegion nr = createRegion(player, getMainCorners(getMainAddress(location)), minus);
		nr.setOwners(owners);
		nr.setMembers(members);
		nr.setFlags(flags);
	}

	public void combineSmallRegion(Player player, ProtectedRegion region, ProtectedRegion target){
		int n = claimWidth / 2 - 1;
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
		ProtectedRegion nr = null;
		if(ra == 1){
			if(ta == 2 || ta == 3){
				nr = createRegion(player, new int[]{rsc[0], rsc[1], tsc[0] + n, tsc[1] + n}, rminus);
			}
		}else if(ra == 2){
			if(ta == 1){
				nr = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}else if(ta == 4){
				nr = createRegion(player, new int[]{rsc[0], rsc[1], tsc[0] + n, tsc[1] + n}, rminus);
			}
		}else if(ra == 3){
			if(ta == 1){
				nr = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}else if(ta == 4){
				nr = createRegion(player, new int[]{rsc[0], rsc[1], tsc[0] + n, tsc[1] + n}, rminus);
			}
		}else if(ra == 4){
			if(ta == 2){
				nr = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}else if(ta == 3){
				nr = createRegion(player, new int[]{tsc[0], tsc[1], rsc[0] + n, rsc[1] + n}, rminus);
			}
		}
		if(nr != null){
			nr.setOwners(owners);
			nr.setMembers(members);
			nr.setFlags(flags);
		}
	}

	public void splitLargeRegion(Player player, ProtectedRegion region, boolean isX){
		Location minl = toLoc(region.getMinimumPoint(), true);
		boolean[] minMinus = isMinus(minl);
		Location minLoc = correct(minl, minMinus);
		int[] corners = getMainCorners(getMainAddress(minLoc));
		int[] minLocs = new int[]{corners[0], corners[1]};
		int[] maxLocs = new int[]{corners[2], corners[3]};
		DefaultDomain owners = region.getOwners(), members = region.getMembers();
		Map<Flag<?>, Object> flags = region.getFlags();
		removeRegion(region);
		int n = claimWidth - 1;
		int sn = claimWidth / 2 - 1;
		ProtectedRegion nr1 = null;
		ProtectedRegion nr2 = null;
		if(isX){
			nr1 = createRegion(player, new int[]{minLocs[0], minLocs[1], minLocs[0] + n, minLocs[1] + sn}, minMinus);
			nr2 = createRegion(player, new int[]{maxLocs[0] - n, maxLocs[1] - sn, maxLocs[0], maxLocs[1]}, minMinus);
		}else{
			nr1 = createRegion(player, new int[]{minLocs[0], minLocs[1], minLocs[0] + sn, minLocs[1] + n}, minMinus);
			nr2 = createRegion(player, new int[]{maxLocs[0] - sn, maxLocs[1] - n, maxLocs[0], maxLocs[1]}, minMinus);
		}
		nr1.setOwners(owners);
		nr1.setMembers(members);
		nr1.setFlags(flags);
		nr2.setOwners(owners);
		nr2.setMembers(members);
		nr2.setFlags(flags);
	}

	public void splitSmallRegion(Player player, ProtectedRegion region){
		boolean isX = abs(abs(region.getMaximumPoint().getBlockX()) - abs(region.getMinimumPoint().getBlockX())) == claimWidth / 2 - 1;
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
		ProtectedRegion nr1 = null;
		ProtectedRegion nr2 = null;
		if(isX){
			nr1 = createRegion(player, new int[]{minsc[0], minsc[1], minsc[2], minsc[3]}, minMinus);
			nr2 = createRegion(player, new int[]{maxsc[0], maxsc[1], maxsc[2], maxsc[3]}, maxMinus);
		}else{
			nr1 = createRegion(player, new int[]{minsc[0], minsc[1], minsc[2], minsc[3]}, minMinus);
			nr2 = createRegion(player, new int[]{maxsc[0], maxsc[1], maxsc[2], maxsc[3]}, maxMinus);
		}
		nr1.setOwners(owners);
		nr1.setMembers(members);
		nr1.setFlags(flags);
		nr2.setOwners(owners);
		nr2.setMembers(members);
		nr2.setFlags(flags);
	}

	public int[] getMainAddress(Location loc){
		return getMainAddress(loc.getBlockX(), loc.getBlockZ());
	}

	public int[] getMainAddress(int x, int z){
		int m = roadWidth / 2 + 1;
		int n = roadWidth + claimWidth;
		return new int[]{(abs(x) - m) / n, (abs(z) - m) / n};
	}

	public int[] getMainCorners(int[] address){
		int x = address[0], z = address[1];
		int m = roadWidth / 2;
		return new int[]{(roadWidth + claimWidth) * x + m, (roadWidth + claimWidth) * z + m, (roadWidth + claimWidth) * x + m + claimWidth - 1, (roadWidth + claimWidth) * z + m + claimWidth - 1};
	}
	private int getSubAddress(int x, int z, int cx, int cz){
		int i = 1;
		if(x > cx)i += 1;
		if(z > cz)i += 2;
		return i;
	}

	public int[] getSubCorners(int x, int z, int[] mainCorners){
		//minx, minz, maxx, maxz
		int n = claimWidth / 2 - 1;
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

	private int[] set(int minx, int minz, int maxx, int maxz){
		return new int[]{minx, minz, maxx, maxz};
	}

	private int[] setMinus(int[] corners, boolean[] minus){
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

	private int[] minmax(int[] corners){
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

	private int abs(int i){
		return Math.abs(i);
	}

	private boolean[] isMinus(Location loc){
		return new boolean[]{isMinus(loc.getX()), isMinus(loc.getZ())};
	}

	private boolean isMinus(double d){
		return d < 0;
	}

}
