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

import is.currency.Currency;
import is.currency.syst.AccountContext;

/**
 *
 * @author Giant
 */
public class CurrencyCore_Engine implements iEco {
	
	private GiantShop plugin;
	private Currency eco = null;
	
	public CurrencyCore_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("CurrencyCore");

			if(ecoEn != null && ecoEn.isEnabled()) {
				eco = (Currency) ecoEn;
				if(eco == null) {
					plugin.getLogger().log(Level.WARNING, "Failed to hook into CurrencyCore!");
				}else{
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into CurrencyCore!");
				}
			}
		}
	}
	
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
		AccountContext acc = eco.getAccountManager().getAccount(player);
		if(acc == null)
			return 0.0;
		
		return acc.getBalance();
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		AccountContext acc = eco.getAccountManager().getAccount(player);
		if(acc != null) {
			if(acc.hasBalance(amount)) {
				acc.subtractBalance(amount);
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
		AccountContext acc = eco.getAccountManager().getAccount(player);
		if(acc != null) {
			acc.addBalance(amount);
			return true;
		}
		
		return false;
	}
	
	public class EcoListener implements Listener {
		private CurrencyCore_Engine eco;
		
		public EcoListener(CurrencyCore_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("CurrencyCore");
				
				if(ecoEn != null && ecoEn.isEnabled()) {
					eco.eco = (Currency) ecoEn;
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into CurrencyCore!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("CurrencyCore")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "Succesfully unhooked into CurrencyCore!");
				}
			}
		}
	}
}
