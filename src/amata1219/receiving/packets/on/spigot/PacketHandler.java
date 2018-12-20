package amata1219.receiving.packets.on.spigot;

import org.bukkit.entity.Player;

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
		if(!msg.getClass().getSimpleName().equals("PacketPlayOutChat")){
			super.write(ctx, msg, promise);
			return;
		}

		if(!p.getWorld().getName().equals("main_flat")){
			super.write(ctx, msg, promise);
			return;
		}

		Object a = Reflection.getFieldValueT(HogochiByebye.getInjector().getPacketPlayOutChat_a(), msg);
		String text = (String) HogochiByebye.getInjector().getPlainText().invoke(a);

		if(text.equals("§bNo one has claimed this block."))
			return;

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
