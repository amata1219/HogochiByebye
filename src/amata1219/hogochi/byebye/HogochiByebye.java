package amata1219.hogochi.byebye;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.ryanhamshire.GriefPrevention.GriefPreventionX;
import me.ryanhamshire.GriefPrevention.claims.Claim;

public class HogochiByebye extends JavaPlugin {

	private static HogochiByebye plugin;

	private WorldGuardPlugin worldGuard;
	private GriefPreventionX griefPreventionX;

	private RegionByebye regionByebye;
	private ClaimByebye claimByebye;

	@Override
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

		regionByebye = new RegionByebye();
		claimByebye = new ClaimByebye();
	}

	@Override
	public void onDisable(){
		regionByebye.unload();
		claimByebye.unload();
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

	public RegionByebyeAPI getRegionByebyeAPI(){
		return (RegionByebyeAPI) regionByebye;
	}

	public ClaimByebyeAPI getClaimByebyeAPI(){
		return (ClaimByebyeAPI) claimByebye;
	}

	public Claim getClaim(Location location){
		return griefPreventionX.getGriefPreventionXApi().getClaimManager().getClaimAt(location);
	}

}
