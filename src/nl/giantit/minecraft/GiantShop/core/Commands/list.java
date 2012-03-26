package nl.giantit.minecraft.GiantShop.core.Commands;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class list {
	
	public static void list(Player player, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		perm perms = perm.Obtain();
		config conf = config.Obtain();
		if(perms.has(player, "giantshop.shop.list")) {
			String name = GiantShop.getPlugin().getPubName();
			int perPage = conf.getInt("GiantShop.global.perPage");
			int curPag = 0;
			
			if(args.length >= 2) {
				try{
					curPag = Integer.parseInt(args[1]);
				}catch(Exception e) {
					curPag = 1;
				}
			}else
				curPag = 1;

			curPag = (curPag > 0) ? curPag : 1;
		
			db DB = db.Obtain();
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("itemID");
			fields.add("type");
			fields.add("perStack");
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("stock");
			ArrayList<HashMap<String, String>> data = DB.select(fields).execQuery();
			
			int pages = ((int)Math.ceil((double)data.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)data.size() / (double)perPage);
			int start = (curPag * perPage) - perPage;
			if(data.size() <= 0) {
				Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "noItems"));
			}else if(curPag > pages) {
				Heraut.say("&e[&3" + name + "&e]&c My Item list only has &e" + pages + " &cpages!!");
			}else{
				
			}
			
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "list");

			Heraut.say(player, msgs.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
	public static void listConsole(CommandSender sender, String[] args) {
		Messages msgs = GiantShop.getPlugin().getMsgHandler();
		Items iH = GiantShop.getPlugin().getItemHandler();
		config conf = config.Obtain();
		db DB = db.Obtain();
		ArrayList<String> fields = new ArrayList<String>();
		
	}
}
