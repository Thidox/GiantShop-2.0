package nl.giantit.minecraft.GiantShop.core.Commands.Console;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.giantcore.Database.QueryResult;
import nl.giantit.minecraft.giantcore.Database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.Database.iDriver;

/**
 *
 * @author Giant
 */
public class check {
	
	public static void check(CommandSender sender, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		config conf = config.Obtain();
		
		iDriver DB = GiantShop.getPlugin().getDB().getEngine();
		int itemID;
		Integer itemType = -1;

		if(args.length >= 2) {
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
						Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "itemNotFound"));
						return;
					}
				}catch(Exception e) {
					if(conf.getBoolean("GiantShop.global.debug") == true) {
						GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
						GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
					}
	
					Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "unknown"));
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
	
					Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
					return;
				}catch(Exception e) {
					if(conf.getBoolean("GiantShop.global.debug") == true) {
						GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
						GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
					}
	
					Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "unknown"));
					return;
				}
			}
			itemType = (itemType == null || itemType == 0) ? -1 : itemType;
	
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("perStack");
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("stock");
			fields.add("maxStock");
			fields.add("shops");
			
			HashMap<String, String> where = new HashMap<String, String>();
			where.put("itemID", String.valueOf(itemID));
			where.put("type", String.valueOf(itemType));
			
			QueryResult QRes = DB.select(fields).from("#__items").where(where).execQuery();
			if(QRes.size() == 1) {
				//Wait didn't we just do this the other way round?!
				//Yea we did! Why? Because we can!
				itemType = (itemType == -1) ? 0 : itemType;
				
				String name = iH.getItemNameByID(itemID, itemType);
				QueryRow QR = QRes.getRow();
				
				int stock = QR.getInt("stock");
				int maxStock = QR.getInt("maxstock");
				double sellFor = QR.getDouble("sellfor");
				double buyFor = QR.getDouble("buyfor");
				
				if(buyFor != -1) {
					buyFor = Misc.getPrice(buyFor, stock, maxStock, 1);
				}

				if(sellFor != -1) {
					sellFor = Misc.getPrice(sellFor, stock, maxStock, 1);
				}
				
				String sf = String.valueOf(sellFor);
				String bf = String.valueOf(buyFor);
	
				Heraut.say(sender, "Here's the result for " + name + "!");
				Heraut.say(sender, "ID: " + itemID);
				Heraut.say(sender, "Type: " + itemType);
				Heraut.say(sender, "Quantity per amount: " + QR.getString("perstack"));
				Heraut.say(sender, "Leaves shop for: " + (!sf.equals("-1.0") ? sf : "Not for sale!"));
				Heraut.say(sender, "Returns to shop for: " + (!bf.equals("-1.0") ? bf : "No returns!"));
				Heraut.say(sender, "Amount of items in the shop: " + (!QR.getString("stock").equals("-1") ? QR.getString("stock") : "unlimited"));
				Heraut.say(sender, "Maximum amount of items in the shop: " + (!QR.getString("maxstock").equals("-1") ? QR.getString("maxstock") : "unlimited"));
				//More future stuff
				/*if(conf.getBoolean("GiantShop.Location.useGiantShopLocation") == true) {
				 *		ArrayList<Indaface> shops = GiantShop.getPlugin().getLocationHandler().parseShops(res.get("shops"));
				 *		for(Indaface shop : shops) {
				 *			if(shop.inShop(player.getLocation())) {
				 *				Heraut.say(player, "Something about what shops these items are in or something like that!");
				 *				break;
				 *			}
				 *		}
				 * } 
				 */
			}else{
				Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "noneOrMoreResults"));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "check");

			Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
}
