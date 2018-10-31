package amata1219.hogochi.byebye;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.api.ClaimManager;
import me.ryanhamshire.GriefPrevention.claims.Claim;

public class ClaimByebye implements ClaimByebyeAPI {

	private List<String> claimList;

	private ClaimManager manager;

	public ClaimByebye(){
		load();
	}

	public void load(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		claimList = plugin.getConfig().getStringList("ClaimList");

		manager = plugin.getGriefPreventionX().getGriefPreventionXApi().getClaimManager();
	}

	public void unload(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		plugin.getConfig().set("ClaimList", claimList);
		plugin.saveConfig();
		plugin.reloadConfig();
	}

	@Override
	public void buy(Player player, Claim claim) {
		manager.sellClaim(claim, player, 0);

	}

	@Override
	public void sell(Claim claim) {
		claimList.add(String.valueOf(claim.getID()));
	}

	@Override
	public boolean isExistClaimByLocation(Location location) {
		return manager.getClaimAt(location) != null;
	}

	@Override
	public Claim getClaim(Location location) {
		return manager.getClaimAt(location);
	}

	@Override
	public boolean isOwner(Player player, Claim claim) {
		return claim.ownerID.equals(player.getUniqueId());
	}


	@Override
	public boolean isBuyable(Claim claim) {
		return claimList.contains(String.valueOf(claim.getID()));
	}

	@Override
	public boolean isSellable(Claim claim) {
		return !claimList.contains(String.valueOf(claim.getID()));
	}

}
