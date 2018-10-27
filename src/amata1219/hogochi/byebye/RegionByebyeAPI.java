package amata1219.hogochi.byebye;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

interface RegionByebyeAPI {

	boolean isRBEnable(World world);

	boolean isRBEnable(String worldName);

	void buy(Player player, ProtectedRegion region);

	void sell(ProtectedRegion region);

	void combineLargeRegions(Player player, ProtectedRegion region, ProtectedRegion target);

	void combineSmallRegions(Player player, ProtectedRegion region, ProtectedRegion target);

	void splitLargeRegion(Player player, ProtectedRegion region, boolean isAlongX);

	void splitSmallRegion(Player player, ProtectedRegion region);

	boolean isExistRegionByLocation(Location location);

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

	boolean isExistRBSign(Location location);

	boolean isRBSign(Sign sign);

	long getPrice(Sign sign);

	boolean canPlaceRBSign(Block block, BlockFace face);

	void setRBSign(Block block, BlockFace face, long price);

	void removeRBSign(Block block);

}
