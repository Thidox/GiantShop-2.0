package nl.giantit.minecraft.GiantShop.Locationer;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Locationer.AreaReaders.*;
import nl.giantit.minecraft.GiantShop.Locationer.Executors.*;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class Locationer {

	private GiantShop plugin;
	private Indaface slHandle;
	private Indaface wlHandle;
	private config conf;
	private ArrayList<Indaface> shops = new ArrayList<Indaface>();
	private List<String> worlds, allow;
	private chat chat;
	private console console;
	private HashMap<Player, HashMap<String, Location>> points = new HashMap<Player, HashMap<String, Location>>();

	public Locationer(GiantShop plugin) {
		this.plugin = plugin;
		this.conf = config.Obtain();
		
		this.wlHandle = new WorldEditLoc(plugin);
		this.slHandle = new PlainJane(plugin);

		new shopLoader(this.plugin, this);
		worlds = conf.getStringList("GiantShop.Location.protect.Worlds.protected");
		allow = conf.getStringList("GiantShop.Location.protect.Worlds.allowed");
		
		chat = new chat(this.plugin);
		console = new console(this.plugin);
		
		GiantShop.log.log(Level.INFO, "[GiantShopLocation] " + String.valueOf(shops.size()) + (shops.size() == 1 ? " shop" : " shops") + " loaded!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("loc")) {
			if(!(sender instanceof Player)){
				return console.exec(sender, cmd, commandLabel, args);
			}
			
			return chat.exec(sender, cmd, commandLabel, args);
		}
		
		return false;
	}
	
	public void reload() {
		shops = new ArrayList<Indaface>();
		new shopLoader(this.plugin, this);
	}
	
	public HashMap<String, Location> getPlayerPoints(Player p) {
		if(points.containsKey(p))
			return points.get(p);
		
		return new HashMap<String, Location>();
	}
	
	public void setPlayerPoint(Player p, HashMap<String, Location> tmp) {
		points.put(p, tmp);
	}
	
	public void remPlayerPoint(Player p) {
		if(points.containsKey(p))
			points.remove(p);
	}
	
	public Indaface getShop(String name, String world) {
		for(Indaface shop : shops) {
			if(shop.getName().equals(name) && shop.getWorldName().equals(world)) return shop;
		}

		return null;
	}
	
	public ArrayList<Indaface> getShops() {
		return this.shops;
	}

	public boolean addShop(ArrayList<Location> loc, String name) {
		Indaface shop = slHandle.newShop(loc, name);
		if(shop != null) {
			shops.add(shop);
			return true;
		}

		return false;
	}

	public boolean removeShop(String name, String World) {
		int i = 0;
		for(Indaface shop : shops) {
			if(shop.getName().equals(name) && shop.getWorldName().equals(World)) {
				slHandle.remove(name, World);
				shops.remove(i);
				return true;
			}
			i++;
		}
		
		return false;
	}

	public boolean inShop(Location loc) {
		for(Indaface shop : shops) {
			if(shop.inShop(loc)) return true;
		}

		return false;
	}

	public boolean inShop(Location loc, String filter) {
		for(Indaface shop : shops) {
			if(shop.inShop(loc) && !shop.getName().equals(filter)) return true;
		}

		return false;
	}

	public int inShop(Location loc, int useless) {
		for(Indaface shop : shops) {
			if(shop.inShop(loc)) return shop.getID();
		}

		return -1;
	}
	
	public String getShopName(Location loc) {
		for(Indaface shop : shops) {
			if(shop.inShop(loc)) {
				if(!shop.getName().equals("null")) {
					return shop.getName();
				}else{
					return "Nameless shop";
				}
			}
		}

		return "Nameless shop";
	}
	
	public Boolean canUse(Player player) {
		String world = player.getWorld().getName();
		String name = conf.getString("GiantShop.global.name");
		if(Misc.contains(worlds, world)) {
			// Should we protect on a per world basis? And is this world included in the protection?
			if(!inShop(player.getLocation()) && !plugin.getPermHandler().getEngine().has(player, "giantshop.location.override")) {
				// Player not in shop, cancel!
				Heraut.say(player, "&d[&f" + name + "&d]&c You are not in a shop!");
				return false;
			}else{
				// Ok player is in a shop! We can allow him to use the commands now!
				return true;
			}
		}else{
			if(conf.getBoolean("GiantShop.Location.protect.Worlds.disableOffList") && !Misc.contains(allow, world)) {
				// Protect all worlds, but only allow shops on world list
				Heraut.say(player, "&d[&f" + name + "&d]&c The shop is disabled in your world!");
				return false;
			}else{
				// No need to protect this world
				return true;
			}
		}
	}
}
