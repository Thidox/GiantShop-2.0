package nl.giantit.minecraft.GiantShop.core.Commands;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class check {
	
	public static void check(Player player, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		perm perms = perm.Obtain();
		config conf = config.Obtain();
		if(perms.has(player, "giantshop.shop.check")) {
			db DB = db.Obtain();
			int itemID;
			Integer itemType = -1;
			
			if(!args[1].matches("[0-9]+:[0-9]+")) {
				try {
					itemID = Integer.parseInt(args[1]);
					itemType = -1;
				}catch(NumberFormatException e) {
					ItemID key = iH.getItemIDByName(args[1]);
					if(key != null) {
						itemID = key.getId();
						itemType = key.getType();
					}else{
						Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "itemNotFound"));
						return;
					}
				}catch(Exception e) {
					if(conf.getBoolean("GiantShop.global.debug") == true) {
						GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
						GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
					}

					Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "unknown"));
					return;
				}
			}else{
				try {
					String[] data = args[1].split(":");
					itemID = Integer.parseInt(data[0]);
					itemType = Integer.parseInt(data[1]);
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "check");

					Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "syntaxError", data));
					return;
				}catch(Exception e) {
					if(conf.getBoolean("GiantShop.global.debug") == true) {
						GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
						GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
					}

					Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "unknown"));
					return;
				}
			}
			itemType = itemType == 0 ? -1 : itemType;
			
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("perStack");
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("stock");
			fields.add("shops");
			
			HashMap<String, HashMap<String, String>> where = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> d = new HashMap<String, String>();
			d.put("type", "INT");
			d.put("value", String.valueOf(itemID));
			where.put("itemID", d);
			d.put("value", String.valueOf(itemType));
			where.put("type", d);
			
			ArrayList<HashMap<String, String>> resSet = DB.select(fields).where(where, true).execQuery();
			if(resSet.size() == 1) {
				
			}else{
				Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "noneOrMoreResults"));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "check");

			Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
	public static void check(CommandSender sender, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		config conf = config.Obtain();
		
	}
}
