package nl.giantit.minecraft.GiantShop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.Locationer.Locationer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class update {
	
	private static config conf = config.Obtain();
	private static perm perms = perm.Obtain();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Locationer lH = GiantShop.getPlugin().getLocHandler();
	
	public static void update(Player player, String[] args) {
		if(perms.has(player, "giantshop.location.update")) {
			
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "loc update");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
