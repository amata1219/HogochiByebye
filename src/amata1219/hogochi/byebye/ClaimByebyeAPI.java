package amata1219.hogochi.byebye;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.claims.Claim;

public interface ClaimByebyeAPI {

	boolean isCBEnable(World world);

	boolean isCBEnable(String worldName);

	void buy(Player player, Claim claim);

	void sell(Claim claim);

	boolean isExistClaimByLocation(Location location);

	boolean isOwner(Player player, Claim claim);

	boolean isBuyable(Claim claim);

	boolean isSellable(Claim claim);

	boolean isExistCBSign(Location location);

	boolean isCBSign(Sign sign);

	long getPrice(Sign sign);

	boolean canPlaceCBSign(Block block, BlockFace face);

	void setCBSign(Block block, BlockFace face, long price);

	void removeCBSign(Block block);

}
