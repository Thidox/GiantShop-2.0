package nl.giantit.minecraft.GiantShop.Listeners;

import nl.giantit.minecraft.giantcore.Misc.Heraut;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;;

public class PlayerListener implements Listener {
	
	private GiantShop plugin;
	private config conf = config.Obtain();
	
	public PlayerListener(GiantShop plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e) {
		if(e.getPlayer().isOp()) {
			if(plugin.isOutOfDate() && conf.getBoolean("GiantShop.Updater.broadcast")) {
				plugin.scheduleAsyncDelayedTask(new Runnable() {
					@Override
					public void run()
					{
						delayedJoin(e.getPlayer());
					}
				});
			}
		}
	}
	
	public void delayedJoin(Player p) {
		Heraut.say(p.getPlayer(), "&cA new version of GiantShop has been released! You are currently running: " + plugin.getDescription().getVersion() + " while the latest version is: " + plugin.getNewVersion());
	}

}
