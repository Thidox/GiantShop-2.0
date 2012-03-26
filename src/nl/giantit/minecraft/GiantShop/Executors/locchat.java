package nl.giantit.minecraft.GiantShop.Executors;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.Commands.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class locchat {
	
	private GiantShop plugin;
	private perm perm;
	
	public locchat(GiantShop plugin) {
		this.plugin = plugin;
		perm = plugin.getPermMan();
	}

	public boolean exec(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		return true;
	}
}
