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

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;

/**
 *
 * @author Giant
 */
public class ic4_Engine implements iEco {
	
	private GiantShop plugin;
	private iConomy eco;
	
	public ic4_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("iConomy");

			if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getClass().getName().equals("com.nijiko.coelho.iConomy.iConomy.class")) {
				eco = (iConomy) ecoEn;
				plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into iConomy 4!");
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
		Account account = eco.getBank().getAccount(player);
		if(account == null) {
			eco.getBank().addAccount(player);
			account = eco.getBank().getAccount(player);
		}
		
		return account.getBalance();
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			Account account = eco.getBank().getAccount(player);
			if(account == null) {
				eco.getBank().addAccount(player);
				account = eco.getBank().getAccount(player);
			}
			
			double balance = account.getBalance();
			amount = Math.ceil(amount);
			if((balance - amount) >= 0) {
				account.subtract(amount);
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
			Account account = eco.getBank().getAccount(player);
			if(account == null) {
				eco.getBank().addAccount(player);
				account = eco.getBank().getAccount(player);
			}
			
			account.add(amount);
			return true;
		}
		return false;
	}
	
	public class EcoListener implements Listener {
		private ic4_Engine eco;
		
		public EcoListener(ic4_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("iConomy");
				
				if(ecoEn != null && ecoEn.isEnabled() && ecoEn.getClass().getName().equals("com.nijiko.coelho.iConomy.iConomy.class")) {
					eco.eco = (iConomy) ecoEn;
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into iConomy 4!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("iConomy")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully unhooked into iConomy 4!");
				}
			}
		}
	}
}
