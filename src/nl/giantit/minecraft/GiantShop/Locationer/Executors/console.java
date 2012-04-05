package nl.giantit.minecraft.GiantShop.Locationer.Executors;

import nl.giantit.minecraft.GiantShop.GiantShop;

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

	public static boolean exec(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		return true;
	}
}
