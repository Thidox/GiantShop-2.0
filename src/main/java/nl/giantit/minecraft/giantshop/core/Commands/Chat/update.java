package nl.giantit.minecraft.giantshop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.core.Items.ItemID;
import nl.giantit.minecraft.giantcore.core.Items.Items;
import nl.giantit.minecraft.giantcore.database.query.Group;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;
import nl.giantit.minecraft.giantcore.database.query.UpdateQuery;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.giantshop.API.GiantShopAPI;
import nl.giantit.minecraft.giantshop.API.stock.Events.MaxStockUpdateEvent;
import nl.giantit.minecraft.giantshop.API.stock.Events.StockUpdateEvent;
import nl.giantit.minecraft.giantshop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.Logger.Logger;
import nl.giantit.minecraft.giantshop.core.Logger.LoggerType;
import nl.giantit.minecraft.giantshop.core.config;

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
	private static Driver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
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
			fields.add("maxStock");
			fields.add("perStack");
			fields.add("shops");

			SelectQuery sQ = DB.select(fields);
			sQ.from("#__items");
			sQ.where("itemID", String.valueOf(itemID), Group.ValueType.EQUALSRAW);
			sQ.where("type", String.valueOf(((itemType == null) ? -1 : itemType)), Group.ValueType.EQUALSRAW);

			QueryResult QRes = sQ.exec();
			if(QRes.size() == 1) {
				QueryRow QR = QRes.getRow();
				HashMap<String, String> tmp = new HashMap<String, String>();
				tmp.put("itemID", String.valueOf(itemID));
				tmp.put("itemType", String.valueOf(itemType));
				tmp.put("sellFor", QR.getString("sellfor"));
				tmp.put("buyFor", QR.getString("buyfor"));
				tmp.put("stock", QR.getString("stock"));
				tmp.put("maxStock", QR.getString("maxstock"));
				tmp.put("ostock", QR.getString("stock"));
				tmp.put("omaxStock", QR.getString("maxstock"));
				tmp.put("perStack", QR.getString("perstack"));
				tmp.put("shops", QR.getString("shops"));
				stored.put(player, tmp);
				
				Heraut.say(player, "Successfully selected " + name + " for updating!");
			}else{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("item", name);
				if(QRes.size() == 0) {
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotInShop", params));
				}else{
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotUnique", params));
				}
			}
		}
	}
	
	private static void set(Player player, String[] args) {
		if(args.length >= 4) {
			if(args[2].equalsIgnoreCase("sellFor")) {
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
			}else if(args[2].equalsIgnoreCase("maxStock")) {
				try{
					int maxStock = Integer.parseInt(args[3]);
					if(maxStock >= 0) {
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("maxStock", String.valueOf(maxStock));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully updated the max stock to " + maxStock + "!");
					}else{
						maxStock = -1;
						
						HashMap<String, String> tmp = stored.get(player);
						tmp.put("maxStock", String.valueOf(maxStock));
						stored.put(player, tmp);
						
						Heraut.say(player, "Succesfully set the max stock to unlimited!");
					}
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "update set maxStock");

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
		HashMap<String, String> tmp = stored.remove(player);
		
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
			
			UpdateQuery uQ = DB.update("#__items");
			uQ.set("sellFor", tmp.get("sellFor"), UpdateQuery.ValueType.SETRAW);
			uQ.set("buyFor", tmp.get("buyFor"), UpdateQuery.ValueType.SETRAW);
			uQ.set("perStack", tmp.get("perStack"), UpdateQuery.ValueType.SETRAW);
			uQ.set("stock", tmp.get("stock"), UpdateQuery.ValueType.SETRAW);
			uQ.set("maxStock", tmp.get("maxStock"), UpdateQuery.ValueType.SETRAW);

			uQ.where("itemID", String.valueOf(itemID), Group.ValueType.EQUALSRAW);
			uQ.where("type", String.valueOf(((itemType == null) ? -1 : itemType)), Group.ValueType.EQUALSRAW);
			uQ.exec();
			
			Heraut.say(player, "You have successfully updated " + name + "!");
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
			Logger.Log(LoggerType.UPDATE, player.getName(), d);
			
			try {
				StockUpdateEvent.StockUpdateType t = (oS < s) ? StockUpdateEvent.StockUpdateType.INCREASE : StockUpdateEvent.StockUpdateType.DECREASE;
				MaxStockUpdateEvent.StockUpdateType mt = (omS < mS) ? MaxStockUpdateEvent.StockUpdateType.INCREASE : MaxStockUpdateEvent.StockUpdateType.DECREASE;
				
				StockUpdateEvent event = new StockUpdateEvent(player, GiantShopAPI.Obtain().getStockAPI().getItemStock(itemID, itemType), t);
				MaxStockUpdateEvent events = new MaxStockUpdateEvent(player, GiantShopAPI.Obtain().getStockAPI().getItemStock(itemID, itemType), mt);
				GiantShop.getPlugin().getSrvr().getPluginManager().callEvent(event);
				GiantShop.getPlugin().getSrvr().getPluginManager().callEvent(events);
			}catch(ItemNotFoundException e) {}
		}else{
			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "stockExeedsMaxStock"));
		}
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
						Heraut.say(player, "Maximum amount of items in the shop: " + (!data.get("maxStock").equals("-1") ? data.get("maxStock") : "unlimited"));
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
