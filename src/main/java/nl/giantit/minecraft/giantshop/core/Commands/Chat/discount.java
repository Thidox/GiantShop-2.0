package nl.giantit.minecraft.GiantShop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.Commands.Chat.Discount.*;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class discount {
	
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	public static void exec(Player player, String[] args) {
		if(args.length > 1) {
			if(Misc.isEitherIgnoreCase(args[1], "list", "l")) {
				List.exec(player, args);
			}else if(Misc.isEitherIgnoreCase(args[1], "add", "a")) {
				Add.exec(player, args);
			}else if(Misc.isEitherIgnoreCase(args[1], "update", "u")) {
				Update.exec(player, args);
			}else if(Misc.isEitherIgnoreCase(args[1], "remove", "r")) {
				Remove.exec(player, args);
			}else{
				try {
					Integer.parseInt(args[1]);
					List.exec(player, args);
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "discount");
					
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}
		}else{
			List.exec(player, args);
		}		
	}
}
