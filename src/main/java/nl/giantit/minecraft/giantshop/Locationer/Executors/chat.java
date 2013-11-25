package nl.giantit.minecraft.giantshop.Locationer.Executors;

import nl.giantit.minecraft.giantcore.Misc.Heraut;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Locationer.core.Commands.chat.*;
import nl.giantit.minecraft.giantshop.Misc.Misc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class chat {
	
	private GiantShop plugin;
	
	
	public chat(GiantShop plugin) {
		this.plugin = plugin;
	}

	public boolean exec(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
			help.showHelp(player, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "list", "l")) {
			list.list(player, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "add", "a")) {
			add.add(player, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "update", "u")) {
			update.update(player, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "remove", "r")) {
			remove.remove(player, args);
		}else{
			Heraut.say(player, "Command not found sending help!");
			help.showHelp(player, args);
		}
		
		return true;
	}
}
