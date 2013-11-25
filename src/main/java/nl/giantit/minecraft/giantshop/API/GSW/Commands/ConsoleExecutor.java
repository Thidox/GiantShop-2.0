package nl.giantit.minecraft.giantshop.API.GSW.Commands;

import nl.giantit.minecraft.giantcore.Misc.Heraut;

import nl.giantit.minecraft.giantshop.API.GSW.Commands.Console.Help;
import nl.giantit.minecraft.giantshop.API.GSW.Commands.Console.List;
import nl.giantit.minecraft.giantshop.API.GSW.Commands.Console.Reload;
import nl.giantit.minecraft.giantshop.Misc.Misc;

import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 *
 * @author Giant
 */
public class ConsoleExecutor {
	
	public static boolean exec(CommandSender sender, String[] args) {
		if(args.length >= 1) {
			String cmd = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			
			if(Misc.isAnyIgnoreCase(cmd, "help", "hel", "he", "h", "?")) {
				Help.showHelp(sender, args);
			}else if(Misc.isAnyIgnoreCase(cmd, "list", "lis", "li", "l")) {
				List.exec(sender, args);
			}else if(Misc.isAnyIgnoreCase(cmd, "reload", "reloa", "relo", "rel", "re", "r")) {
				Reload.exec(sender, args);
			}
		}else{
			Heraut.say(sender, "Ok, we have no friggin clue what you are on about, so what about we just send you our help page?");
			Help.showHelp(sender, args);
		}
		
		return true;
	}
	
}
