package nl.giantit.minecraft.giantshop.core.Commands.Console;

import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.core.Items.Items;
import nl.giantit.minecraft.giantcore.database.query.Group;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.config;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class list {
	
	public static void list(CommandSender sender, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		config conf = config.Obtain();
		
		String name = GiantShop.getPlugin().getPubName();
		int perPage = conf.getInt("GiantShop.global.perPage");
		int curPag = 0;
		
		if(args.length >= 2) {
			try{
				curPag = Integer.parseInt(args[1]);
			}catch(NumberFormatException e) {
				curPag = 1;
			}
		}else
			curPag = 1;
			curPag = (curPag > 0) ? curPag : 1;
	
		Driver DB = GiantShop.getPlugin().getDB().getEngine();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("itemID");
		fields.add("type");
		fields.add("perStack");
		fields.add("sellFor");
		fields.add("buyFor");
		fields.add("stock");
		fields.add("maxStock");
		fields.add("shops");
		
		SelectQuery sQ = DB.select(fields);
		sQ.from("#__items");
		if(conf.getBoolean("GiantShop.stock.hideEmptyStock")) {
			sQ.where("stock", "0", Group.ValueType.NOTEQUALSRAW);
		}

		sQ.orderBy("itemID", SelectQuery.Order.ASC);
		sQ.orderBy("type", SelectQuery.Order.ASC);
		sQ.exec();

		QueryResult QRes = sQ.exec();
		int pages = ((int)Math.ceil((double)QRes.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)QRes.size() / (double)perPage);
		int start = (curPag * perPage) - perPage;
		if(QRes.size() <= 0) {
			Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "noItems"));
		}else if(curPag > pages) {
			Heraut.say(sender, "[" + name + "] My Item list only has " + pages + " pages!!");
		}else{
			Heraut.say(sender, "[" + name + "] Item list. Page: " + curPag + "/" + pages);
			
			for(int i = start; i < (((start + perPage) > QRes.size()) ? QRes.size() : (start + perPage)); i++) {
				QueryRow entry = QRes.getRow(i);
				
				int stock = entry.getInt("stock");
				int maxStock = entry.getInt("maxstock");
				double sellFor = entry.getDouble("sellfor");
				double buyFor = entry.getDouble("buyfor");
				
				if(buyFor != -1) {
					buyFor = Misc.getPrice(buyFor, stock, maxStock, 1);
				}

				if(sellFor != -1) {
					sellFor = Misc.getPrice(sellFor, stock, maxStock, 1);
				}

				String sf = String.valueOf(sellFor);
				String bf = String.valueOf(buyFor);
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("id", entry.getString("itemid"));
				params.put("type", (!entry.getString("type").equals("-1") ? entry.getString("type") : "0"));
				params.put("name", iH.getItemNameByID(entry.getInt("itemid"), Integer.parseInt(params.get("type"))));
				params.put("perStack", entry.getString("perstack"));
				params.put("sellFor", (!sf.equals("-1.0") ? sf : "Not for sale!"));
				params.put("buyFor", (!bf.equals("-1.0") ? bf : "No returns!"));
				
				if(conf.getBoolean("GiantShop.stock.useStock") == true) {
					params.put("stock", (!entry.getString("stock").equals("-1") ? entry.getString("stock") : "unlimited"));
					params.put("maxStock", (!entry.getString("maxstock").equals("-1") ? entry.getString("maxstock") : "unlimited"));
				}else{
					params.put("stock", "unlimited");
					params.put("maxStock", "unlimited");
				}
					
				Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.MAIN, "itemListEntry", params));
			}
		}	
	}
}
