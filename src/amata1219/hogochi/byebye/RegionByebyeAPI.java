package amata1219.hogochi.byebye;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public interface RegionByebyeAPI {

	void buy(Player player, ProtectedRegion region);

	void sell(ProtectedRegion region, long price);

	void withdrawSale(ProtectedRegion region);

	ProtectedRegion combineLargeRegions(Player player, ProtectedRegion region, ProtectedRegion target);

	ProtectedRegion combineSmallRegions(Player player, ProtectedRegion region, ProtectedRegion target);

	ProtectedRegion[] splitLargeRegion(Player player, ProtectedRegion region, boolean isAlongX);

	ProtectedRegion[] splitSmallRegion(Player player, ProtectedRegion region, boolean setAdminRegion);

	boolean isExistRegionByLocation(Location location);

	ProtectedRegion getProtectedRegion(Location location);

	boolean isOwner(Player player, ProtectedRegion region);

	boolean canBuild(Player player, Location location);

	boolean isBuyable(ProtectedRegion region);

	boolean isSellable(ProtectedRegion region);

	boolean is50x50(ProtectedRegion region);

	boolean is25x50(ProtectedRegion region);

	boolean is50x25(ProtectedRegion region);

	boolean is25x25(ProtectedRegion region);

	int getWidth(ProtectedRegion region);

	int getDepth(ProtectedRegion region);

	long getPrice(ProtectedRegion region);

	boolean isAdminRegion(ProtectedRegion region);

	int[] getMainAddress(Location location);

	int[] getMainAddress(int x, int z);

	int getSubAddress(Location location);

	int getSubAddress(int x, int z);

}
