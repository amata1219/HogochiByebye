package amata1219.receiving.packets.on.spigot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;

public class PacketInjector {

	private Field EntityPlayer_playerConnection;
	private Class<?> PlayerConnection;
	private Field PlayerConnection_networkManager;

	private Class<?> NetworkManager;
	private Field k;
	private Field m;

	private Class<?> PacketPlayOutChat;
	private Class<?> IChatBaseComponent;
	private Field a;
	private Method toPlainText;

	public PacketInjector() {
		try{
			EntityPlayer_playerConnection = Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"), "playerConnection");
			PlayerConnection = Reflection.getClass("{nms}.PlayerConnection");
			PlayerConnection_networkManager = Reflection.getField(PlayerConnection, "networkManager");
			NetworkManager = Reflection.getClass("{nms}.NetworkManager");
			k = Reflection.getField(NetworkManager, "channel");
			m = Reflection.getField(NetworkManager, "m");

			PacketPlayOutChat = Reflection.getClass("{nms}.PacketPlayOutChat");
			IChatBaseComponent = Reflection.getClass("{nms}.IChatBaseComponent");
			a = Reflection.getField(PacketPlayOutChat, "a");
			a.setAccessible(true);
			toPlainText = IChatBaseComponent.getMethod("toPlainText");
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}

	public Class<?> getIChatBaseComponent(){
		return IChatBaseComponent;
	}

	public Field getPacketPlayOutChat_a(){
		return a;
	}

	public Method toPlainText(){
		return toPlainText;
	}

	public void addPlayer(Player p) {
	try{
		Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
		if(ch.pipeline().get("PacketInjector") == null) {
			PacketHandler h = new PacketHandler(p);
			ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
		}
	}catch(Throwable t) {
		t.printStackTrace();
		}
	}

	public void removePlayer(Player p) {
		try{
			Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
			if(ch.pipeline().get("PacketInjector") != null) {
				ch.pipeline().remove("PacketInjector");
			}
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}

	private Object getNetworkManager(Object ep) throws Exception {
		return Reflection.getFieldValueT(PlayerConnection_networkManager, Reflection.getFieldValueT(EntityPlayer_playerConnection, ep));
	}

	private Channel getChannel(Object networkManager) {
		Channel ch = null;
		try{
			ch = Reflection.getFieldValueT(k, networkManager);
		}catch(Exception e) {
			ch = Reflection.getFieldValueT(m, networkManager);
		}
		return ch;
	}

}