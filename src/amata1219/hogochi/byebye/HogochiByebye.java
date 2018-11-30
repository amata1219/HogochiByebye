package amata1219.hogochi.byebye;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.ryanhamshire.GriefPrevention.GriefPreventionX;

public class HogochiByebye extends JavaPlugin implements CommandExecutor {

	private static HogochiByebye plugin;

	private WorldGuardPlugin worldGuard;
	private GriefPreventionX griefPreventionX;

	private BukkitTask task;

	public void onEnable(){
		plugin = this;

		saveDefaultConfig();

		Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");
		if(worldGuard == null)
			throw new NullPointerException("[HogochiByebye] WorldGuard is not exist!");

		this.worldGuard = (WorldGuardPlugin) worldGuard;

		Plugin griefPreventionX = getServer().getPluginManager().getPlugin("GriefPreventionX");
		if(griefPreventionX == null)
			throw new NullPointerException("[HogochiByebye] GriefPreventionX is not exist!");

		this.griefPreventionX = (GriefPreventionX) griefPreventionX;

		RegionByebye.load();
		ClaimByebye.load();

		task = new BukkitRunnable(){

			@Override
			public void run(){
				RegionByebye.save();
				ClaimByebye.save();
			}

		}.runTaskTimer(this, 0, 36000);

		getCommand("rgview").setExecutor(this);
	}

	@Override
	public void onDisable(){
		task.cancel();

		RegionByebye.save();
		ClaimByebye.save();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "ゲーム内から実行して下さい。");
			return true;
		}

		if(args.length == 0){
			Player player = (Player) sender;
			Location loc = player.getLocation();
			Compartment cpm = new Compartment(loc.getBlockX(), loc.getBlockZ());
			for(Direction direction : Direction.values()){
				Region region = cpm.getRegion(direction);
				System.out.println("REGION: " + region.getMin().getX() + ", " + region.getMin().getZ() + ", " + region.getMax().getX() + ", " + region.getMax().getZ());
				player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMin().getX(), 62, region.getMin().getZ()), Material.GOLD_BLOCK, (byte) 0);
				player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMax().getX(), 62, region.getMax().getZ()), Material.GOLD_BLOCK, (byte) 0);

				new BukkitRunnable(){

					@Override
					public void run(){
						player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMin().getX(), 62, region.getMin().getZ()), Material.GRASS, (byte) 0);
						player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMax().getX(), 62,region.getMax().getZ()), Material.GRASS, (byte) 0);
					}

				}.runTaskLater(this, 300);
			}

			player.sendMessage(ChatColor.AQUA + "DISPLAY PROTECTED REGIONS");
			return true;
		}else if(args[0].equalsIgnoreCase("pr")){
		Player player = (Player) sender;
		Location loc = player.getLocation();
		Compartment cpm = new Compartment(loc.getBlockX(), loc.getBlockZ());
		for(Direction direction : Direction.values()){
			Region region = cpm.getRegion(direction);
			if(!region.isProtected()){
				player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMin().getX(), 62, region.getMin().getZ()), Material.GOLD_BLOCK, (byte) 0);
				player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMax().getX(), 62, region.getMax().getZ()), Material.GOLD_BLOCK, (byte) 0);

				new BukkitRunnable(){

					@Override
					public void run(){
						player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMin().getX(), 62, region.getMin().getZ()), Material.GRASS, (byte) 0);
						player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), region.getMax().getX(), 62, region.getMax().getZ()), Material.GRASS, (byte) 0);
					}

				}.runTaskLater(this, 300);
				continue;
			}

			ProtectedRegion pr = region.getProtectedRegion();
			BlockVector min = pr.getMinimumPoint();
			BlockVector max = pr.getMaximumPoint();

			player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), min.getBlockX(), 62, min.getBlockZ()), Material.IRON_BLOCK, (byte) 0);
			player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), max.getBlockX(), 62, max.getBlockZ()), Material.IRON_BLOCK, (byte) 0);

			new BukkitRunnable(){

				@Override
				public void run(){
					player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), min.getBlockX(), 62, min.getBlockZ()), Material.GRASS, (byte) 0);
					player.sendBlockChange(new Location(Bukkit.getWorld("main_flat"), max.getBlockX(), 62, max.getBlockZ()), Material.GRASS, (byte) 0);
				}

			}.runTaskLater(this, 300);
		}

		player.sendMessage(ChatColor.AQUA + "DISPLAY PROTECTED REGIONS");
		}else if(args[0].equalsIgnoreCase("remove")){
			worldGuard.getRegionManager(Bukkit.getWorld("main_flat")).getRegions().values().forEach(region -> {
				if(region.getId().startsWith("user_"))
					Util.removeProtectedRegion(region);
			});
		}

		return true;
	}

	public static HogochiByebye getPlugin(){
		return plugin;
	}

	public WorldGuardPlugin getWorldGuardPlugin(){
		return worldGuard;
	}

	public GriefPreventionX getGriefPreventionX(){
		return griefPreventionX;
	}

}
