package nl.giantit.minecraft.GiantShop.core.Commands.Console;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Logger.Logger;
import nl.giantit.minecraft.GiantShop.core.Logger.LoggerType;
import nl.giantit.minecraft.giantcore.Database.QueryResult;
import nl.giantit.minecraft.giantcore.Database.iDriver;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class remove {
	
	private static config conf = config.Obtain();
	private static iDriver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	
	public static void remove(CommandSender sender, String[] args) {
		int itemID;
		Integer itemType = null;
		String name = "";

		if(args.length >= 2) {
			if(!args[1].matches("[0-9]+:[0-9]+")) {
				try {
					itemID = Integer.parseInt(args[1]);
					itemType = null;
				}catch(NumberFormatException e) {
					ItemID key = iH.getItemIDByName(args[1]);
					if(key != null) {
						itemID = key.getId();
						itemType = key.getType();
					}else{
						Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "itemNotFound"));
						return;
					}
				}catch(Exception e) {
					if(conf.getBoolean("GiantShop.global.debug") == true) {
						GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
						GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
					}

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "unknown"));
					return;
				}
			}else{
				try {
					String[] data = args[1].split(":");
					itemID = Integer.parseInt(data[0]);
					itemType = Integer.parseInt(data[1]);
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "add");

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
					return;
				}catch(Exception e) {
					if(conf.getBoolean("GiantShop.global.debug") == true) {
						GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
						GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
					}

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "unknown"));
					return;
				}
			}

			if(iH.isValidItem(itemID, itemType)) {
				name = iH.getItemNameByID(itemID, itemType);
				ArrayList<String> fields = new ArrayList<String>();
				fields.add("id");
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("itemID", "" + itemID);
				data.put("type", "" + ((itemType == null) ? -1 : itemType));

				DB.select(fields).from("#__items").where(data);
				QueryResult resSet = DB.execQuery();
				if(resSet.size() == 1) { 
					DB.delete("#__items").where(data).updateQuery();
					Heraut.say(sender, "Item " + name + " has been successfully removed from the store!");
					HashMap<String, String> d = new HashMap<String, String>();
					d.put("id", String.valueOf(itemID));
					d.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));
					Logger.Log(LoggerType.REMOVE, "Console", d);
				}else{
					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "noneOrMoreResults"));
				}
			}else{
				Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "itemNotFound"));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "remove");

			Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
	
}
