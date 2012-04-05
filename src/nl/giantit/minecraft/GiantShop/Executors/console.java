package nl.giantit.minecraft.GiantShop.Executors;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.Commands.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Giant
 */
public class console {
	
	private GiantShop plugin;
	
	public console(GiantShop plugin) {
		this.plugin = plugin;
	}

	public boolean exec(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//on hold till after buy/sell command
		if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
			//done
			help.showConsoleHelp(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "sendhelp", "sh")) {
			//done
			help.sendConsoleHelp(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "list", "l")) {
			//done
			//needs testing
			list.listConsole(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "check", "c")) {
			//done
			//needs testing
			check.check(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "add", "a")) {
			//done
			add.addConsole(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "update", "u")) {
			//done
			//needs testing
			update.update(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "remove", "r")) {
			//done
			//needs testing
			remove.remove(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "addStock", "as")) {
			//stalled
		}else if(Misc.isEitherIgnoreCase(args[0], "import", "i")) {
			//stalled
		}else if(Misc.isEitherIgnoreCase(args[0], "export", "e")) {
			//in dev
			impexp.exp(sender, args);
		}else{
			sender.sendMessage("[" + plugin.getName() + "] Command not found! See help for more information.");
		}
			
		return true;
	}
}
