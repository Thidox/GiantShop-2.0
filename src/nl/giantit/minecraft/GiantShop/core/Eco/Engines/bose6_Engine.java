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
@SuppressWarnings("deprecation")
public class bose6_Engine implements iEco {
	
	private GiantShop plugin;
	private BOSEconomy eco;
	
	public bose6_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		plugin.getLogger().log(Level.WARNING, "BOSEconomy 6 is HEAVILY outdated please upgrade!");
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");

			if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getDescription().getVersion().startsWith("0.6")) {
				eco = (BOSEconomy) ecoEn;
				plugin.getLogger().log(Level.INFO, "Succesfully hooked into BOSEconomy 6!");
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
		return (double) eco.getPlayerMoney(player);
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			double balance = eco.getPlayerMoneyDouble(player);
			return eco.setPlayerMoney(player, (int) (balance - Math.round(amount)), true);
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
			return eco.addPlayerMoney(player, (int) Math.round(amount), true);
		}
		
		return false;
	}
	
	public class EcoListener implements Listener {
		private bose6_Engine eco;
		
		public EcoListener(bose6_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
				
				if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getDescription().getVersion().startsWith("0.6")) {
					eco.eco = (BOSEconomy) ecoEn;
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into BOSEconomy 6!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("BOSEconomy")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "Succesfully unhooked into BOSEconomy 6!");
				}
			}
		}
	}
}
