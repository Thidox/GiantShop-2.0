package nl.giantit.minecraft.GiantShop.core.Commands.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Tools.Discount.Discounter;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.perms.Permission;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class search {
	
	public static void exec(Player player, String[] args) {
		Messages mH = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
		config conf = config.Obtain();
		Discounter disc = GiantShop.getPlugin().getDiscounter();
		if(perms.has(player, "giantshop.shop.search")) {
			String name = GiantShop.getPlugin().getPubName();
			int perPage = conf.getInt("GiantShop.global.perPage");
			int curPag = 0;
			
			if(args.length < 2 || args[1].length() < 3) {
				HashMap<String, String> d = new HashMap<String, String>();
				d.put("command", "search");
				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", d));
				return;
			}
			
			ArrayList<ItemID> iList = iH.getItemIDsByPart(args[1]);
			if(iList.isEmpty()) {
				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
				return;
			}
			
			if(args.length >= 3) {
				try{
					curPag = Integer.parseInt(args[2]);
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
			
			DB.select(fields).from("#__items");
			
			boolean hide = false;
			HashMap<String, HashMap<String, String>> where = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> t = new HashMap<String, String>();
			if(conf.getBoolean("GiantShop.stock.hideEmptyStock")) {
				t.put("kind", "NOT");
				t.put("data", "0");
				where.put("stock", t);
				hide = true;
				DB.where(where, true);
			}
			
			int a = 0;
			for(ItemID iID : iList) {
				if(a > 0) {
					DB.buildQuery(" OR \n", true);
				}else{
					if(hide) {
						DB.buildQuery(" AND \n", true);
					}else{
						DB.buildQuery(" WHERE \n", true);
					}
					++a;
				}
				
				DB.buildQuery("(itemID = " + iID.getId() + " AND type = " + ((iID.getType() == null || iID.getType() <= 0) ? -1 : iID.getType()) + ")\n", true);
			}
			
			HashMap<String, String> order = new HashMap<String, String>();
			order.put("itemID", "ASC");
			order.put("type", "ASC");
			ArrayList<HashMap<String, String>> data = DB.orderBy(order).execQuery();
			
			int pages = ((int)Math.ceil((double)data.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)data.size() / (double)perPage);
			int start = (curPag * perPage) - perPage;
			
			if(data.size() <= 0) {
				Heraut.say(player, "&e[&3" + name + "&e] " + mH.getMsg(Messages.msgType.ERROR, "itemNotInShop"));
			}else if(curPag > pages) {
				HashMap<String, String> d = new HashMap<String, String>();
				d.put("list", "search");
				d.put("pages", String.valueOf(pages));
				Heraut.say(player, "&e[&3" + name + "&e] " + mH.getMsg(Messages.msgType.ERROR, "pageOverMax", d));
			}else{
				HashMap<String, String> d = new HashMap<String, String>();
				d.put("itemName", args[1]);
				d.put("page", String.valueOf(curPag));
				d.put("maxPages", String.valueOf(pages));

				Heraut.say(player, "&e[&3" + name + "&e] " + mH.getMsg(Messages.msgType.MAIN, "searchListHead", d));

				for(int i = start; i < (((start + perPage) > data.size()) ? data.size() : (start + perPage)); i++) {
					// This area most defenitally requires clean-up!
					HashMap<String, String> params = new HashMap<String, String>();
					HashMap<String, String> entry = data.get(i);

					int stock = Integer.parseInt(entry.get("stock"));
					int maxStock = Integer.parseInt(entry.get("maxstock"));
					double sellFor = Double.parseDouble(entry.get("sellfor"));
					double buyFor = Double.parseDouble(entry.get("buyfor"));

					if(conf.getBoolean("GiantShop.stock.useStock") && conf.getBoolean("GiantShop.stock.stockDefinesCost") && maxStock != -1 && stock != -1) {
						double maxInfl = conf.getDouble("GiantShop.stock.maxInflation");
						double maxDefl = conf.getDouble("GiantShop.stock.maxDeflation");
						int atmi = conf.getInt("GiantShop.stock.amountTillMaxInflation");
						int atmd = conf.getInt("GiantShop.stock.amountTillMaxDeflation");
						double split = Math.round((atmi + atmd) / 2);
						if(maxStock <= atmi + atmd) {
							split = maxStock / 2;
							atmi = 0;
							atmd = maxStock;
						}

						if(stock >= atmd) {
							if(buyFor != -1)
								buyFor = buyFor * (1.0 - maxDefl / 100.0);

							if(sellFor != -1)
								sellFor = sellFor * (1.0 - maxDefl / 100.0); 
						}else if(stock <= atmi) {
							if(buyFor != -1)
								buyFor = buyFor * (1.0 + maxDefl / 100.0);

							if(sellFor != -1)
								sellFor = sellFor * (1.0 + maxDefl / 100.0);
						}else{
							if(stock < split) {
								if(buyFor != -1)
									buyFor = (double)Math.round((buyFor * (1.0 + (maxInfl / stock) / 100)) * 100.0) / 100.0;

								if(sellFor != -1)
									sellFor = (double)Math.round((sellFor * (1.0 + (maxInfl / stock) / 100)) * 100.0) / 100.0;
							}else if(stock > split) {
								if(buyFor != -1)
									buyFor = 2.0 + (double)Math.round((buyFor / (maxDefl * stock / 100)) * 100.0) / 100.0;

								if(sellFor != -1)
									sellFor = 2.0 + (double)Math.round((sellFor / (maxDefl * stock / 100)) * 100.0) / 100.0;

							}
						}
					}

					Integer type = Integer.parseInt(entry.get("type"));
					type = type <= 0 ? null : type;

					int discount = disc.getDiscount(iH.getItemIDByName(iH.getItemNameByID(Integer.parseInt(entry.get("itemid")), type)), player);
					if(discount > 0) {
						double actualDiscount = (100 - discount) / 100D;
						buyFor = Misc.Round(buyFor * actualDiscount, 2);
						if(conf.getBoolean(GiantShop.getPlugin().getName() + ".discounts.affectsSales"))
							sellFor = Misc.Round(sellFor * actualDiscount, 2);
					}

					String sf = String.valueOf(sellFor);
					String bf = String.valueOf(buyFor);

					params.put("id", entry.get("itemid"));
					params.put("type", (!entry.get("type").equals("-1") ? entry.get("type") : "0"));
					params.put("name", iH.getItemNameByID(Integer.parseInt(entry.get("itemid")), type));
					params.put("perStack", entry.get("perstack"));
					params.put("sellFor", (!sf.equals("-1.0") && !sf.equals("-1") ? sf : "Not for sale!"));
					params.put("buyFor", (!bf.equals("-1.0") && !sf.equals("-1") ? bf : "No returns!"));

					if(conf.getBoolean("GiantShop.stock.useStock") == true) {
						params.put("stock", (!entry.get("stock").equals("-1") ? entry.get("stock") : "unlimited"));
						params.put("maxStock", (!entry.get("maxstock").equals("-1") ? entry.get("maxstock") : "unlimited"));
					}else{
						params.put("stock", "unlimited");
						params.put("maxStock", "unlimited");
					}

					Heraut.say(player, mH.getMsg(Messages.msgType.MAIN, "searchListEntry", params));
				}
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "search");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
}
