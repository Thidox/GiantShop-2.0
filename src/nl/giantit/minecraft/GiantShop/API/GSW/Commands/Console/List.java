package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Console;

import java.util.HashMap;
import java.util.Map;
import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.API.GSW.Server.ShopSender;
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
public class List {
	
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static GSWAPI gA = GiantShopAPI.Obtain().getGSWAPI();
	
	public static void exec(CommandSender sender, String[] args) {
		Map<String, ShopSender> tA = gA.getTrustedAppsMap();

		if(tA.size() > 0) {
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
			int pages = ((int)Math.ceil((double)tA.size() / (double)perPage) < 1) ? 1 : (int)Math.ceil((double)tA.size() / (double)perPage);
			int start = (curPag * perPage) - perPage;

			if(curPag > pages) {
				HashMap<String, String> d = new HashMap<String, String>();
				d.put("list", "trusted app");
				d.put("pages", String.valueOf(pages));
				Heraut.say(sender, mH.getMsg(Messages.msgType.ERROR, "pageOverMax", d));
			}else{
				HashMap<String, String> d = new HashMap<String, String>();
				d.put("page", String.valueOf(curPag));
				d.put("maxPages", String.valueOf(pages));
				Heraut.say(sender, mH.getMsg(Messages.msgType.MAIN, "TrustedAppPageHead", d));
				for(Map.Entry<String, ShopSender> app : tA.entrySet()) {
					d = new HashMap<String, String>();
					d.put("appName", app.getKey());
					d.put("appURI", app.getValue().getHostURI());

					Heraut.say(sender, mH.getMsg(Messages.msgType.MAIN, "TrustedAppListEntry", d));
				}
			}
		}else{
			Heraut.say(sender, mH.getMsg(Messages.msgType.ERROR, "noWebApps"));
		}
	}
}
