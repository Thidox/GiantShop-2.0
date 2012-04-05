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

/**
 *
 * @author Giant
 */
public class Locationer {

	private GiantShop plugin;
	private Indaface slHandle;
	private config conf;
	private ArrayList<Indaface> shops;
	private List<String> worlds, allow;
	private chat chat;
	private console console;

	public Locationer(GiantShop plugin) {
		this.plugin = plugin;
		this.conf = config.Obtain();
		if(conf.getString("GiantShop.Location.useWorldEdit").equalsIgnoreCase("WorldEdit"))
			this.slHandle = new WorldEditLoc(plugin);
		else
			this.slHandle = new PlainJane(plugin);

		shops = slHandle.getShops();
		worlds = conf.getStringList("GiantShop.Location.protect.Worlds.protected");
		allow = conf.getStringList("GiantShop.Location.protect.Worlds.allowed");
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
	
	public void print(String[] args) {
		int perPag = (conf.getInt("GiantShop.Location.perPage") > 0) ? conf.getInt("GiantShop.Location.perPage") : 5;
		int pag;
		if(args.length >= 3) {
			try {
				pag = Integer.valueOf(args[2].toString());
			}catch(Exception e) {
				pag = 1;
			}
		}else if(args.length >= 2) {
			try {
				pag = Integer.valueOf(args[1].toString());
			}catch(Exception e) {
				pag = 1;
			}
		}else
			pag = 1;
		
		int curPag = (pag > 0) ? pag : 1;
		int itemCount = shops.size();

		int pages = ((int)Math.ceil((double)itemCount / (double)perPag) < 1) ? 1 : (int)Math.ceil((double)itemCount / (double)perPag);
		int start = (curPag * perPag) - perPag;

		if(shops.isEmpty()) {
			Heraut.say("&d[&f" + conf.getString("GiantShop.global.name") + "&d]&c Sorry no shops yet :(");
			return;
		}else if(curPag > pages) {
			Heraut.say("&d[&f" + conf.getString("GiantShop.global.name") + "&d]&c My shop list only has " + pages + " pages!!");
			return;
		}else {
			Heraut.say("&d[&f" + conf.getString("GiantShop.global.name") + "&d]&f Showing available shops. Page &e" + curPag + "&f/&e" + pages);
			for(int i = start; i < (((start + perPag) > itemCount) ? itemCount : (start + perPag)); i++) {
				Indaface shop = shops.get(i);
				
				Heraut.say("&eID: &f" + shop.getID() + " &eName: &f" + shop.getName() + " &eWorld: &f" + shop.getWorldName() + " &eMinX: &f"
							+ (shop.getLocation()).get(0).getBlockX()	+ " &eMinY: &f"	+ (shop.getLocation()).get(0).getBlockY() + " &eMinZ: &f"
							+ (shop.getLocation()).get(0).getBlockZ() + " &eMaxX: &f" + (shop.getLocation()).get(1).getBlockX() + " &eMaxY: &f"
							+ (shop.getLocation()).get(1).getBlockY() + " &eMaxZ: &f" + (shop.getLocation()).get(1).getBlockZ());
			}
		}
		/*Heraut.say("[" + conf.getString("GiantShop.global.name") + "] Showing available shops");
		for(Indaface shop : shops) {
			Heraut.say("ID: " + shop.getID() + " Name: " + shop.getName() + " World: " + shop.getWorldName() + " MinX: " + (shop.getLocation()).get(0).getBlockX() + " MinY: "
					+ (shop.getLocation()).get(0).getBlockY() + " MinZ: " + (shop.getLocation()).get(0).getBlockZ() + " MaxX: " + (shop.getLocation()).get(1).getBlockX() + " MaxY: "
					+ (shop.getLocation()).get(1).getBlockY() + " MaxZ: " + (shop.getLocation()).get(1).getBlockZ());
		}*/
	}

	public boolean addShop(Player player, ArrayList<Location> loc, String name) {
		Indaface shop = slHandle.newShop(loc, name, player.getWorld().getName());
		if(shop != null) {
			shops.add(shop);
			return true;
		}

		return false;
		//shops.add(new Indaface(plugin, locs));
	}

	public boolean removeShop(int id) {
		int i = 0;
		for(Indaface shop : shops) {
			if(shop.getID() == id) {
				slHandle.remove(id);
				shops.remove(i);
				return true;
			}
			i++;
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

		return "unknown";
	}
	
	public Boolean canUse(Player player) {
		String world = player.getWorld().getName();
		String name = conf.getString("GiantShop.global.name");
		if(Misc.contains(worlds, world)) {
			// Should we protect on a per world basis? And is this world included in the protection?
			if(!inShop(player.getLocation()) && !plugin.getPermMan().has(player, "giantshop.location.override")) {
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
