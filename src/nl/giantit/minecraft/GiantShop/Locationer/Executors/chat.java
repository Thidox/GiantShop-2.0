package nl.giantit.minecraft.GiantShop.Locationer.Executors;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.Locationer.core.Commands.chat.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class chat {
	private GiantShop plugin;
	private perm perm;
	
	public chat(GiantShop plugin) {
		this.plugin = plugin;
		perm = plugin.getPermMan();
	}

	public static boolean exec(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
			help.showHelp(player, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "list", "l")) {
			list.list(player, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "add", "a")) {
			add.add(player, args);
		}else{
			Heraut.say(player, "Command not found sending help!");
			help.showHelp(player, args);
		}
		
		return true;
	}
}
