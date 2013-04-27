package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat;

import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.API.GSW.Server.ShopSender;
import nl.giantit.minecraft.GiantShop.API.GSW.Server.resultHandlers.RegisterHandler;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.giantcore.perms.Permission;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class Register {
	
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static GSWAPI gA = GiantShopAPI.Obtain().getGSWAPI();
	
	public static void exec(Player player, String[] args) {
		if(perms.has(player, "giantshop.api.web.register")) {
			if(args.length >= 1) {
				for(String app : args) {
					HashMap<String, String> data = new HashMap<String, String>();
					if(gA.isTrustedApp(app)) {
						ShopSender ss = gA.getTrustedApp(app);
						RegisterHandler rH = new RegisterHandler(ss.getAppName(), player);
						try {
							ss.write("REGISTER " + player.getName(), rH);
						}catch(Exception e) {
							GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + app);
						}
					}else{
						data.put("app", app);
						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "webAppNotTrusted", data));
					}
				}
			}else{
				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noWebAppSpecified"));
			}
		}
	}
}
