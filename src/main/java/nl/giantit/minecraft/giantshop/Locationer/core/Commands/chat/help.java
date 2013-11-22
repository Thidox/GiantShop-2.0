package nl.giantit.minecraft.GiantShop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class help {
	
	private static ArrayList<String[]> entries = new ArrayList<String[]>();
	private static config conf = config.Obtain();
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	
	private static void init() {
		entries = new ArrayList<String[]>();
		entries.add(new String[] {"loc", "Show GiantShopLocation help page 1", "giantshop.location.access"});
		entries.add(new String[] {"loc help|h|? (page)", "Show GiantShopLocation help page x", "giantshop.location.access"});
		entries.add(new String[] {"loc list|l (page)", "Show GiantShopLocation shop list page x", "giantshop.location.list"});
		entries.add(new String[] {"loc add|a| (name)", "Finish the creation of the GiantShopLocation shop", "giantshop.location.create"});
		entries.add(new String[] {"loc update|u select|s [-n:name] (-w:[world])", "Select a GSL shop for updating", "giantshop.location.update"});
		entries.add(new String[] {"loc update|u show", "Show current details for the selected GSL shop", "giantshop.location.update"});
		entries.add(new String[] {"loc update|u set -t:name -n:[name]", "Update the shops name", "giantshop.location.update"});
		entries.add(new String[] {"loc update|u set -t:world", "Update the world the shop is located in", "giantshop.location.update"});
		entries.add(new String[] {"loc update|u set -t:loc", "Same as -t:world, but does not update shops world", "giantshop.location.update"});
		entries.add(new String[] {"loc update|u save", "Saves the changes made to the GSL shop", "giantshop.location.update"});
		entries.add(new String[] {"loc remove|r [-n:name] (-w:[world])", "Remove a shop from GSL", "giantshop.location.remove"});
	}
	
	public static void showHelp(Player player, String[] args) {
		if(entries.isEmpty())
			init();
		
		ArrayList<String[]> uEntries = new ArrayList<String[]>();
		for(int i = 0; i < entries.size(); i++) {
			String[] data = entries.get(i);

			String permission = data[2];

			if(permission.equalsIgnoreCase("null") || perms.has(player, permission)) {
				uEntries.add(data);				
			}else{
				continue;
			}
		}
		
		String name = GiantShop.getPlugin().getPubName();
		int perPage = conf.getInt("GiantShop.Location.perPage");
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
		
		int pages = ((int)Math.ceil((double)uEntries.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)uEntries.size() / (double)perPage);
		int start = (curPag * perPage) - perPage;
		
		if(uEntries.size() <= 0) {
			Heraut.say(player, "&e[&3" + name + "&e]&c Sorry no help entries yet :(");
		}else if(curPag > pages) {
			Heraut.say(player, "&e[&3" + name + "&e]&c My help list only has &e" + pages + " &cpages!!");
		}else{
			Heraut.say(player, "&e[&3" + name + "&e]&f Help. Page: &e" + curPag + "&f/&e" + pages);

			for(int i = start; i < (((start + perPage) > uEntries.size()) ? uEntries.size() : (start + perPage)); i++) {
				String[] data = uEntries.get(i);

				String helpEntry = data[0];
				String description = data[1];
				
				Messages msg = GiantShop.getPlugin().getMsgHandler();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("command", helpEntry);
				params.put("description", description);
				
				Heraut.say(player, msg.getMsg(Messages.msgType.MAIN, "helpCommand", params));
			}
		}
	}
}
