package nl.giantit.minecraft.GiantShop.Listeners;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class playerListener implements Listener {
	GiantShop plugin;
	ArrayList<Player> inShop = new ArrayList<Player>();
	
	public playerListener(GiantShop plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Heraut.savePlayer(player);
		
		if(!inShop.contains(player) && plugin.loc.inShop(player.getLocation())) {
			inShop.add(player);
			Heraut.say("&3You have just entered a shop &e(&f" + plugin.loc.getShopName(player.getLocation()).toString() + "&e)&3!");
			return;
		}else if(inShop.contains(player) && !plugin.loc.inShop(player.getLocation())) {
			inShop.remove(player);
			Heraut.say("&3You have just left a shop!");
			return;
		}
	}
}
