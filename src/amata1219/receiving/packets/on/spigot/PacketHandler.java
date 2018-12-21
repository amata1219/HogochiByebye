package amata1219.receiving.packets.on.spigot;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import amata1219.hogochi.byebye.HogochiByebye;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketHandler extends ChannelDuplexHandler {

	private Player p;

	public PacketHandler(final Player p) {
		this.p = p;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutChat")){
			if(p.getWorld().getName().equals("main_flat")){
				ItemStack item = p.getInventory().getItemInMainHand();
				if(item != null && item.getType() == Material.STICK){
					Object a = Reflection.getFieldValueT(HogochiByebye.getInjector().getPacketPlayOutChat_a(), msg);
					String text = (String) HogochiByebye.getInjector().toPlainText().invoke(a);

					if(text.equals("No one has claimed this block."))
						return;
				}
			}
		}

		super.write(ctx, msg, promise);
	}

	@Override
	public void channelRead(ChannelHandlerContext c, Object m) throws Exception {
		super.channelRead(c, m);
	}

	public Player getPlayer(){
		return p;
	}

}
