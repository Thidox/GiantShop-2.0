package nl.giantit.minecraft.GiantShop.core.Commands.Console;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.Misc.Messages.msgType;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.giantcore.perms.Permission;

import org.bukkit.command.CommandSender;

public class reload {

	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	public static void exec(CommandSender player, String[] args) {
		config.Obtain().reload();
		Heraut.say(player, mH.getConsoleMsg(msgType.ADMIN, "confReload"));
	}
}
