package nl.giantit.minecraft.giantshop.core.Commands.Console;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.Commands.Console.Discount.*;

import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class discount {
	
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	public static void exec(CommandSender sender, String[] args) {
		if(args.length > 1) {
			if(Misc.isEitherIgnoreCase(args[1], "list", "l")) {
				List.exec(sender, args);
			}else if(Misc.isEitherIgnoreCase(args[1], "add", "a")) {
				Add.exec(sender, args);
			}else if(Misc.isEitherIgnoreCase(args[1], "update", "u")) {
				Update.exec(sender, args);
			}else if(Misc.isEitherIgnoreCase(args[1], "remove", "r")) {
				Remove.exec(sender, args);
			}else{
				try {
					Integer.parseInt(args[1]);
					List.exec(sender, args);
				}catch(NumberFormatException e) {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("command", "discount");
					
					Heraut.say(sender, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
				}
			}
		}else{
			List.exec(sender, args);
		}		
	}
}
