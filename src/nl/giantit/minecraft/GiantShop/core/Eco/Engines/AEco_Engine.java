package nl.giantit.minecraft.GiantShop.core.Eco.Engines;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Eco.iEco;

import java.lang.reflect.Method;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import org.neocraft.AEco.AEco;

/**
 *
 * @author Giant
 */
public class AEco_Engine implements iEco {
	
	private GiantShop plugin;
	private org.neocraft.AEco.part.Economy.Economy eco = null;
	private Method createWallet = null;
	
	public AEco_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("AEco");

			if(ecoEn != null && ecoEn.isEnabled()) {
				eco = AEco.ECONOMY;
				
				try{
					createWallet = eco.getClass().getMethod("createWallet", String.class);
					createWallet.setAccessible(true);
				}catch(SecurityException e) {
				}catch(NoSuchMethodException e) {
				}catch(NullPointerException e) {
				}
				
				if(eco == null) {
					plugin.getLogger().log(Level.WARNING, "[" + plugin.getPubName() + "] Failed to hook into AEco!");
				}else{
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into AEco!");
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
		return (double) eco.cash(player);
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			int balance = eco.cash(player);
			amount = Math.ceil(amount);
			if((balance - amount) >= 0) {
				eco.set(player, ((int) (balance - amount)));
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
			int balance = eco.cash(player);
			eco.set(player, ((int) (balance + Math.ceil(amount))));
			return true;
		}
		return false;
	}
	
	public class EcoListener implements Listener {
		private AEco_Engine eco;
		
		public EcoListener(AEco_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("AEco");
				
				if(ecoEn != null && ecoEn.isEnabled()) {
					eco.eco = AEco.ECONOMY;
					try{
						createWallet = eco.eco.getClass().getMethod("createWallet", String.class);
						createWallet.setAccessible(true);
					}catch(SecurityException e) {
					}catch(NoSuchMethodException e) {
					}catch(NullPointerException e) {
					}
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into AEco!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("AEco")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully unhooked into AEco!");
				}
			}
		}
	}
}
