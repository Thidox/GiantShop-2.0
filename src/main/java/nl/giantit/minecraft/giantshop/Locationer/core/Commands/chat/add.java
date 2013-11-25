package nl.giantit.minecraft.giantshop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.database.query.InsertQuery;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Locationer.Locationer;
import nl.giantit.minecraft.giantshop.core.config;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Giant
 */
public class add {
	
	private static config conf = config.Obtain();
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Locationer lH = GiantShop.getPlugin().getLocHandler();
	
	public static void add(Player player, String[] args) {
		if(perms.has(player, "giantshop.location.add")) {
			HashMap<String, Location> points = lH.getPlayerPoints(player);
			if(points.size() < 2) {
				Heraut.say(player, "You need to set 2 points to add a shop!");
				return;
			}
			
			if(!points.containsKey("min") || !points.containsKey("max")) {
				Heraut.say(player, "Failed to add shop. Invalid points passed!");
				return;
			}
			
			String name = (args.length > 1) ? args[1] : "unkown";
			Location loc1, loc2;
			loc1 = points.get("min");
			loc2 = points.get("max");
			
			if("".equals(name)) {
				Heraut.say(player, "Failed to add shop. Name can not be empty!");
				return;
			}
			
			if(!loc1.getWorld().getName().equals(loc2.getWorld().getName())) {
				Heraut.say(player, "Failed to add shop. Points not in same world!");
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
			
			if(!lH.inShop(l) && !lH.inShop(l2)) {
				Driver DB = GiantShop.getPlugin().getDB().getEngine();

				SelectQuery sQ = DB.select("id").from("#__shops");
				sQ.where("name", name);
				sQ.where("world", loc1.getWorld().getName());
				if(sQ.exec().size() == 0) {
					List<String> fields = new ArrayList<String>();
					fields.add("name");
					fields.add("world");
					fields.add("locMinX");
					fields.add("locMinY");
					fields.add("locMinZ");
					fields.add("locMaxX");
					fields.add("locMaxY");
					fields.add("locMaxZ");
					
					InsertQuery iQ = DB.insert("#__shops");
					iQ.addFields(fields);
					iQ.addRow();
					iQ.assignValue("name", name);
					iQ.assignValue("world", loc1.getWorld().getName());
					iQ.assignValue("locMinX", String.valueOf(minX), InsertQuery.ValueType.RAW);
					iQ.assignValue("locMinY", String.valueOf(minY), InsertQuery.ValueType.RAW);
					iQ.assignValue("locMinZ", String.valueOf(minZ), InsertQuery.ValueType.RAW);
					iQ.assignValue("locMaxX", String.valueOf(maxX), InsertQuery.ValueType.RAW);
					iQ.assignValue("locMaxY", String.valueOf(maxY), InsertQuery.ValueType.RAW);
					iQ.assignValue("locMaxZ", String.valueOf(maxZ), InsertQuery.ValueType.RAW);
					
					iQ.exec();

					HashMap<String, String> data = new HashMap<String, String>();
					data.put("shop", name);
					data.put("world", loc1.getWorld().getName());
					data.put("minX", String.valueOf(minX));
					data.put("minY", String.valueOf(minY));
					data.put("minZ", String.valueOf(minZ));
					data.put("maxX", String.valueOf(maxX));
					data.put("maxY", String.valueOf(maxY));
					data.put("maxZ", String.valueOf(maxZ));

					ArrayList<Location> t = new ArrayList<Location>();
					t.add(l);
					t.add(l2);
					
					lH.addShop(t, name);
					lH.remPlayerPoint(player);
					Heraut.say(player, mH.getMsg(Messages.msgType.ADMIN, "shopAdded", data));
				}else{
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("shop", name);
					data.put("world", loc1.getWorld().getName());

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "shopNameTaken", data));
				}
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
			data.put("command", "loc add");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
}
