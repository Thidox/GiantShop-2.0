package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Console;

import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.API.conf;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
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
