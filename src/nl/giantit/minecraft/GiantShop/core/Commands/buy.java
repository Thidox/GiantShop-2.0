package nl.giantit.minecraft.GiantShop.core.Commands;

import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.perm;

import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class buy {
	
	public static void buy(Player player, String[] args) {
		config conf = config.Obtain();
		db database = db.Obtain();
		perm perms = perm.Obtain();
		
		Heraut.savePlayer(player);
		Heraut.say("test");
	}
	
	public static void gift(Player player, String[] args) {
		config conf = config.Obtain();
		db database = db.Obtain();
		perm perms = perm.Obtain();
		
		Heraut.savePlayer(player);
		Heraut.say("test");
	}
}
