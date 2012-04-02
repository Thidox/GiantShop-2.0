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
	private static HashMap<Player, HashMap<String, String>> stored = new HashMap<Player, HashMap<String, String>>();
	
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
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("stock");
			fields.add("perStack");
			fields.add("shops");
			
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("itemID", "" + itemID);
			data.put("type", "" + ((itemType == null) ? -1 : itemType));

			DB.select(fields).from("#__items").where(data);
			ArrayList<HashMap<String, String>> resSet = DB.execQuery();
			if(resSet.size() == 1) {
				HashMap<String, String> res = resSet.get(0);
				HashMap<String, String> tmp = new HashMap<String, String>();
				tmp.put("itemID", String.valueOf(itemID));
				tmp.put("itemType", String.valueOf(itemType));
				tmp.put("sellFor", res.get("sellFor"));
				tmp.put("buyFor", res.get("buyFor"));
				tmp.put("stock", res.get("stock"));
				tmp.put("perStack", res.get("perStack"));
				tmp.put("shops", res.get("shops"));
				stored.put(player, tmp);
				
				Heraut.say(player, "Successfully selected " + name + " for updating!");
			}else{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("item", name);
				if(resSet.isEmpty()) {
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotInShop", params));
				}else{
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotUnique", params));
				}
			}
		}
	}
	
	private static void set(Player player, String[] args) {
		if(args.length >= 4) {
			/*if(args[2].equalsIgnoreCase("itemID")) {
				try{
					int itemID = Integer.parseInt(args[3]);
					if(itemID > 0) {
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("itemID", String.valueOf(itemID));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully updated itemID to " + itemID + "!");
						Heraut.say(player, "Please note validation is not applied untill finishing!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set itemID");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("itemType")) {
				try{
					int itemType = Integer.parseInt(args[3]);
					if(itemType > 0) {
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("itemType", String.valueOf(itemType));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully updated itemType to " + itemType + "!");
						Heraut.say(player, "Please note validation is not applied untill finishing!");
					}else{
						itemType = null;
						
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("itemType", String.valueOf(itemType));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully removed the itemType!");
						Heraut.say(player, "Please note validation is not applied untill finishing!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set itemType");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else*/ if(args[2].equalsIgnoreCase("sellFor")) {
				try{
					double sellFor = Double.parseDouble(args[3]);
					if(sellFor >= 0) {
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("sellFor", String.valueOf(sellFor));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully updated stock to " + sellFor + "!");
					}else{
						sellFor = -1;
						
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("sellFor", String.valueOf(sellFor));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully disabled selling!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set sellFor");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("buyFor")) {
				try{
					double buyFor = Double.parseDouble(args[3]);
					if(buyFor >= 0) {
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("buyFor", String.valueOf(buyFor));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully updated the amount a player receives to " + buyFor + "!");
					}else{
						buyFor = -1;
						
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("buyFor", String.valueOf(buyFor));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully set accept returns to false!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set buyFor");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("stock")) {
				try{
					int stock = Integer.parseInt(args[3]);
					if(stock >= 0) {
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("stock", String.valueOf(stock));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully updated stock to " + stock + "!");
						Heraut.say(player, "Please note validation is not applied untill finishing!");
					}else{
						stock = -1;
						
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("stock", String.valueOf(stock));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully set the stock to unlimited!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set stock");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("perStack")) {
				try{
					int perStack = Integer.parseInt(args[3]);
					if(perStack > 0) {
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("perStack", String.valueOf(perStack));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully updated perStack to " + perStack + "!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set perStack");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("shops")) {
				Heraut.say(player, "We are sorry, but using GiantShopLocation is not currently supported yet! :(");
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "update set");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "update set");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
	
	private static void save(Player player) {
		HashMap<String, String> tmp = stored.get(player);
		
		int itemID = Integer.parseInt(tmp.get("itemID"));
		Integer itemType;
		try{
			itemType = Integer.parseInt(tmp.get("itemType"));
		}catch(NumberFormatException e) {
			itemType = null;
		}
		String name = iH.getItemNameByID(itemID, itemType);
		
		tmp.remove("itemID");
		tmp.remove("itemType");
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("itemID", "" + itemID);
		data.put("type", "" + ((itemType == null) ? -1 : itemType));
		
		DB.update("#__items").set(tmp).where(data).updateQuery();
		Heraut.say(player, "You have successfully updated " + name + "!");
	}
	
	public static void update(Player player, String[] args) {
		if(perms.has(player, "giantshop.admin.update")) {
			if(args.length >= 3) {
				if(args[1].equalsIgnoreCase("select")) {
					update.select(player, args[2]);
				}else if(args[1].equalsIgnoreCase("set")) {
					if(stored.containsKey(player)) {
						if(args.length >= 4) {
							update.set(player, args);
						}else{
							HashMap<String, String> data = new HashMap<String, String>();
							data.put("command", "update set");

							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
						}
					}else{
						Heraut.say(player, "You have not selected an item yet!");
					}
				}
			}else if(args.length == 2) {
				 if(args[1].equalsIgnoreCase("show")) {
					if(stored.containsKey(player)) {
						HashMap<String, String> data = stored.get(player);
						int iD = Integer.parseInt(data.get("itemID"));
						Integer iT;
						try {
							iT = Integer.parseInt(data.get("itemType"));
						}catch(NumberFormatException e) {
							iT = null;
						}
						String name = iH.getItemNameByID(iD, iT);
						
						Heraut.say(player, "Here's the result for " + name + "!");
						Heraut.say(player, "ID: " + iD);
						Heraut.say(player, "Type: " + iT);
						Heraut.say(player, "Quantity per amount: " + data.get("perStack"));
						Heraut.say(player, "Leaves shop for: " + (!data.get("sellFor").equals("-1.0") ? data.get("sellFor") : "Doesn't leave the shop!"));
						Heraut.say(player, "Returns to shop for: " + (!data.get("buyFor").equals("-1.0") ? data.get("buyFor") : "No returns!"));
						Heraut.say(player, "Amount of items in the shop: " + (!data.get("stock").equals("-1") ? data.get("stock") : "unlimited"));
					}else{
						Heraut.say(player, "You have not selected an item yet!");
					}
				}else if(args[1].equalsIgnoreCase("save")) {
					if(stored.containsKey(player)) {
						update.save(player);
					}else{
						Heraut.say(player, "You have not selected an item yet!");
					}
				}else if(args[1].equalsIgnoreCase("reset")) {
					if(stored.containsKey(player)) {
						stored.remove(player);
						Heraut.say(player, "Successfully reset the updating!");
					}else{
						Heraut.say(player, "You have not selected an item yet!");
					}
				}else{
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update");

					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
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
