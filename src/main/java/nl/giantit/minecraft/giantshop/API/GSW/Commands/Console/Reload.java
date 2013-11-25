package nl.giantit.minecraft.giantshop.API.GSW.Commands.Console;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;

import nl.giantit.minecraft.giantshop.API.GSW.GSWAPI;
import nl.giantit.minecraft.giantshop.API.GiantShopAPI;
import nl.giantit.minecraft.giantshop.API.conf;
import nl.giantit.minecraft.giantshop.GiantShop;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Giant
 */
public class Reload {
	
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static GSWAPI gA = GiantShopAPI.Obtain().getGSWAPI();
	
	public static void exec(CommandSender sender, String[] args) {
		conf c = gA.getConfig();
		c.reload();
		
		gA.reload();
		Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ADMIN, "confReload"));
	}
}
