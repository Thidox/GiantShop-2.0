package nl.giantit.minecraft.GiantShop.API.GSW.Commands;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import java.util.Arrays;
import nl.giantit.minecraft.GiantShop.API.GSL.GSLAPI;
import nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat.*;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class ChatExecutor {
	
	public static boolean exec(CommandSender sender, String[] args) {
		GSLAPI gsl = GiantShopAPI.Obtain().getGSLAPI();
		Player player = (Player) sender;
		
		if(args.length >= 1) {
			String cmd = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			
			if(Misc.isAnyIgnoreCase(cmd, "help", "hel", "he", "h", "?")) {
				if(gsl.isProtectedCommand("gswhelp")) {
					if(gsl.canUse(player)) {
						Help.showHelp(player, args);
					}
				}else{
					Help.showHelp(player, args);
				}
			}else if(Misc.isAnyIgnoreCase(cmd, "list", "li", "l")) {
				if(gsl.isProtectedCommand("gswlist")) {
					if(gsl.canUse(player)) {
						List.exec(player, args);
					}
				}else{
					List.exec(player, args);
				}
			}else if(Misc.isAnyIgnoreCase(cmd, "register", "reg", "r")) {
				if(gsl.isProtectedCommand("gswregister")) {
					if(gsl.canUse(player)) {
						Register.exec(player, args);
					}
				}else{
					Register.exec(player, args);
				}
			}else if(Misc.isAnyIgnoreCase(cmd, "pickup", "pick", "pu", "p")) {
				Pick.exec(player, args);
			}else{
				// admin command stuff
			}
		}else{
			Heraut.say(player, "Ok, we have no friggin clue what you are on about, so what about we just send you our help page?");
			Help.showHelp(player, args);
		}
		
		return true;
	}
}
