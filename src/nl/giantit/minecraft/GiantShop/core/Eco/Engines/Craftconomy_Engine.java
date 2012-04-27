package nl.giantit.minecraft.GiantShop.core.Eco.Engines;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Eco.iEco;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

import me.greatman.Craftconomy.Craftconomy;
import me.greatman.Craftconomy.Account;
import me.greatman.Craftconomy.AccountHandler;

/**
 *
 * @author Giant
 */
public class Craftconomy_Engine implements iEco {
	
	private GiantShop plugin;
	private Craftconomy eco = null;
	
	public Craftconomy_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("Craftconomy");

			if(ecoEn != null && ecoEn.isEnabled()) {
				eco = (Craftconomy) ecoEn;
				if(eco == null) {
					plugin.getLogger().log(Level.WARNING, "Failed to hook into Craftconomy!");
				}else{
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into Craftconomy!");
				}
			}
		}
	}
	
	@Override
	public boolean isLoaded() {
		return eco != null;
	}
	
	@Override
	public double getBalance(Player player) {
		return this.getBalance(player.getName());
	}
	
	@Override
	public double getBalance(String player) {
		Account acc = AccountHandler.getAccount(player);
		return acc.getDefaultBalance();
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			Account acc = AccountHandler.getAccount(player);
			if(acc.hasEnough(amount)) {
				acc.substractMoney(amount);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean deposit(Player player, double amount) {
		return this.deposit(player.getName(), amount);
	}
	
	@Override
	public boolean deposit(String player, double amount) {
		if(amount > 0) {
			Account acc = AccountHandler.getAccount(player);
			acc.addMoney(amount);
			return true;
		}
		
		return false;
	}
	
	public class EcoListener implements Listener {
		private Craftconomy_Engine eco;
		
		public EcoListener(Craftconomy_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("Craftconomy");
				
				if(ecoEn != null && ecoEn.isEnabled()) {
					eco.eco = (Craftconomy) ecoEn;
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into Craftconomy!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("Craftconomy")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "Succesfully unhooked into Craftconomy!");
				}
			}
		}
	}
}
