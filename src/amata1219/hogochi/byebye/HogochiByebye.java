package amata1219.hogochi.byebye;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.ryanhamshire.GriefPrevention.GriefPreventionX;

public class HogochiByebye extends JavaPlugin {

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
	}

	@Override
	public void onDisable(){
		task.cancel();

		RegionByebye.save();
		ClaimByebye.save();
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
