/*
 * 本プラグインの著作権は、amata1219(Twitter@amata1219)に帰属します。
 * また、本プラグインの二次配布、改変使用、自作発言を禁じます。
 */

package amata1219.hogochi.byebye;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import amata1219.hypering.economy.Database;
import amata1219.receiving.packets.on.spigot.PacketInjector;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class HogochiByebye extends JavaPlugin implements Listener {

	private static HogochiByebye plugin;

	private static PacketInjector injector;

	private WorldGuardPlugin worldGuard;
	private GriefPrevention griefPrevention;

	private BukkitTask task;

	public void onEnable(){
		plugin = this;

		saveDefaultConfig();

		Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");
		if(worldGuard == null)
			throw new NullPointerException("[HogochiByebye] WorldGuard is not exist!");

		this.worldGuard = (WorldGuardPlugin) worldGuard;

		Plugin griefPrevention = getServer().getPluginManager().getPlugin("GriefPrevention");
		if(griefPrevention == null)
			throw new NullPointerException("[HogochiByebye] GriefPrevention is not exist!");

		this.griefPrevention = (GriefPrevention) griefPrevention;

		RegionByebye.load();
		ClaimByebye.load();

		task = new BukkitRunnable(){

			@Override
			public void run(){
				RegionByebye.save();
				ClaimByebye.save();
			}

		}.runTaskTimer(this, 0, 36000);

		injector = new PacketInjector();

		for(Player player : getServer().getOnlinePlayers())
			injector.addPlayer(player);

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable(){

		for(Player player : getServer().getOnlinePlayers())
			injector.removePlayer(player);

		task.cancel();

		HandlerList.unregisterAll((JavaPlugin) this);

		RegionByebye.save();
		ClaimByebye.save();
	}

	public static HogochiByebye getPlugin(){
		return plugin;
	}

	public static PacketInjector getInjector(){
		return injector;
	}

	public WorldGuardPlugin getWorldGuardPlugin(){
		return worldGuard;
	}

	public GriefPrevention getGriefPrevention(){
		return griefPrevention;
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e){
		injector.addPlayer(e.getPlayer());
	}

	@EventHandler
	public void onDelete(PlayerCommandPreprocessEvent e){
		if(e.isCancelled())
			return;

		Player player = e.getPlayer();
		String message = e.getMessage();

		if(message.startsWith("abandonclaim") || message.startsWith("deleteclaim")){
			Claim claim = ClaimByebye.getClaim(player.getLocation());
			if(claim == null)
				return;

			if(!player.isOp() && player.getUniqueId().equals(claim.ownerID))
				return;

			Bukkit.getPluginManager().callEvent(new ClaimDeletedEvent(player, claim));
			ClaimByebye.withdrawSale(ClaimByebye.getClaim(player.getLocation()));
		}else if(message.startsWith("abandonallclaims") || message.startsWith("deleteallclaims")){
			for(Claim claim : HogochiByebye.getPlugin().getGriefPrevention().dataStore.getPlayerData(player.getUniqueId()).getClaims()){
				Bukkit.getPluginManager().callEvent(new ClaimDeletedEvent(player, claim));
				ClaimByebye.withdrawSale(claim);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if(e.getHand() != EquipmentSlot.HAND)
			return;

		if(e.getItem() == null || e.getItem().getType() != Material.STICK)
			return;

		Player player = e.getPlayer();
		if(!player.getWorld().getName().equals("main_flat"))
			return;

		if(e.getClickedBlock() == null)
			return;

		Location loc = e.getClickedBlock().getLocation();
		Compartment cpm = new Compartment(loc.getBlockX(), loc.getBlockZ());
		Region rg = cpm.getRegion(loc.getBlockX(), loc.getBlockZ());
		if(rg == null || !rg.isProtected())
			return;

		ProtectedRegion pr = rg.getProtectedRegion();
		if(pr == null)
			return;

		World world = Bukkit.getWorld("main_flat");

		Point min = rg.getMin();

		loc.setX(min.getX());
		loc.setZ(min.getZ());
		loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

		Util.displayPoint(player, loc);

		Point max = rg.getMax();

		loc.setX(max.getX());
		loc.setZ(max.getZ());
		loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

		Util.displayPoint(player, loc);

		if(RegionByebye.is25x25(pr)){
			loc.setX(Util.applyMinus(min.getAbsoluteX() + Util.REGION_ONE_SIDE - 1, Util.isUnderZero(min.getX())));
			loc.setZ(min.getZ());
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);

			loc.setX(min.getX());
			loc.setZ(Util.applyMinus(min.getAbsoluteZ() + Util.REGION_ONE_SIDE - 1, Util.isUnderZero(min.getZ())));
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);
		}else if(RegionByebye.is25x50(pr)){
			loc.setX(Util.applyMinus(min.getAbsoluteX() + Util.COMPARTMENT_ONE_SIDE - 1, Util.isUnderZero(min.getX())));
			loc.setZ(min.getZ());
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);

			loc.setX(min.getX());
			loc.setZ(Util.applyMinus(min.getAbsoluteZ() + Util.REGION_ONE_SIDE - 1, Util.isUnderZero(min.getZ())));
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);
		}else if(RegionByebye.is50x25(pr)){
			loc.setX(Util.applyMinus(min.getAbsoluteX() + Util.REGION_ONE_SIDE - 1, Util.isUnderZero(min.getX())));
			loc.setZ(min.getZ());
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);

			loc.setX(min.getX());
			loc.setZ(Util.applyMinus(min.getAbsoluteZ() + Util.COMPARTMENT_ONE_SIDE - 1, Util.isUnderZero(min.getZ())));
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);
		}else{
			loc.setX(Util.applyMinus(min.getAbsoluteX() + Util.COMPARTMENT_ONE_SIDE - 1, Util.isUnderZero(min.getX())));
			loc.setZ(min.getZ());
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);

			loc.setX(min.getX());
			loc.setZ(Util.applyMinus(min.getAbsoluteZ() + Util.COMPARTMENT_ONE_SIDE - 1, Util.isUnderZero(min.getZ())));
			loc.setY(world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1);

			Util.displayPoint(player, loc);
		}

		if(pr.getId().startsWith(IdType.ADMIN.getString())){
			player.sendMessage(ChatColor.AQUA + "That block has been protected by an administrator.");
			player.sendMessage(ChatColor.AQUA + "  25x25=625");//1250, 2500
			player.sendMessage(ChatColor.AQUA + "  Need tickets: " + RegionByebye.STONE);
		}else{
			UUID uuid = pr.getOwners().getUniqueIds().iterator().next();

			player.sendMessage(ChatColor.AQUA + "That block has been protected by " + Bukkit.getOfflinePlayer(uuid).getName() + ".");
			player.sendMessage(ChatColor.AQUA + "  " + (RegionByebye.is25x25(pr) ? "25x25=625" : (RegionByebye.is50x50(pr) ? "50x50=2500" : (RegionByebye.is25x50(pr) ? "25x50=1250" : "50x25=1250"))));

			String days = new SimpleDateFormat("dd").format(System.currentTimeMillis() - Database.getHyperingEconomyAPI().getLastPlayed(uuid));
			if(days.startsWith("0"))
				days = days.substring(1);

			player.sendMessage(ChatColor.AQUA + "  Last login: " + days + " days ago.");

			if(RegionByebye.isBuyable(pr))
				player.sendMessage(ChatColor.AQUA + "  Need money: ¥" + RegionByebye.getPrice(pr));
		}

	}

}
