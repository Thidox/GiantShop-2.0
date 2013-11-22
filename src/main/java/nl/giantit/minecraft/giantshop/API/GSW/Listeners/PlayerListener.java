package nl.giantit.minecraft.GiantShop.API.GSW.Listeners;

import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.GiantShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Giant
 */
public class PlayerListener implements Listener {

	private GSWAPI a;
	
	public PlayerListener(GSWAPI a) {
		this.a = a;
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPlayerJoin(final PlayerJoinEvent e) {
		if(a.getPickupQueue().inQueue(e.getPlayer().getName())) {
			GiantShop.getPlugin().getServer().getScheduler().runTaskLater(GiantShop.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					a.getPickupQueue().stalkUser(e.getPlayer().getName());
				}
			}, 10L);
		}
	}
}
