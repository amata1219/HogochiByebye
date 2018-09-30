package amata1219.main.flat.assist;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class MainFlatAssist extends JavaPlugin{

	private static MainFlatAssist plugin;

	private WorldGuardPlugin wg;

	private HashMap<String, TabExecutor> commands;

	private RegionAssist regionAssist;

	@Override
	public void onEnable(){
		plugin = this;
		Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
		if(wg == null){
			throw new NullPointerException("[MainFlatAssist] WorldGuard is not exist!");
		}
		this.wg = (WorldGuardPlugin) wg;
		commands = new HashMap<String, TabExecutor>();
		commands.put("wgregion", new WGRegionCommand(plugin));
		regionAssist = new RegionAssist(plugin);
	}

	@Override
	public void onDisable(){

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	public static MainFlatAssist getPlugin(){
		return plugin;
	}

	public WorldGuardPlugin getWorldGuardPlugin(){
		return wg;
	}

	public RegionAssist getRegionAssist(){
		return regionAssist;
	}

}
