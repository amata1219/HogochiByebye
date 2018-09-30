package amata1219.main.flat.assist;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WGRegionCommand implements TabExecutor{

	private MainFlatAssist plugin;

	private ProtectedRegion select;
	private boolean debug = false;

	public WGRegionCommand(MainFlatAssist plugin){
		this.plugin = plugin;
		debug = true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))return true;
		Player p = (Player) sender;
		RegionAssist r = plugin.getRegionAssist();
		if(args.length == 0){

		}else if(args[0].equals("exist")){
			p.sendMessage(String.valueOf(r.isExistRegion(p.getLocation())));
			return true;
		}else if(args[0].equals("create")){
			r.create(p, p.getLocation());
			p.sendMessage("create");
			return true;
		}else if(args[0].equals("lsplit")){
			r.splitLargeRegion(p, r.getRegion(p.getLocation()), false);
			p.sendMessage("lsplit");
			return true;
		}else if(args[0].equals("ssplit")){
			r.splitSmallRegion(p, r.getRegion(p.getLocation()));
			p.sendMessage("ssplit");
			return true;
		}else if(args[0].equals("visible")){
			r.visible(p, r.getRegion(p.getLocation()));
			p.sendMessage("visible");
			return true;
		}else if(args[0].equals("allvisible")){
			for(ProtectedRegion region : r.getRegionManager().getRegions().values()){
				r.visible(p, region);
			}
			p.sendMessage("allvisible");
			return true;
		}else if(args[0].equals("remove")){
			if(r.getRegion(p.getLocation()) == null)return true;
			r.removeRegion(r.getRegion(p.getLocation()));
			p.sendMessage("remove");
			return true;
		}else if(args[0].equals("allremove")){
			if(!debug)return true;
			for(ProtectedRegion region : r.getRegionManager().getRegions().values()){
				r.removeRegion(region);
			}
			p.sendMessage("allremove");
			return true;
		}else if(args[0].equals("scombine")){
			if(select != null){
				r.combineSmallRegion(p, select, r.getRegion(p.getLocation()));
				p.sendMessage("scombine");
				select = null;
				return true;
			}
			select = r.getRegion(p.getLocation());
			p.sendMessage("sselect");
			return  true;
		}else if(args[0].equals("lcombine")){
			if(select != null){
				r.combineLargeRegion(p, select, r.getRegion(p.getLocation()));
				p.sendMessage("lcombine");
				select = null;
				return true;
			}
			select = r.getRegion(p.getLocation());
			p.sendMessage("lselect");
			return  true;
		}
		return false;
	}

}