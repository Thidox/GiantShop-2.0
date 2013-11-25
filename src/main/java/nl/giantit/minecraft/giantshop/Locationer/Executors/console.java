package nl.giantit.minecraft.giantshop.Locationer.Executors;

import nl.giantit.minecraft.giantshop.GiantShop;

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
		return true;
	}
}
