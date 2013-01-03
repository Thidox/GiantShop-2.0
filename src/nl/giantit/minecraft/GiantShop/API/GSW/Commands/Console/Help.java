package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Console;

import java.util.ArrayList;
import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Giant
 */
public class Help {
	
	private static ArrayList<String[]> entries = new ArrayList<String[]>();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	private static void init() {
		entries = new ArrayList<String[]>();
		entries.add(new String[] {"gsw", "Show GiantShopWeb API help page 1", "null"});
		//entries.add(new String[] {"gsw help|h|? (page)", "Show GiantShopWeb API page x", "null"});
		entries.add(new String[] {"gsw sendhelp|sh [receiver] (page)", "Send Show GiantShopWeb API help page x to player y", "giantshop.admin.sendhelp"});
		entries.add(new String[] {"gsw list|l (page)", "Show all trusted web apps", "giantshop.api.web.list"});
	}
	
	public static void showHelp(CommandSender sender, String[] args) {
		if(entries.isEmpty())
			init();
		
		String name = GiantShop.getPlugin().getName() + " Web API (v" + GiantShopAPI.Obtain().getGSWAPI().getAPIVersion() + ")";
		int perPage = config.Obtain().getInt("GiantShop.global.perPage");
		int curPag = 0;
		
		if(args.length >= 1) {
			try{
				curPag = Integer.parseInt(args[0]);
			}catch(Exception e) {
				curPag = 1;
			}
		}else
			curPag = 1;
		
		curPag = (curPag > 0) ? curPag : 1;
		int pages = ((int)Math.ceil((double)entries.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)entries.size() / (double)perPage);
		int start = (curPag * perPage) - perPage;
		
		if(entries.size() <= 0) {
			Heraut.say(sender, "&e[&3" + name + "&e]" + mH.getConsoleMsg(Messages.msgType.ERROR, "noHelpEntries"));
		}else if(curPag > pages) {
			HashMap<String, String> d = new HashMap<String, String>();
			d.put("list", "help");
			d.put("pages", String.valueOf(pages));
			Heraut.say(sender, "&e[&3" + name + "&e]" + mH.getConsoleMsg(Messages.msgType.ERROR, "pageOverMax", d));
		}else{
			HashMap<String, String> d = new HashMap<String, String>();
			d.put("page", String.valueOf(curPag));
			d.put("maxPages", String.valueOf(pages));
			Heraut.say(sender, "&e[&3" + name + "&e]" + mH.getConsoleMsg(Messages.msgType.MAIN, "helpPageHead", d));

			for(int i = start; i < (((start + perPage) > entries.size()) ? entries.size() : (start + perPage)); i++) {
				String[] data = entries.get(i);

				String helpEntry = data[0];
				String description = data[1];
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("command", helpEntry);
				params.put("description", description);
				
				Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.MAIN, "helpCommand", params));
			}
		}
	}
}
