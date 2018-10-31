package amata1219.hogochi.byebye;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.claims.Claim;

public interface ClaimByebyeAPI {

	void buy(Player player, Claim claim);

	void sell(Claim claim, long price);

	boolean isExistClaimByLocation(Location location);

	Claim getClaim(Location location);

	boolean isOwner(Player player, Claim claim);

	boolean isBuyable(Claim claim);

	boolean isSellable(Claim claim);

	long getPrice(Claim claim);

}
