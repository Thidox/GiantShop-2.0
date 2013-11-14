package nl.giantit.minecraft.GiantShop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Logger.Logger;
import nl.giantit.minecraft.GiantShop.core.Logger.LoggerType;
import nl.giantit.minecraft.giantcore.Database.iDriver;
import nl.giantit.minecraft.giantcore.perms.Permission;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class add {
	
	public static void add(Player player, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
		config conf = config.Obtain();
		if(perms.has(player, "giantshop.admin.add")) {
			int itemID;
			Integer itemType = null;
			Double sellFor = -1.0, buyFor = -1.0;
			int stock = -1, maxStock = -1, perStack = 1;
			String shops = "";
			
			if(args.length < 4) {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "add");
				
				Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}else{
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
						data.put("command", "add");

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
				
				if(GiantShop.getPlugin().getItemHandler().isValidItem(itemID, itemType)) {
					String name = iH.getItemNameByID(itemID, itemType);
					iDriver DB = GiantShop.getPlugin().getDB().getEngine();
					
					ArrayList<String> fields = new ArrayList<String>();
					fields.add("id");
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("itemID", "" + itemID);
					data.put("type", "" + ((itemType == null) ? -1 : itemType));
					
					DB.select(fields).from("#__items").where(data);
					if(DB.execQuery().size() == 0) { 
						try {
							perStack = Integer.parseInt(args[2]);
							sellFor = Double.parseDouble(args[3]);
							if(args.length >= 5)
								buyFor = Double.parseDouble(args[4]);
							if(args.length >= 6)
								stock = Integer.parseInt(args[5]);
							if(args.length >= 7) {
								maxStock = Integer.parseInt(args[6]);
							}else{
								maxStock = conf.getInt("GiantShop.stock.defaultMaxStock");
								maxStock = (maxStock > 0) ? maxStock : -1;
							}
							
						}catch(NumberFormatException e) {
							data = new HashMap<String, String>();
							data.put("command", "add");

							Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "syntaxError", data));
							return;
						}
						
						fields = new ArrayList<String>();
						fields.add("itemID");
						fields.add("type");
						fields.add("sellFor");
						fields.add("buyFor");
						fields.add("stock");
						fields.add("maxStock");
						fields.add("perStack");
						fields.add("shops");

						ArrayList<HashMap<Integer, HashMap<String, String>>> values = new ArrayList<HashMap<Integer, HashMap<String, String>>>();
						HashMap<Integer, HashMap<String, String>> tmp = new HashMap<Integer, HashMap<String, String>>();
						int i = 0;
						for(String field : fields) {
							HashMap<String, String> temp = new HashMap<String, String>();
							if(field.equalsIgnoreCase("itemID")) {
								temp.put("kind", "INT");
								temp.put("data", "" + itemID);
								tmp.put(i, temp);
							}else if(field.equalsIgnoreCase("type")) {
								temp.put("kind", "INT");
								temp.put("data", "" + ((itemType == null) ? -1 : itemType));
								tmp.put(i, temp);
							}else if(field.equalsIgnoreCase("sellFor")) {
								temp.put("data", "" + sellFor);
								tmp.put(i, temp);
							}else if(field.equalsIgnoreCase("buyFor")) {
								temp.put("data", "" + buyFor);
								tmp.put(i, temp);
							}else if(field.equalsIgnoreCase("stock")) {
								temp.put("kind", "INT");
								temp.put("data", "" + stock);
								tmp.put(i, temp);
							}else if(field.equalsIgnoreCase("maxStock")) {
								temp.put("kind", "INT");
								temp.put("data", "" + maxStock);
								tmp.put(i, temp);
							}else if(field.equalsIgnoreCase("perStack")) {
								temp.put("kind", "INT");
								temp.put("data", "" + perStack);
								tmp.put(i, temp);
							}else if(field.equalsIgnoreCase("shops")) {
								temp.put("data", shops);
								tmp.put(i, temp);
							}
							i++;
						}
						values.add(tmp);
						
						DB.insert("#__items", fields, values).updateQuery();

						HashMap<String, String> d = new HashMap<String, String>();
						d.put("id", String.valueOf(itemID));
						d.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));
						d.put("sF", String.valueOf(sellFor));
						d.put("bF", String.valueOf(buyFor));
						d.put("pS", String.valueOf(perStack));
						d.put("s", String.valueOf(stock));
						d.put("mS", String.valueOf(maxStock));
						d.put("shops", shops);
						Logger.Log(LoggerType.ADD, player.getName(), d);
						
						Heraut.say(player, "The requested item (&e" + name + "&f) has been added to the shop!");
					}else{
						Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "itemAlreadyFound"));
					}
				}else{
					Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "itemNotFound"));
				}
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "add");

			Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
