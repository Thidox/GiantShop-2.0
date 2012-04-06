package nl.giantit.minecraft.GiantShop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.Locationer.Locationer;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class add {
	
	private static config conf = config.Obtain();
	private static perm perms = perm.Obtain();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Locationer lH = GiantShop.getPlugin().getLocHandler();
	
	public static void add(Player player, String[] args) {
		if(perms.has(player, "giantshop.location.add")) {
			String name = (args.length > 1) ? args[1] : "unkown";
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "loc add");

			Heraut.say(mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
}
