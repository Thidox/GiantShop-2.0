package nl.giantit.minecraft.giantshop.core.Commands.Console;

import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.database.query.Group;
import nl.giantit.minecraft.giantcore.database.query.InsertQuery;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.config;
import nl.giantit.minecraft.giantshop.core.Items.ItemID;
import nl.giantit.minecraft.giantshop.core.Items.Items;
import nl.giantit.minecraft.giantshop.core.Logger.Logger;
import nl.giantit.minecraft.giantshop.core.Logger.LoggerType;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class add {
	
	public static void add(CommandSender sender, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		config conf = config.Obtain();
		int itemID;
		Integer itemType = null;
		Double sellFor = -1.0, buyFor = -1.0;
		int stock = -1, maxStock = -1, perStack = 1;
		String shops = "";

		if(args.length < 4) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "add");

			Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
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
					data.put("command", "add");

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

			if(GiantShop.getPlugin().getItemHandler().isValidItem(itemID, itemType)) {
				String name = iH.getItemNameByID(itemID, itemType);
				Driver DB = GiantShop.getPlugin().getDB().getEngine();

				ArrayList<String> fields = new ArrayList<String>();
				fields.add("id");
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("itemID", "" + itemID);
				data.put("type", "" + ((itemType == null) ? -1 : itemType));

					
				SelectQuery sQ = DB.select(fields);
				sQ.from("#__items");
				sQ.where("itemID", String.valueOf(itemID), Group.ValueType.EQUALSRAW);
				sQ.where("type", String.valueOf(((itemType == null) ? -1 : itemType)), Group.ValueType.EQUALSRAW);

				if(sQ.exec().size() == 0) { 
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

						Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
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

					InsertQuery iQ = DB.insert("#__items");
					iQ.addFields(fields);
					iQ.addRow();
					iQ.assignValue("itemID", String.valueOf(itemID), InsertQuery.ValueType.RAW);
					iQ.assignValue("type", String.valueOf((itemType == null) ? -1 : itemType), InsertQuery.ValueType.RAW);
					iQ.assignValue("sellFor", String.valueOf(sellFor), InsertQuery.ValueType.RAW);
					iQ.assignValue("buyFor", String.valueOf(buyFor), InsertQuery.ValueType.RAW);
					iQ.assignValue("stock", String.valueOf(stock), InsertQuery.ValueType.RAW);
					iQ.assignValue("maxStock", String.valueOf(maxStock), InsertQuery.ValueType.RAW);
					iQ.assignValue("perStack", String.valueOf(perStack), InsertQuery.ValueType.RAW);

					iQ.exec();

					Heraut.say(sender, "The requested item (" + name + ") has been added to the shop!");
					HashMap<String, String> d = new HashMap<String, String>();
					d.put("id", String.valueOf(itemID));
					d.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));
					d.put("sF", String.valueOf(sellFor));
					d.put("bF", String.valueOf(buyFor));
					d.put("pS", String.valueOf(perStack));
					d.put("s", String.valueOf(stock));
					d.put("mS", String.valueOf(maxStock));
					d.put("shops", shops);
					Logger.Log(LoggerType.ADD, "Console", d);
				}else{
					Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "itemAlreadyFound"));
				}
			}else{
				Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "itemNotFound"));
			}
		}
	}
}
