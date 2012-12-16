package nl.giantit.minecraft.GiantShop.core.Commands.Chat;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
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
			
			if(args.length < 2) {
				mH.getMsg(Messages.msgType.ERROR, "syntaxError");
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
			fields.add("perStack");
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("stock");
			fields.add("maxStock");
			
			HashMap<String, HashMap<String, String>> where = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> t = new HashMap<String, String>();
			if(conf.getBoolean("GiantShop.stock.hideEmptyStock")) {
				t.put("kind", "NOT");
				t.put("data", "0");
				where.put("stock", t);
			}
			
			ArrayList<ItemID> iList = iH.getItemIDsByPart(args[1]);
			int i = 0;
			for(ItemID iID : iList) {
				t = new HashMap<String, String>();
				if(i > 0) {
					t.put("type", "OR");
				}else{
					++i;
				}
				
				
				t.put("group", "START");
				t.put("kind", "INT");
				t.put("data", String.valueOf(iID.getId()));
				where.put("itemID", t);
				t = new HashMap<String, String>();
				
				t.put("group", "END");
				t.put("kind", "INT");
				t.put("data", String.valueOf((iID.getType() == null || iID.getType() == 0 ? -1 : iID.getType())));
				where.put("type", t);
			}
			
			HashMap<String, String> order = new HashMap<String, String>();
			order.put("itemID", "ASC");
			order.put("type", "ASC");
			ArrayList<HashMap<String, String>> data = DB.select(fields).from("#__items").where(where, true).orderBy(order).execQuery();
			
			int pages = ((int)Math.ceil((double)data.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)data.size() / (double)perPage);
			int start = (curPag * perPage) - perPage;
			
			
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "search");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
}
