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

import me.mjolnir.mineconomy.internal.MCCom;
import me.mjolnir.mineconomy.MineConomy;

/**
 *
 * @author Giant
 */
public class MineConomy_Engine implements iEco {
	
	private GiantShop plugin;
	private MineConomy eco = null;
	
	public MineConomy_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("MineConomy");

			if(ecoEn != null && ecoEn.isEnabled()) {
				eco = (MineConomy) ecoEn;
				if(eco == null) {
					plugin.getLogger().log(Level.WARNING, "Failed to hook into MineConomy!");
				}else{
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into MineConomy!");
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
		return MCCom.getExternalBalance(player);
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			double b = MCCom.getExternalBalance(player);
			if((b - amount) > 0) {
				MCCom.setExternalBalance(player, (b - amount));
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
			double b = MCCom.getExternalBalance(player);
			MCCom.setExternalBalance(player, (b + amount));
			return true;
		}
		
		return false;
	}
	
	public class EcoListener implements Listener {
		private MineConomy_Engine eco;
		
		public EcoListener(MineConomy_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("MineConomy");
				
				if(ecoEn != null && ecoEn.isEnabled()) {
					eco.eco = (MineConomy) ecoEn;
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into MineConomy!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("MineConomy")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "Succesfully unhooked into MineConomy!");
				}
			}
		}
	}
}
