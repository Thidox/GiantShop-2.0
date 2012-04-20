package nl.giantit.minecraft.GiantShop.API.stock.Events;

import nl.giantit.minecraft.GiantShop.API.stock.core.itemStock;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Giant
 */
public class StockUpdateEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private Player p;
	private itemStock iS;
	private StockUpdateType t;
	
	public static enum StockUpdateType {
		INCREASE,
		DECREASE
	}
	
	public StockUpdateEvent(Player p, itemStock iS, StockUpdateType t) {
		this.p = p;
		this.iS = iS;
		this.t = t;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public itemStock getItemStock() {
		return iS;
	}
	
	public StockUpdateType getUpdateType() {
		return t;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
