package nl.giantit.minecraft.giantshop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.Misc.Messages.msgType;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.config;
import nl.giantit.minecraft.giantcore.perms.Permission;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class reload {

	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	public static void exec(Player player, String[] args) {
		if(perms.has(player, "giantshop.admin.reload")) {
			config.Obtain().reload();
			Heraut.say(player, mH.getMsg(msgType.ADMIN, "confReload"));
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "reload");
			
			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
