package amata1219.hogochi.byebye;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.api.ClaimManager;
import me.ryanhamshire.GriefPrevention.claims.Claim;

public class ClaimByebye implements ClaimByebyeAPI {

	private HashMap<String, Long> sales;

	private ClaimManager manager;

	public ClaimByebye(){
		load();
	}

	public void load(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		for(String key : plugin.getConfig().getKeys(false)){
			if(!key.equals("Claims"))
				continue;

			for(String id : plugin.getConfig().getKeys(false)){
				sales.put(id, plugin.getConfig().getLong(id));
			}
		}

		manager = plugin.getGriefPreventionX().getGriefPreventionXApi().getClaimManager();
	}

	public void unload(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		plugin.getConfig().createSection("Claims", sales);

		plugin.saveConfig();
		plugin.reloadConfig();
	}

	@Override
	public void buy(Player player, Claim claim) {
		manager.sellClaim(claim, player, 0);

	}

	@Override
	public void sell(Claim claim, long price) {
		sales.put(String.valueOf(claim.getID()), price);
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
		return sales.containsKey(String.valueOf(claim.getID()));
	}

	@Override
	public boolean isSellable(Claim claim) {
		return !sales.containsKey(String.valueOf(claim.getID()));
	}

	@Override
	public long getPrice(Claim claim){
		if(!isBuyable(claim))
			return -1;

		return sales.get(String.valueOf(claim.getID()));
	}

}
