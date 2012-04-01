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
public class update {
	
	private static config conf = config.Obtain();
	private static db DB = db.Obtain();
	private static perm perms = perm.Obtain();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	
	private static void select(Player player, String item) {
		int itemID;
		Integer itemType = null;
		
		if(!item.matches("[0-9]+:[0-9]+")) {
			try {
				itemID = Integer.parseInt(item);
				itemType = null;
			}catch(NumberFormatException e) {
				ItemID key = iH.getItemIDByName(item);
				if(key != null) {
					itemID = key.getId();
					itemType = key.getType();
				}else{
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
					return;
				}
			}catch(Exception e) {
				if(conf.getBoolean("GiantShop.global.debug") == true) {
					GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
					GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
				}

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "unknown"));
				return;
			}
		}else{
			try {
				String[] data = item.split(":");
				itemID = Integer.parseInt(data[0]);
				itemType = Integer.parseInt(data[1]);
			}catch(NumberFormatException e) {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "add");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				return;
			}catch(Exception e) {
				if(conf.getBoolean("GiantShop.global.debug") == true) {
					GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
					GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
				}

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "unknown"));
				return;
			}
		}
		
		if(iH.isValidItem(itemID, itemType)) {
			String name = iH.getItemNameByID(itemID, itemType);
			
		}
	}
	
	public static void update(Player player, String[] args) {
		if(perms.has(player, "giantshop.admin.update")) {
			if(args.length >= 3) {
				if(args[1].equalsIgnoreCase("select")) {
					update.select(player, args[2]);
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "update");
				
				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "update");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
