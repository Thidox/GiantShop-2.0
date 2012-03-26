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
public class locconsole {
	
	private GiantShop plugin;
	
	public locconsole(GiantShop plugin) {
		this.plugin = plugin;
	}

	public boolean exec(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		return true;
	}
}
