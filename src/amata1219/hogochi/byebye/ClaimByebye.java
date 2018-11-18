package amata1219.hogochi.byebye;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.api.ClaimManager;
import me.ryanhamshire.GriefPrevention.claims.Claim;

public class ClaimByebye implements ClaimByebyeAPI {

	private HashMap<String, Long> sales = new HashMap<>();

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
	}

	public void unload(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		sales.forEach((k, v) -> plugin.getConfig().set("Claims." + k, String.valueOf(v)));

		plugin.saveConfig();
		plugin.reloadConfig();
	}

	public ClaimManager getClaimManager(){
		return HogochiByebye.getPlugin().getGriefPreventionX().getGriefPreventionXApi().getClaimManager();
	}

	@Override
	public void buy(Player player, Claim claim) {
		getClaimManager().sellClaim(claim, player, 0);

	}

	@Override
	public void sell(Claim claim, long price) {
		sales.put(String.valueOf(claim.getID()), price);
	}

	@Override
	public void withdrawSale(Claim claim){
		sales.remove(String.valueOf(claim.getID()));
	}

	@Override
	public boolean isExistClaimByLocation(Location location) {
		return getClaimManager().getClaimAt(location) != null;
	}

	@Override
	public Claim getClaim(Location location) {
		return getClaimManager().getClaimAt(location);
	}

	@Override
	public boolean isOwner(Player player, Claim claim) {
		return claim.getOwnerUUID(true).equals(player.getUniqueId());
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
