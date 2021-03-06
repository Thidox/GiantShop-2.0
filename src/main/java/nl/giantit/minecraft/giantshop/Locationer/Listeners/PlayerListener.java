package nl.giantit.minecraft.giantshop.Locationer.Listeners;

import nl.giantit.minecraft.giantcore.Misc.Heraut;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Locationer.Locationer;
import nl.giantit.minecraft.giantshop.core.config;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Giant
 */
public class PlayerListener implements Listener {
	
	private GiantShop plugin;
	private Locationer lH;
	private Set<String> inShop = new HashSet<String>();
	
	public PlayerListener(GiantShop plugin) {
		this.plugin = plugin;
		lH = plugin.getLocHandler();
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(!inShop.contains(player.getName()) && plugin.getLocHandler().inShop(player.getLocation())) {
			inShop.add(player.getName());
			Heraut.say(player, "&3You have just entered a shop &e(&f" + plugin.getLocHandler().getShopName(player.getLocation()).toString() + "&e)&3!");
			return;
		}else if(inShop.contains(player.getName()) && !plugin.getLocHandler().inShop(player.getLocation())) {
			inShop.remove(player.getName());
			Heraut.say(player, "&3You have just left a shop!");
			return;
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if(plugin.getPermHandler().getEngine().has(event.getPlayer(), "giantshop.location.add")) {
			config conf = config.Obtain();
			ItemStack i = event.getItem();
			if(i == null)
				return;
				
			if(i.getTypeId() == conf.getInt("GiantShop.Location.tool.id") && i.getData().getData() == (byte)((int) conf.getInt("GiantShop.Location.tool.type"))) {
				if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
					HashMap<String, Location> point = lH.getPlayerPoints(event.getPlayer());
					point.put("min", event.getClickedBlock().getLocation());

					lH.setPlayerPoint(event.getPlayer(), point);
					Heraut.say(event.getPlayer(), "Successfully set first point to: " 
														+ event.getClickedBlock().getLocation().getBlockX() 
														+ ", " + event.getClickedBlock().getLocation().getBlockY()
														+ ", " + event.getClickedBlock().getLocation().getBlockZ());
				}else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					HashMap<String, Location> point = lH.getPlayerPoints(event.getPlayer());
					point.put("max", event.getClickedBlock().getLocation());

					lH.setPlayerPoint(event.getPlayer(), point);
					Heraut.say(event.getPlayer(), "Successfully set second point to: " 
													+ event.getClickedBlock().getLocation().getBlockX() 
													+ ", " + event.getClickedBlock().getLocation().getBlockY()
													+ ", " + event.getClickedBlock().getLocation().getBlockZ());
				}
			}
		}
	}
}
