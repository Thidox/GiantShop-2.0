package nl.giantit.minecraft.GiantShop.core.Eco;

import org.bukkit.entity.Player;
/**
 *
 * @author Giant
 */
public interface iEco {
	
	public boolean isLoaded();
	
	public double getBalance(Player player);
	public double getBalance(String player);
	
	public boolean withdraw(Player player, double amount);
	public boolean withdraw(String player, double amount);
	
	public boolean deposit(Player player, double amount);
	public boolean deposit(String player, double amount);
}
