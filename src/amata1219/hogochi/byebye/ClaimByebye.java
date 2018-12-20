package amata1219.hogochi.byebye;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore.NoTransferException;

public class ClaimByebye {

	private static ClaimByebye cb;

	private HashMap<String, Long> sales = new HashMap<>();

	private ClaimByebye(){

	}

	public static void load(){
		cb = new ClaimByebye();

		HogochiByebye plugin = HogochiByebye.getPlugin();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("Claims");
		if(section == null)
			return;

		section.getKeys(false).forEach(id -> cb.sales.put(id, plugin.getConfig().getLong("Claims." + id)));
	}

	public static void save(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		cb.sales.forEach((k, v) -> plugin.getConfig().set("Claims." + k, v));

		plugin.saveConfig();
		plugin.reloadConfig();
	}

	public static void buy(Player player, Claim claim) {
		try {
			HogochiByebye.getPlugin().getGriefPrevention().dataStore.changeClaimOwner(claim, player.getUniqueId());
		} catch (NoTransferException e) {
			e.printStackTrace();
		}
	}

	public static void sell(Claim claim, long price) {
		cb.sales.put(String.valueOf(claim.getID()), price);
	}

	public static void withdrawSale(Claim claim){
		cb.sales.remove(String.valueOf(claim.getID()));
	}

	public static boolean isExistClaim(Location location) {
		return HogochiByebye.getPlugin().getGriefPrevention().dataStore.getClaimAt(location, true, null) != null;
	}

	public static Claim getClaim(Location location) {
		return HogochiByebye.getPlugin().getGriefPrevention().dataStore.getClaimAt(location, true, null);
	}

	public static boolean isOwner(Player player, Claim claim) {
		return claim.ownerID.equals(player.getUniqueId());
	}

	public static  boolean isBuyable(Claim claim) {
		return cb.sales.containsKey(String.valueOf(claim.getID()));
	}

	public static boolean isSellable(Claim claim) {
		return !cb.sales.containsKey(String.valueOf(claim.getID()));
	}

	public static long getPrice(Claim claim){
		if(!isBuyable(claim))
			return -1;

		return cb.sales.get(String.valueOf(claim.getID()));
	}

}
