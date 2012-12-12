package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat;

import java.util.Arrays;
import nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat.Pickup.Pickup;
import nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat.Pickup.List;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class Pick {
	
	public static void exec(Player player, String[] args) {
		if(args.length >= 1) {
			String cmd = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			
			if(Misc.isAnyIgnoreCase(cmd, "list", "lis", "li", "l")) {
				List.exec(player, args);
			}else{
				Pickup.exec(player, cmd);
			}
		}else{
			Pickup.getAll(player);
		}
	}
}
