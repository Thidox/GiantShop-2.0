package nl.giantit.minecraft.giantshop.core.Commands;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.Commands.Console.*;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Giant
 */
public class ConsoleExecutor {
	
	private GiantShop plugin;
	
	public ConsoleExecutor(GiantShop plugin) {
		this.plugin = plugin;
	}

	public boolean exec(CommandSender sender, String[] args) {
		//on hold till after buy/sell command
		if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
			//done
			help.showHelp(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "sendhelp", "sh")) {
			//done
			help.sendHelp(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "list", "l")) {
			//done
			//needs testing
			list.list(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "check", "c")) {
			//done
			//needs testing
			check.check(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "add", "a")) {
			//done
			add.add(sender, args);
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
			//in dev
			impexp.imp(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "importLegacy", "iL")) {
			//stalled
			impexp.impLegacy(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "export", "e")) {
			//done
			//needs testing
			impexp.exp(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "truncate", "t")) {
			//done
			//needs testing
			truncate.truncate(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "discount", "d")) {
			discount.exec(sender, args);
		}else if(Misc.isEitherIgnoreCase(args[0], "reload", "rel")) {
			reload.exec(sender, args);
		}else{
			sender.sendMessage("[" + plugin.getName() + "] Command not found! See help for more information.");
		}
			
		return true;
	}
}
