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

import me.ashtheking.currency.Currency;
import me.ashtheking.currency.CurrencyList;

/**
 *
 * @author Giant
 */
public class MultiCurrency_Engine implements iEco {
	
	private GiantShop plugin;
	private Currency eco = null;
	
	public MultiCurrency_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("MultiCurrency");

			if(ecoEn != null && ecoEn.isEnabled()) {
				eco = (Currency) ecoEn;
				if(eco == null) {
					plugin.getLogger().log(Level.WARNING, "Failed to hook into MultiCurrency!");
				}else{
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into MultiCurrency!");
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
		return CurrencyList.getValue((String) CurrencyList.maxCurrency(player)[0], player);
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			if(CurrencyList.hasEnough(player, amount)) {
				CurrencyList.subtract(player, amount);
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
			CurrencyList.add(player, amount);
			return true;
		}
		
		return false;
	}
	
	public class EcoListener implements Listener {
		private MultiCurrency_Engine eco;
		
		public EcoListener(MultiCurrency_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("MultiCurrency");
				
				if(ecoEn != null && ecoEn.isEnabled()) {
					eco.eco = (Currency) ecoEn;
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into MultiCurrency!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("MultiCurrency")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "Succesfully unhooked into MultiCurrency!");
				}
			}
		}
	}
}
