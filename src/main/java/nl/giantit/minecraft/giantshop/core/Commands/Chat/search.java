package nl.giantit.minecraft.GiantShop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.Database.QueryResult;
import nl.giantit.minecraft.giantcore.Database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.Database.iDriver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Tools.Discount.Discounter;
import nl.giantit.minecraft.GiantShop.core.config;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

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
		
			iDriver DB = GiantShop.getPlugin().getDB().getEngine();
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
			//QueryResult QRes = DB.select(fields).from("#__items").where(where, true).execQuery();
			QueryResult QRes = DB.orderBy(order).execQuery();
			
			int pages = ((int)Math.ceil((double)QRes.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)QRes.size() / (double)perPage);
			int start = (curPag * perPage) - perPage;
			
			if(QRes.size() <= 0) {
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

				for(int i = start; i < (((start + perPage) > QRes.size()) ? QRes.size() : (start + perPage)); i++) {
					// This area most defenitally requires clean-up!
					HashMap<String, String> params = new HashMap<String, String>();
					QueryRow QR = QRes.getRow(i);

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

					Integer type = QR.getInteger("type");
					type = type <= 0 ? null : type;

					int discount = disc.getDiscount(iH.getItemIDByName(iH.getItemNameByID(QR.getInt("itemid"), type)), player);
					if(discount > 0) {
						double actualDiscount = (100 - discount) / 100D;
						buyFor = Misc.Round(buyFor * actualDiscount, 2);
						if(conf.getBoolean(GiantShop.getPlugin().getName() + ".discounts.affectsSales"))
							sellFor = Misc.Round(sellFor * actualDiscount, 2);
					}

					String sf = String.valueOf(sellFor);
					String bf = String.valueOf(buyFor);

					params.put("id", QR.getString("itemid"));
					params.put("type", (!QR.getString("type").equals("-1") ? QR.getString("type") : "0"));
					params.put("name", iH.getItemNameByID(QR.getInt("itemid"), type));
					params.put("perStack", QR.getString("perstack"));
					params.put("sellFor", (!sf.equals("-1.0") && !sf.equals("-1") ? sf : "Not for sale!"));
					params.put("buyFor", (!bf.equals("-1.0") && !sf.equals("-1") ? bf : "No returns!"));

					if(conf.getBoolean("GiantShop.stock.useStock") == true) {
						params.put("stock", (!QR.getString("stock").equals("-1") ? QR.getString("stock") : "unlimited"));
						params.put("maxStock", (!QR.getString("maxstock").equals("-1") ? QR.getString("maxstock") : "unlimited"));
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
