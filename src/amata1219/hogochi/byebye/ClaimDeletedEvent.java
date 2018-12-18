package amata1219.hogochi.byebye;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.ryanhamshire.GriefPrevention.Claim;

public class ClaimDeletedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private Claim claim;

	public ClaimDeletedEvent(Player player, Claim claim){
		this.player = player;
		this.claim = claim;
	}

	public Player getPlayer(){
		return player;
	}

	public Claim getClaim(){
		return claim;
	}

	@Override
	public HandlerList getHandlers() {
		return null;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

}
