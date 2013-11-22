package nl.giantit.minecraft.GiantShop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Locationer.Locationer;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.giantcore.Database.iDriver;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class remove {
	
	private static config conf = config.Obtain();
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Locationer lH = GiantShop.getPlugin().getLocHandler();
	
	public static void remove(Player player, String[] args) {
		if(perms.has(player, "giantshop.location.remove")) {
			String name = null;
			String world = null;

			for(int i = 0; i < args.length; i++) {
				if(args[i].startsWith("-n:")) {
					name = args[i].replaceFirst("-n:", "");
					continue;
				}else if(args[i].startsWith("-w:")) {
					world = args[i].replaceFirst("-w:", "");
					continue;
				}
			}
			
			if(name != null) {
				if(world == null)
					world = player.getWorld().getName();
				
				iDriver DB = GiantShop.getPlugin().getDB().getEngine();

				ArrayList<String> fields = new ArrayList<String>();
				fields.add("id");
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("name", name);
				data.put("world", world);

				DB.select(fields).from("#__shops").where(data);
				if(DB.execQuery().size() != 0) {
					DB.delete("#__shops").where(data).updateQuery();
					
					lH.removeShop(name, world);
					
					data = new HashMap<String, String>();
					data.put("shop", name);
					data.put("world", world);
					
					Heraut.say(player, mH.getMsg(Messages.msgType.ADMIN, "shopRemoved", data));
				}else{
					data = new HashMap<String, String>();
					data.put("shop", name);
					data.put("world", world);
					
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "shopNotFound", data));
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "loc remove");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "loc remove");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
}
