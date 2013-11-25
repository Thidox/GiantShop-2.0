package nl.giantit.minecraft.giantshop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;
import nl.giantit.minecraft.giantcore.database.query.UpdateQuery;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Locationer.Locationer;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.config;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class update {
	
	private static config conf = config.Obtain();
	private static Driver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Locationer lH = GiantShop.getPlugin().getLocHandler();
	private static HashMap<Player, HashMap<String, String>> stored = new HashMap<Player, HashMap<String, String>>();
	
	private static void select(Player player, String[] args) {
		String name = null;
		String world = null;

		for(int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-n:")) {
				name = args[i].replaceFirst("-n:", "");
				continue;
			}else if(args[i].startsWith("-w:")) {
				world = args[i].replaceFirst("-w:", "");
				continue;
			}
		}

		if(name != null) {
			if(world == null)
				world = player.getWorld().getName();

			ArrayList<String> fields = new ArrayList<String>();
			fields.add("locMinX");
			fields.add("locMinY");
			fields.add("locMinZ");
			fields.add("locMaxX");
			fields.add("locMaxY");
			fields.add("locMaxZ");

			SelectQuery sQ = DB.select(fields).from("#__shops");
			sQ.where("name", name);
			sQ.where("world", world);
			QueryResult QRes = sQ.exec();
			if(QRes.size() > 0) {
				QueryRow QR = QRes.getRow();
				HashMap<String, String> res = new HashMap<String, String>();
				res.put("name", name);
				res.put("world", world);
				res.put("oname", name);
				res.put("oworld", world);
				res.put("locMinX", QR.getString("locMinX"));
				res.put("locMinY", QR.getString("locMinY"));
				res.put("locMinZ", QR.getString("locMinZ"));
				res.put("locMaxX", QR.getString("locMaxX"));
				res.put("locMaxY", QR.getString("locMaxY"));
				res.put("locMaxZ", QR.getString("locMaxZ"));
				
				stored.put(player, res);
				
				HashMap<String, String>data = new HashMap<String, String>();
				data.put("shop", name);
				data.put("world", world);

				Heraut.say(player, mH.getMsg(Messages.msgType.ADMIN, "shopSelected", data));
			}else{
				HashMap<String, String>data = new HashMap<String, String>();
				data.put("shop", name);
				data.put("world", world);

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "shopNotFound", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "loc update select");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
	
	private static void set (Player player, String[] args) {
		String type = "name";
		String name = stored.get(player).get("name");

		for(int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-t:")) {
				type = args[i].replaceFirst("-t:", "");
				continue;
			}else if(args[i].startsWith("-n:")) {
				name = args[i].replaceFirst("-n:", "");
				continue;
			}
		}

		if(Misc.isEitherIgnoreCase(type, "name", "n")) {
			if(name != null && !name.equals("")) {
				ArrayList<String> fields = new ArrayList<String>();
				fields.add("id");
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("name", name);
				data.put("world", stored.get(player).get("world"));

				SelectQuery sQ = DB.select("id").from("#__shops");
				sQ.where("name", name);
				sQ.where("world", stored.get(player).get("world"));
				if(sQ.exec().size() == 0) {
					HashMap<String, String> p = stored.get(player);
				
					p.put("name", name);

					stored.put(player, p);
					
					data = new HashMap<String, String>();
					data.put("shop", name);
					
					Heraut.say(player, mH.getMsg(Messages.msgType.ADMIN, "nameUpdated", data));
				}else{
					data = new HashMap<String, String>();
					data.put("shop", name);
					data.put("world", stored.get(player).get("world"));

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "shopNameTaken", data));
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "loc update set -t:name");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else if(Misc.isEitherIgnoreCase(type, "world", "w")) {
			HashMap<String, Location> points = lH.getPlayerPoints(player);
			if(points.size() < 2) {
				Heraut.say(player, "You need to set 2 points to update a shops location!");
				return;
			}
			
			if(!points.containsKey("min") || !points.containsKey("max")) {
				Heraut.say(player, "Failed to update location. Invalid points passed!");
				return;
			}
			
			Location loc1, loc2;
			loc1 = points.get("min");
			loc2 = points.get("max");
			
			if(!loc1.getWorld().getName().equals(loc2.getWorld().getName())) {
				Heraut.say(player, "Failed to update location. Points not in same world!");
				return;
			}
			
			double minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
			double minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
			double minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
			double maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
			double maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
			double maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
			
			while (maxX - minX < 4) maxX += 1;
			while (maxY - minY < 2) maxY += 1;
			while (maxZ - minZ < 4) maxZ += 1;
			
			Location l = new Location(loc1.getWorld(), minX, minY, minZ);
			Location l2 = new Location(loc1.getWorld(), maxX, maxY, maxZ);
			
			if(!lH.inShop(l, stored.get(player).get("name")) && !lH.inShop(l2, stored.get(player).get("name"))) {
				HashMap<String, String> p = stored.get(player);
				
				p.put("world", loc1.getWorld().getName());
				p.put("locminx", String.valueOf(minX));
				p.put("locminy", String.valueOf(minY));
				p.put("locminz", String.valueOf(minZ));
				p.put("locmaxx", String.valueOf(maxX));
				p.put("loxmaxy", String.valueOf(maxY));
				p.put("locmaxz", String.valueOf(maxZ));
				
				stored.put(player, p);
				
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("shop", name);
				data.put("world", loc1.getWorld().getName());
				data.put("minX", String.valueOf(minX));
				data.put("minY", String.valueOf(minY));
				data.put("minZ", String.valueOf(minZ));
				data.put("maxX", String.valueOf(maxX));
				data.put("maxY", String.valueOf(maxY));
				data.put("maxZ", String.valueOf(maxZ));
				
				lH.remPlayerPoint(player);
				Heraut.say(player, mH.getMsg(Messages.msgType.ADMIN, "worldUpdated", data));
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("shop", name);
				data.put("world", loc1.getWorld().getName());
				data.put("minX", String.valueOf(minX));
				data.put("minY", String.valueOf(minY));
				data.put("minZ", String.valueOf(minZ));
				data.put("maxX", String.valueOf(maxX));
				data.put("maxY", String.valueOf(maxY));
				data.put("maxZ", String.valueOf(maxZ));

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "locTaken", data));
			}
		}else if(Misc.isEitherIgnoreCase(type, "loc", "l")) {
			HashMap<String, Location> points = lH.getPlayerPoints(player);
			if(points.size() < 2) {
				Heraut.say(player, "You need to set 2 points to update a shops location!");
				return;
			}
			
			if(!points.containsKey("min") || !points.containsKey("max")) {
				Heraut.say(player, "Failed to update location. Invalid points passed!");
				return;
			}
			
			Location loc1, loc2;
			loc1 = points.get("min");
			loc2 = points.get("max");
			
			if(!loc1.getWorld().getName().equals(loc2.getWorld().getName())) {
				Heraut.say(player, "Failed to update location. Points not in same world!");
				return;
			}
			
			double minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
			double minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
			double minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
			double maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
			double maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
			double maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
			
			while (maxX - minX < 4) maxX += 1;
			while (maxY - minY < 2) maxY += 1;
			while (maxZ - minZ < 4) maxZ += 1;
			
			Location l = new Location(loc1.getWorld(), minX, minY, minZ);
			Location l2 = new Location(loc1.getWorld(), maxX, maxY, maxZ);
			
			if(!lH.inShop(l, stored.get(player).get("name")) && !lH.inShop(l2, stored.get(player).get("name"))) {
				HashMap<String, String> p = stored.get(player);
				
				p.put("locminx", String.valueOf(minX));
				p.put("locminy", String.valueOf(minY));
				p.put("locminz", String.valueOf(minZ));
				p.put("locmaxx", String.valueOf(maxX));
				p.put("locmaxy", String.valueOf(maxY));
				p.put("locmaxz", String.valueOf(maxZ));
				
				stored.put(player, p);
				
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("shop", name);
				data.put("world", loc1.getWorld().getName());
				data.put("minX", String.valueOf(minX));
				data.put("minY", String.valueOf(minY));
				data.put("minZ", String.valueOf(minZ));
				data.put("maxX", String.valueOf(maxX));
				data.put("maxY", String.valueOf(maxY));
				data.put("maxZ", String.valueOf(maxZ));
				
				lH.remPlayerPoint(player);
				Heraut.say(player, mH.getMsg(Messages.msgType.ADMIN, "locUpdated", data));
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("shop", name);
				data.put("world", loc1.getWorld().getName());
				data.put("minX", String.valueOf(minX));
				data.put("minY", String.valueOf(minY));
				data.put("minZ", String.valueOf(minZ));
				data.put("maxX", String.valueOf(maxX));
				data.put("maxY", String.valueOf(maxY));
				data.put("maxZ", String.valueOf(maxZ));

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "locTaken", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "loc update set");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
	
	private static void save(Player player, String[] args) {
		HashMap<String, String> tmp = stored.get(player);
		
		String oname = tmp.remove("oname");
		String oworld = tmp.remove("oworld");
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("name", oname);
		data.put("world", oworld);

		UpdateQuery uQ = DB.update("#__shops");
		uQ.set("locMinX", tmp.get("locminx"), UpdateQuery.ValueType.SETRAW);
		uQ.set("locMinY", tmp.get("locminy"), UpdateQuery.ValueType.SETRAW);
		uQ.set("locMinZ", tmp.get("locminz"), UpdateQuery.ValueType.SETRAW);
		uQ.set("locMinX", tmp.get("locmaxx"), UpdateQuery.ValueType.SETRAW);
		uQ.set("locMinY", tmp.get("locmaxy"), UpdateQuery.ValueType.SETRAW);
		uQ.set("locMinZ", tmp.get("locmaxz"), UpdateQuery.ValueType.SETRAW);
		
		uQ.where("name", oname);
		uQ.where("world", oworld);
		uQ.exec();
		
		lH.removeShop(oname, oworld);
		
		double minX = Double.parseDouble(tmp.get("locminx"));
		double minY = Double.parseDouble(tmp.get("locminy"));
		double minZ = Double.parseDouble(tmp.get("locminz"));
		double maxX = Double.parseDouble(tmp.get("locmaxx"));
		double maxY = Double.parseDouble(tmp.get("locmaxy"));
		double maxZ = Double.parseDouble(tmp.get("locmaxz"));
		
		Location l = new Location(GiantShop.getPlugin().getSrvr().getWorld(tmp.get("world")), minX, minY, minZ);
		Location l2 = new Location(GiantShop.getPlugin().getSrvr().getWorld(tmp.get("world")), maxX, maxY, maxZ);
		
		ArrayList<Location> loc = new ArrayList<Location>();
		loc.add(l);
		loc.add(l2);
		
		lH.addShop(loc, tmp.get("name"));
		
		data = new HashMap<String, String>();
		data.put("shop", tmp.get("name"));
		data.put("world", tmp.get("world"));
		data.put("minX", String.valueOf(minX));
		data.put("minY", String.valueOf(minY));
		data.put("minZ", String.valueOf(minZ));
		data.put("maxX", String.valueOf(maxX));
		data.put("maxY", String.valueOf(maxY));
		data.put("maxZ", String.valueOf(maxZ));
		
		stored.remove(player);
		Heraut.say(player, mH.getMsg(Messages.msgType.ADMIN, "shopUpdated", data));
	}
	
	public static void update(Player player, String[] args) {
		if(perms.has(player, "giantshop.location.update")) {
			if(args.length > 1) {
				if(Misc.isEitherIgnoreCase(args[1], "select", "s")) {
					update.select(player, args);
				}else if(args[1].equalsIgnoreCase("set")) {
					if(stored.containsKey(player)) {
						update.set(player, args);
					}else{
						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noShopSelected"));
					}
				}else if(args[1].equalsIgnoreCase("save")) {
					if(stored.containsKey(player)) {
						update.save(player, args);
					}else{
						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noShopSelected"));
					}
				}else if(args[1].equalsIgnoreCase("show")) {
					if(stored.containsKey(player)) {
						HashMap<String, String> data = stored.get(player);
						Heraut.say(player, "Here's the result for shop " + data.get("name") + " in word " + data.get("world") + "!");
						Heraut.say(player, "minX: " + data.get("locminx"));
						Heraut.say(player, "minY: " + data.get("locminy"));
						Heraut.say(player, "minZ: " + data.get("locminz"));
						Heraut.say(player, "maxX: " + data.get("locmaxz"));
						Heraut.say(player, "maxY: " + data.get("locmaxy"));
						Heraut.say(player, "maxZ: " + data.get("locmaxz"));
					}else{
						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noShopSelected"));
					}
				}else{
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "loc update");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "loc update");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "loc update");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
