package nl.giantit.minecraft.GiantShop.core.Commands.Console;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.GiantShop.API.stock.Events.MaxStockUpdateEvent;
import nl.giantit.minecraft.GiantShop.API.stock.Events.StockUpdateEvent;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Logger.Logger;
import nl.giantit.minecraft.GiantShop.core.Logger.LoggerType;
import nl.giantit.minecraft.giantcore.Database.QueryResult;
import nl.giantit.minecraft.giantcore.Database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.Database.iDriver;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class update {
	
	private static config conf = config.Obtain();
	private static iDriver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	private static HashMap<CommandSender, HashMap<String, String>> storedC = new HashMap<CommandSender, HashMap<String, String>>();
	
	private static void select(CommandSender sender, String item) {
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
				String[] data = item.split(":");
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
			String name = iH.getItemNameByID(itemID, itemType);
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("stock");
			fields.add("maxStock");
			fields.add("perStack");
			fields.add("shops");
			
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("itemID", "" + itemID);
			data.put("type", "" + ((itemType == null) ? -1 : itemType));

			DB.select(fields).from("#__items").where(data);
			QueryResult resSet = DB.execQuery();
			if(resSet.size() == 1) {
				QueryRow res = resSet.getRow();
				HashMap<String, String> tmp = new HashMap<String, String>();
				tmp.put("itemID", String.valueOf(itemID));
				tmp.put("itemType", String.valueOf(itemType));
				tmp.put("sellFor", res.getString("sellfor"));
				tmp.put("buyFor", res.getString("buyfor"));
				tmp.put("stock", res.getString("stock"));
				tmp.put("maxStock", res.getString("maxstock"));
				tmp.put("ostock", res.getString("stock"));
				tmp.put("omaxStock", res.getString("maxstock"));
				tmp.put("perStack", res.getString("perstack"));
				tmp.put("shops", res.getString("shops"));
				storedC.put(sender, tmp);
				
				Heraut.say(sender, "Successfully selected " + name + " for updating!");
			}else{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("item", name);
				if(resSet.size() == 0) {
					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "itemNotInShop", params));
				}else{
					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "itemNotUnique", params));
				}
			}
		}
	}
	
	private static void set(CommandSender sender, String[] args) {
		if(args.length >= 4) {
			if(args[2].equalsIgnoreCase("sellFor")) {
				try{
					double sellFor = Double.parseDouble(args[3]);
					if(sellFor >= 0) {
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("sellFor", String.valueOf(sellFor));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully updated stock to " + sellFor + "!");
					}else{
						sellFor = -1;
						
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("sellFor", String.valueOf(sellFor));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully disabled selling!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set sellFor");

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("buyFor")) {
				try{
					double buyFor = Double.parseDouble(args[3]);
					if(buyFor >= 0) {
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("buyFor", String.valueOf(buyFor));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully updated the amount a player receives to " + buyFor + "!");
					}else{
						buyFor = -1;
						
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("buyFor", String.valueOf(buyFor));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully set accept returns to false!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set buyFor");

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("stock")) {
				try{
					int stock = Integer.parseInt(args[3]);
					if(stock >= 0) {
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("stock", String.valueOf(stock));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully updated stock to " + stock + "!");
						Heraut.say(sender, "Please note validation is not applied untill finishing!");
					}else{
						stock = -1;
						
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("stock", String.valueOf(stock));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully set the stock to unlimited!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set stock");

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("maxStock")) {
				try{
					int maxStock = Integer.parseInt(args[3]);
					if(maxStock >= 0) {
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("maxStock", String.valueOf(maxStock));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully updated the max stock to " + maxStock + "!");
					}else{
						maxStock = -1;
						
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("maxStock", String.valueOf(maxStock));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully set the max stock to unlimited!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set maxStock");

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("perStack")) {
				try{
					int perStack = Integer.parseInt(args[3]);
					if(perStack > 0) {
						HashMap<String, String> tmp = storedC.get(sender);
						tmp.put("perStack", String.valueOf(perStack));
						storedC.put(sender, tmp);
						
						Heraut.say(sender, "Succesfully updated perStack to " + perStack + "!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set perStack");

					Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}else if(args[2].equalsIgnoreCase("shops")) {
				Heraut.say(sender, "We are sorry, but using GiantShopLocation is not currently supported yet! :(");
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "update set");

				Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "update set");

			Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
	
	private static void save(CommandSender sender) {
		HashMap<String, String> tmp = storedC.remove(sender);

		int s = Integer.parseInt(tmp.get("stock"));
		int mS = Integer.parseInt(tmp.get("maxStock"));
		int oS = Integer.parseInt(tmp.remove("ostock"));
		int omS = Integer.parseInt(tmp.remove("omaxStock"));
		
		if(s == -1 || mS == -1 || (s <= mS && !conf.getBoolean("GiantShop.stock.allowOverStock"))) {
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
			Heraut.say(sender, "You have successfully updated " + name + "!");
			HashMap<String, String> d = new HashMap<String, String>();
			d.put("id", String.valueOf(itemID));
			d.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));
			d.put("pS", tmp.get("perStack"));
			d.put("sF", tmp.get("sellFor"));
			d.put("bF", tmp.get("buyFor"));
			d.put("oS", String.valueOf(oS));
			d.put("nS", tmp.get("stock"));
			d.put("oMS", tmp.get("maxStock"));
			d.put("nMS", String.valueOf(omS));
			Logger.Log(LoggerType.UPDATE, "Console", d);
			
			try {
				StockUpdateEvent.StockUpdateType t = (oS < s) ? StockUpdateEvent.StockUpdateType.INCREASE : StockUpdateEvent.StockUpdateType.DECREASE;
				MaxStockUpdateEvent.StockUpdateType mt = (omS < mS) ? MaxStockUpdateEvent.StockUpdateType.INCREASE : MaxStockUpdateEvent.StockUpdateType.DECREASE;
				
				StockUpdateEvent event = new StockUpdateEvent(null, GiantShopAPI.Obtain().getStockAPI().getItemStock(itemID, itemType), t);
				MaxStockUpdateEvent events = new MaxStockUpdateEvent(null, GiantShopAPI.Obtain().getStockAPI().getItemStock(itemID, itemType), mt);
				GiantShop.getPlugin().getSrvr().getPluginManager().callEvent(event);
				GiantShop.getPlugin().getSrvr().getPluginManager().callEvent(events);
			}catch(ItemNotFoundException e) {}
		}else{
			Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "stockExeedsMaxStock"));
		}
	}
	
	public static void update(CommandSender sender, String[] args) {
		if(args.length >= 3) {
			if(args[1].equalsIgnoreCase("select")) {
				update.select(sender, args[2]);
			}else if(args[1].equalsIgnoreCase("set")) {
				if(storedC.containsKey(sender)) {
					if(args.length >= 4) {
						update.set(sender, args);
					}else{
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("command", "update set");

						Heraut.say(sender, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
					}
				}else{
					Heraut.say(sender, "You have not selected an item yet!");
				}
			}
		}else if(args.length == 2) {
			 if(args[1].equalsIgnoreCase("show")) {
				if(storedC.containsKey(sender)) {
					HashMap<String, String> data = storedC.get(sender);
					int iD = Integer.parseInt(data.get("itemID"));
					Integer iT;
					try {
						iT = Integer.parseInt(data.get("itemType"));
					}catch(NumberFormatException e) {
						iT = null;
					}
					String name = iH.getItemNameByID(iD, iT);

					Heraut.say(sender, "Here's the result for " + name + "!");
					Heraut.say(sender, "ID: " + iD);
					Heraut.say(sender, "Type: " + iT);
					Heraut.say(sender, "Quantity per amount: " + data.get("perStack"));
					Heraut.say(sender, "Leaves shop for: " + (!data.get("sellFor").equals("-1.0") ? data.get("sellFor") : "Doesn't leave the shop!"));
					Heraut.say(sender, "Returns to shop for: " + (!data.get("buyFor").equals("-1.0") ? data.get("buyFor") : "No returns!"));
					Heraut.say(sender, "Amount of items in the shop: " + (!data.get("stock").equals("-1") ? data.get("stock") : "unlimited"));
				}else{
					Heraut.say(sender, "You have not selected an item yet!");
				}
			}else if(args[1].equalsIgnoreCase("save")) {
				if(storedC.containsKey(sender)) {
					update.save(sender);
				}else{
					Heraut.say(sender, "You have not selected an item yet!");
				}
			}else if(args[1].equalsIgnoreCase("reset")) {
				if(storedC.containsKey(sender)) {
					storedC.remove(sender);
					Heraut.say(sender, "Successfully reset the updating!");
				}else{
					Heraut.say(sender, "You have not selected an item yet!");
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "update");

				Heraut.say(sender, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			 }
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "update");

			Heraut.say(sender, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
}
