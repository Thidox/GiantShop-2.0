package nl.giantit.minecraft.GiantShop.core.Commands;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.Items.*;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class buy {
	
	static config conf = config.Obtain();
	static db DB = db.Obtain();
	static perm perms = perm.Obtain();
	static Messages mH = GiantShop.getPlugin().getMsgHandler();
	static Items iH = GiantShop.getPlugin().getItemHandler();
	
	public static void buy(Player player, String[] args) {
		Heraut.savePlayer(player);
		if(perms.has(player, "giantshop.shop.buy")) {
			if(args.length > 2) {
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
						String[] data = args[1].split(":");
						itemID = Integer.parseInt(data[0]);
						itemType = Integer.parseInt(data[1]);
					}catch(NumberFormatException e) {
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("command", "buy");

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
			
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("perStack");
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("stock");
			fields.add("shops");
			
			HashMap<String, String> where = new HashMap<String, String>();
			where.put("itemID", String.valueOf(itemID));
			where.put("type", String.valueOf((itemType == null || itemType == 0) ? -1 : itemType));
			
			ArrayList<HashMap<String, String>> resSet = DB.select(fields).from("#__items").where(where).execQuery();
			if(resSet.size() == 1) {
				String name = iH.getItemNameByID(itemID, itemType);
				HashMap<String, String> res = resSet.get(0);
				
				//Ok have to get eco engine up first! :)
				
				//More future stuff
				/*if(conf.getBoolean("GiantShop.Location.useGiantShopLocation") == true) {
				 *		ArrayList<Indaface> shops = GiantShop.getPlugin().getLocationHandler().parseShops(res.get("shops"));
				 *		for(Indaface shop : shops) {
				 *			if(shop.inShop(player.getLocation())) {
				 *				//Player can get the item he wants! :D
				 *			}
				 *		}
				 * }else{
				 *		//Just a global store then :)
				 * }
				 */
			}else{
				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noneOrMoreResults"));
			}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "buy");

				Heraut.say(mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "buy");

			Heraut.say(mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
	public static void gift(Player player, String[] args) {
		Heraut.savePlayer(player);
		Heraut.say("test");
	}
}
