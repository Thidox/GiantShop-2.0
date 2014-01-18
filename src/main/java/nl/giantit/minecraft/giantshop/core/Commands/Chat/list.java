package nl.giantit.minecraft.giantshop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.core.Items.Items;
import nl.giantit.minecraft.giantcore.database.query.Group;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.config;
import nl.giantit.minecraft.giantshop.core.Tools.Discount.Discounter;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class list {
	
	public static void list(Player player, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
		config conf = config.Obtain();
		Discounter disc = GiantShop.getPlugin().getDiscounter();
		if(perms.has(player, "giantshop.shop.list")) {
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
				Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "noItems"));
			}else if(curPag > pages) {
				Heraut.say(player, "&e[&3" + name + "&e]&c My Item list only has &e" + pages + " &cpages!!");
			}else{
				Heraut.say(player, "&e[&3" + name + "&e]&f Item list. Page: &e" + curPag + "&f/&e" + pages);
				
				for(int i = start; i < (((start + perPage) > QRes.size()) ? QRes.size() : (start + perPage)); i++) {
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
					
					HashMap<String, String> params = new HashMap<String, String>();
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
					// Future stuff
					// Probably am going to want to do this VERY different though :D
					/* if(conf.getBoolean("GiantShop.Location.useGiantShopLocation") == true) {
					 *		ArrayList<Indaface> shops = GiantShop.getPlugin().getLocationHandler().parseShops(entry.get("shops"));
					 *		for(Indaface shop : shops) {
					 *			if(shop.inShop(player.getLocation())) {
					 *				Heraut.say(player, msgs.getMsg(Messages.msgType.MAIN, "itemListEntry", params));
					 *				break;
					 *			}
					 *		}
					 * }else
					 */
					
					Heraut.say(player, msgs.getMsg(Messages.msgType.MAIN, "itemListEntry", params));
				}
			}
			
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "list");

			Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
