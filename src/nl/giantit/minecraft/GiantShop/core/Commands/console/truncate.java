package nl.giantit.minecraft.GiantShop.core.Commands.console;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;

import org.bukkit.command.CommandSender;

import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class truncate {
	
	private static iDriver DB = Database.Obtain().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	public static void truncate(CommandSender sender, String[] args) {
		String type = "items";
		
		if(args.length > 1)
			type = args[1];
		
		if(type.equalsIgnoreCase("items")) {
			DB.Truncate("#__items").updateQuery();
			Heraut.say(sender, "Truncating items table!");
		}else if(type.equalsIgnoreCase("shops")) {
			DB.Truncate("#__shops").updateQuery();
			Heraut.say(sender, "Truncating shops table!");
		}else if(type.equalsIgnoreCase("discounts")) {
			DB.Truncate("#__discounts").updateQuery();
			Heraut.say(sender, "Truncating discounts table!");
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "truncate");

			Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
}
