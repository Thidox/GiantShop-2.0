package nl.giantit.minecraft.GiantShop.core.Eco.Engines;

import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class bose7_Engine implements iEco {
	
	@Override
	public boolean isLoaded() {
		return false;
	}
	
	@Override
	public double getBalance(Player player) {
		return this.getBalance(player.getName());
	}
	
	@Override
	public double getBalance(String player) {
		return 0.0;
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		return false;
	}
	
	@Override
	public boolean deposit(Player player, double amount) {
		return this.deposit(player.getName(), amount);
	}
	
	@Override
	public boolean deposit(String player, double amount) {
		return false;
	}
}
