package nl.giantit.minecraft.GiantShop.API.GSW.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class Executor implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gsw")) {
			if(!(sender instanceof Player)){
				return ConsoleExecutor.exec(sender, args);
			}
			
			return ChatExecutor.exec(sender, args);
		}
		
		return false;
	}
	
}
