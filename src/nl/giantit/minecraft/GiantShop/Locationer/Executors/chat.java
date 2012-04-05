package nl.giantit.minecraft.GiantShop.Locationer.Executors;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Locationer.core.Commands.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class chat {
	private GiantShop plugin;
	private perm perm;
	
	public chat(GiantShop plugin) {
		this.plugin = plugin;
		perm = plugin.getPermMan();
	}

	public static boolean exec(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		
		Heraut.say(player, "test");
		
		return true;
	}
}
