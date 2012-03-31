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

import cosine.boseconomy.BOSEconomy;

/**
 *
 * @author Giant
 */
public class bose7_Engine implements iEco {
	
	private GiantShop plugin;
	private BOSEconomy eco;
	
	public bose7_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");

			if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getDescription().getVersion().startsWith("0.7")) {
				eco = (BOSEconomy) ecoEn;
				plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into BOSEconomy 7!");
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
		return eco.getPlayerMoneyDouble(player);
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			double balance = eco.getPlayerMoneyDouble(player);
			return eco.setPlayerMoney(player, (balance - amount), true);
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
			return eco.addPlayerMoney(player, amount, true);
		}
		
		return false;
	}
	
	public class EcoListener implements Listener {
		private bose7_Engine eco;
		
		public EcoListener(bose7_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
				
				if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getDescription().getVersion().startsWith("0.7")) {
					eco.eco = (BOSEconomy) ecoEn;
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into BOSEconomy 7!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("BOSEconomy")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully unhooked into BOSEconomy 7!");
				}
			}
		}
	}
}
