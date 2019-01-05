package amata1219.hogochi.byebye;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MemberCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "ゲーム内から実行して下さい。");
			return true;
		}

		Player player = (Player) sender;
		if(args.length == 0){
			player.sendMessage(ChatColor.AQUA + "/member [add/remove/list] [player]");
			player.sendMessage(ChatColor.GRAY + "自分の保護地の上に立って実行して下さい。");
			return true;
		}else if(args[0].equalsIgnoreCase("add")){
			if(args.length == 1){
				player.sendMessage(ChatColor.RED + "追加するプレイヤー名を入力して下さい。");
				return true;
			}

			if(!inMainFlat(player)){
				player.sendMessage(ChatColor.RED + "メインフラットで実行して下さい。");
				return true;
			}

			OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
			if(p == null || p.getName() == null){
				player.sendMessage(ChatColor.RED + "指定されたプレイヤーは存在しません。");
				return true;
			}

			Location loc = player.getLocation();
			ProtectedRegion region = RegionByebye.getProtectedRegion(loc.getBlockX(), loc.getBlockZ());
			if(region == null || RegionByebye.isOwner(player, region)){
				player.sendMessage(ChatColor.RED + "自分の保護地の上で実行して下さい。");
				return true;
			}

			UUID uuid = p.getUniqueId();
			if(region.getMembers().contains(uuid)){
				player.sendMessage(ChatColor.RED + "このプレイヤーは既に追加されています。");
				return true;
			}

			region.getMembers().addPlayer(uuid);
			player.sendMessage(ChatColor.AQUA + "メンバーに" + p.getName() + "を追加しました。");
			return true;
		}else if(args[0].equalsIgnoreCase("remove")){
			if(args.length == 1){
				player.sendMessage(ChatColor.RED + "削除するプレイヤー名を入力して下さい。");
				return true;
			}

			if(!inMainFlat(player)){
				player.sendMessage(ChatColor.RED + "メインフラットで実行して下さい。");
				return true;
			}

			OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
			if(p == null || p.getName() == null){
				player.sendMessage(ChatColor.RED + "指定されたプレイヤーは存在しません。");
				return true;
			}

			Location loc = player.getLocation();
			ProtectedRegion region = RegionByebye.getProtectedRegion(loc.getBlockX(), loc.getBlockZ());
			if(region == null || RegionByebye.isOwner(player, region)){
				player.sendMessage(ChatColor.RED + "自分の保護地の上で実行して下さい。");
				return true;
			}

			UUID uuid = p.getUniqueId();
			if(!region.getMembers().contains(uuid)){
				player.sendMessage(ChatColor.RED + "このプレイヤーは追加されていません。");
				return true;
			}

			region.getMembers().removePlayer(uuid);
			player.sendMessage(ChatColor.AQUA + "メンバーから" + p.getName() + "を削除しました。");
			return true;
		}else if(args[0].equalsIgnoreCase("list")){
			if(!inMainFlat(player)){
				player.sendMessage(ChatColor.RED + "メインフラットで実行して下さい。");
				return true;
			}

			Location loc = player.getLocation();
			ProtectedRegion region = RegionByebye.getProtectedRegion(loc.getBlockX(), loc.getBlockZ());
			if(region == null || RegionByebye.isOwner(player, region)){
				player.sendMessage(ChatColor.RED + "自分の保護地の上で実行して下さい。");
				return true;
			}

			DefaultDomain members = region.getMembers();
			player.sendMessage(ChatColor.AQUA + "メンバー一覧(" + members.getUniqueIds().size() + "人)");
			members.getUniqueIds().forEach(id -> player.sendMessage(ChatColor.AQUA + "- " + ChatColor.WHITE + Bukkit.getOfflinePlayer(id).getName()));
			return true;
		}
		return true;
	}

	public boolean inMainFlat(Player player){
		return player.getWorld().getName().equals("main_flat");
	}

}
