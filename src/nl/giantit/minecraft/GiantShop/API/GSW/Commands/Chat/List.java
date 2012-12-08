package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat;

import java.util.Set;
import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.perms.Permission;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class List {
	
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static GSWAPI gA = GiantShopAPI.Obtain().getGSWAPI();
	
	public static void exec(Player player, String[] args) {
		if(perms.has(player, "giantshop.api.web.list")) {
			Set<String> tA = gA.getTrustedApps();
			
			if(tA.size() > 0) {
				for(String app : tA) {
					// Nice and unclean for now :)
					Heraut.say(player, app);
				}
			}else{
				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noWebApps"));
			}
		}
	}
}
