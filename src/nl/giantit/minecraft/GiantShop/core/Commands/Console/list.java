package nl.giantit.minecraft.GiantShop.core.Commands.Console;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;
import nl.giantit.minecraft.GiantShop.core.Items.Items;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.Misc.Misc;

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
	
		iDriver DB = Database.Obtain().getEngine();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("itemID");
		fields.add("type");
		fields.add("perStack");
		fields.add("sellFor");
		fields.add("buyFor");
		fields.add("stock");
		fields.add("maxStock");
		fields.add("shops");
		
		HashMap<String, HashMap<String, String>> where = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> t = new HashMap<String, String>();
		if(conf.getBoolean("GiantShop.stock.hideEmptyStock")) {
			t.put("kind", "NOT");
			t.put("data", "0");
			where.put("stock", t);
		}
		
		HashMap<String, String> order = new HashMap<String, String>();
		order.put("itemID", "ASC");
		order.put("type", "ASC");
		ArrayList<HashMap<String, String>> data = DB.select(fields).from("#__items").where(where, true).orderBy(order).execQuery();
		
		int pages = ((int)Math.ceil((double)data.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)data.size() / (double)perPage);
		int start = (curPag * perPage) - perPage;
		if(data.size() <= 0) {
			Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.ERROR, "noItems"));
		}else if(curPag > pages) {
			Heraut.say(sender, "[" + name + "] My Item list only has " + pages + " pages!!");
		}else{
			Heraut.say(sender, "[" + name + "] Item list. Page: " + curPag + "/" + pages);
			
			for(int i = start; i < (((start + perPage) > data.size()) ? data.size() : (start + perPage)); i++) {
				HashMap<String, String> entry = data.get(i);
				

				int stock = Integer.parseInt(entry.get("stock"));
				int maxStock = Integer.parseInt(entry.get("maxstock"));
				double sellFor = Double.parseDouble(entry.get("sellfor"));
				double buyFor = Double.parseDouble(entry.get("buyfor"));
				
				if(buyFor != -1) {
					buyFor = Misc.getPrice(buyFor, stock, maxStock, 1);
				}

				if(sellFor != -1) {
					sellFor = Misc.getPrice(sellFor, stock, maxStock, 1);
				}

				String sf = String.valueOf(sellFor);
				String bf = String.valueOf(buyFor);
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("id", entry.get("itemid"));
				params.put("type", (!entry.get("type").equals("-1") ? entry.get("type") : "0"));
				params.put("name", iH.getItemNameByID(Integer.parseInt(entry.get("itemid")), Integer.parseInt(params.get("type"))));
				params.put("perStack", entry.get("perstack"));
				params.put("sellFor", (!sf.equals("-1.0") ? sf : "Not for sale!"));
				params.put("buyFor", (!bf.equals("-1.0") ? bf : "No returns!"));
				
				if(conf.getBoolean("GiantShop.stock.useStock") == true) {
					params.put("stock", (!entry.get("stock").equals("-1") ? entry.get("stock") : "unlimited"));
					params.put("maxStock", (!entry.get("maxstock").equals("-1") ? entry.get("maxstock") : "unlimited"));
				}else{
					params.put("stock", "unlimited");
					params.put("maxStock", "unlimited");
				}
					
				Heraut.say(sender, msgs.getConsoleMsg(Messages.msgType.MAIN, "itemListEntry", params));
			}
		}	
	}
}
