package nl.giantit.minecraft.GiantShop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.perm;

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
	private static perm perms = perm.Obtain();
	
	private static void init() {
		entries = new ArrayList<String[]>();
		entries.add(new String[] {"loc", "Show GiantShopLocation help page 1", "giantshop.location.access"});
		entries.add(new String[] {"loc help|h|? (page)", "Show GiantShopLocation help page x", "giantshop.location.access"});
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
		
		int page;
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
		
		Heraut.savePlayer(player);
		
		if(uEntries.size() <= 0) {
			Heraut.say("&e[&3" + name + "&e]&c Sorry no help entries yet :(");
		}else if(curPag > pages) {
			Heraut.say("&e[&3" + name + "&e]&c My help list only has &e" + pages + " &cpages!!");
		}else{
			Heraut.say("&e[&3" + name + "&e]&f Help. Page: &e" + curPag + "&f/&e" + pages);

			for(int i = start; i < (((start + perPage) > uEntries.size()) ? uEntries.size() : (start + perPage)); i++) {
				String[] data = uEntries.get(i);

				String helpEntry = data[0];
				String description = data[1];
				String permission = data[2];
				Messages msg = GiantShop.getPlugin().getMsgHandler();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("command", helpEntry);
				params.put("description", description);
				
				Heraut.say(msg.getMsg(Messages.msgType.MAIN, "helpCommand", params));
			}
		}
	}
}
