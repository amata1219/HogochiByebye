package amata1219.hogochi.byebye;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.api.ClaimManager;
import me.ryanhamshire.GriefPrevention.claims.Claim;

public class ClaimByebye implements ClaimByebyeAPI {

	private List<String> claimList, signs;

	private World main;
	private ClaimManager manager;

	public ClaimByebye(){
		load();
	}

	public void load(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		claimList = plugin.getConfig().getStringList("ClaimList");

		signs = plugin.getConfig().getStringList("RegionSigns");
		signs.forEach(sign -> {
			if(!isExistCBSign(Util.toLocation(sign)))
				signs.remove(sign);
		});

		main = HogochiByebye.getPlugin().getServer().getWorld("world");

		manager = plugin.getGriefPreventionX().getGriefPreventionXApi().getClaimManager();
	}

	public void unload(){
		HogochiByebye plugin = HogochiByebye.getPlugin();

		plugin.getConfig().set("ClaimList", claimList);
		plugin.getConfig().set("ClaimSigns", signs);
		plugin.saveConfig();
		plugin.reloadConfig();
	}

	@Override
	public boolean isCBEnable(World world){
		return this.main.equals(world);
	}

	@Override
	public boolean isCBEnable(String worldName){
		return this.main.getName().equals(worldName);
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

	@Override
	public boolean isExistCBSign(Location location){
		return signs.contains(Util.toString(location));
	}

	@Override
	public boolean isCBSign(Sign sign){
		String[] lines = sign.getLines();

		if(lines.length < 2)
			return false;

		return lines[0].equals("[SellClaim]") && Util.isNumber(lines[1]);
	}

	@Override
	public long getPrice(Sign sign){
		return Long.valueOf(sign.getLines()[1]);
	}

	@Override
	public boolean canPlaceCBSign(Block block, BlockFace face){
		Block slide = block.getLocation().add(face.getModX(), face.getModY(), face.getModZ()).getBlock();
		return slide != null && slide.getType() == Material.AIR;
	}

	@Override
	public void setCBSign(Block block, BlockFace face, long price){
		Block slide = block.getLocation().add(face.getModX(), face.getModY(), face.getModZ()).getBlock();
		if(face == BlockFace.UP || face == BlockFace.DOWN)
			slide.setType(Material.WALL_SIGN);
		else
			slide.setType(Material.SIGN_POST);

		Sign sign = (Sign) slide;
		sign.setLine(0, "[SellClaim]");
		sign.setLine(1, String.valueOf(price));

		org.bukkit.material.Sign msign = new org.bukkit.material.Sign(slide.getType());
		msign.setFacingDirection(face);
		sign.setData(msign);

		sign.update();

		signs.add(Util.toString(slide.getLocation()));
	}

	@Override
	public void removeCBSign(Block block){
		signs.remove(Util.toString(block.getLocation()));
	}

}
