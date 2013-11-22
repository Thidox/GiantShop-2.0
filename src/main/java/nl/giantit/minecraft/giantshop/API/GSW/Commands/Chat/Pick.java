package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat;

import java.util.Arrays;
import nl.giantit.minecraft.GiantShop.API.GSL.GSLAPI;
import nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat.Pickup.Pickup;
import nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat.Pickup.List;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class Pick {
	
	public static void exec(Player player, String[] args) {
		GSLAPI gsl = GiantShopAPI.Obtain().getGSLAPI();
		if(args.length >= 1) {
			String cmd = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			
			if(Misc.isAnyIgnoreCase(cmd, "list", "lis", "li", "l")) {
				if(gsl.isProtectedCommand("pickuplist")) {
					if(gsl.canUse(player)) {
						List.exec(player, args);
					}
				}else{
					List.exec(player, args);
				}
			}else if(Misc.isAnyIgnoreCase(cmd, "all", "al", "a")) {
				if(gsl.isProtectedCommand("pickupall")) {
					if(gsl.canUse(player)) {
						Pickup.getAll(player);
					}
				}else{
					Pickup.getAll(player);
				}
			}else{
				if(gsl.isProtectedCommand("pickupsingle")) {
					if(gsl.canUse(player)) {
						Pickup.exec(player, cmd);
					}
				}else{
					Pickup.exec(player, cmd);
				}
			}
		}else{
			if(gsl.isProtectedCommand("pickupall")) {
				if(gsl.canUse(player)) {
					Pickup.getAll(player);
				}
			}else{
				Pickup.getAll(player);
			}
		}
	}
}
