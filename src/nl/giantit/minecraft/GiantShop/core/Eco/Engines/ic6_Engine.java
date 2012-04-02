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

import com.iCo6.iConomy;
import com.iCo6.system.Holdings;
import com.iCo6.system.Accounts;

/**
 *
 * @author Giant
 */
public class ic6_Engine implements iEco {
	
	private GiantShop plugin;
	private iConomy eco;
	private Accounts accs;
	
	public ic6_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("iConomy");

			if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getClass().getName().equals("com.iCo6.iConomy")) {
				eco = (iConomy) ecoEn;
				accs = new Accounts();
				plugin.getLogger().log(Level.INFO, "Succesfully hooked into iConomy 6!");
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
		if(accs.exists(player)) {
			return accs.get(player).getHoldings().getBalance();
		}else
			return 0.0;
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			Holdings holdings = accs.get(player).getHoldings();
			if(holdings.hasEnough(amount)) {
				holdings.subtract(amount);
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
			accs.get(player).getHoldings().add(amount);
			return true;
		}
		return false;
	}
	
	public class EcoListener implements Listener {
		private ic6_Engine eco;
		
		public EcoListener(ic6_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("iConomy");
				
				if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getClass().getName().equals("com.iCo6.iConomy")) {
					eco.eco = (iConomy) ecoEn;
					eco.accs = new Accounts();
					plugin.getLogger().log(Level.INFO, "Succesfully hooked into iConomy 6!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("iConomy")) {
					eco.eco = null;
					eco.accs = null;
					plugin.getLogger().log(Level.INFO, "Succesfully unhooked into iConomy 6!");
				}
			}
		}
	}
}
