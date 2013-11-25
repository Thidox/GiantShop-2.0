package nl.giantit.minecraft.giantshop.core.Commands.Console;

import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;

import nl.giantit.minecraft.giantshop.GiantShop;

import org.bukkit.command.CommandSender;

import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class truncate {
	
	private static Driver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	public static void truncate(CommandSender sender, String[] args) {
		String type = "items";
		
		if(args.length > 1)
			type = args[1];
		
		if(type.equalsIgnoreCase("items")) {
			DB.Truncate("#__items").exec();
			Heraut.say(sender, "Truncating items table!");
		}else if(type.equalsIgnoreCase("shops")) {
			DB.Truncate("#__shops").exec();
			Heraut.say(sender, "Truncating shops table!");
		}else if(type.equalsIgnoreCase("discounts")) {
			DB.Truncate("#__discounts").exec();
			Heraut.say(sender, "Truncating discounts table!");
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "truncate");

			Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
}
