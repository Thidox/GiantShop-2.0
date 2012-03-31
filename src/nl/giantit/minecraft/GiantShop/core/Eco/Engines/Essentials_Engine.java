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

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

/**
 *
 * @author Giant
 */
public class Essentials_Engine implements iEco {
	
	private GiantShop plugin;
	private Essentials eco;
	
	private boolean createAcc(String player) {
		if(Economy.playerExists(player))
			return false;
		
		Economy.createNPC(player);
		return true;
	}
	
	public Essentials_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		if(eco == null) {
			Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("Essentials");

			if(ecoEn != null && ecoEn.isEnabled()) {
				eco = (Essentials) ecoEn;
				plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into Essentials economy!");
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
		double balance = 0.0;
		try {
			if(!Economy.playerExists(player)) {
				createAcc(player);
			}
			
			balance = Economy.getMoney(player);
		}catch(UserDoesNotExistException e) {
			createAcc(player);
			balance = 0.0;
		}
		
		return balance;
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		if(amount > 0) {
			try {
				if(!Economy.playerExists(player)) {
					createAcc(player);
				}

				Economy.subtract(player, amount);
				return true;
			}catch(NoLoanPermittedException e) {
			}catch(UserDoesNotExistException e) {
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
			try {
				if(!Economy.playerExists(player)) {
					createAcc(player);
				}

				Economy.add(player, amount);
				return true;
			}catch(NoLoanPermittedException e) {
			}catch(UserDoesNotExistException e) {
			}
		}
		
		return false;
	}
	
	public class EcoListener implements Listener {
		private Essentials_Engine eco;
		
		public EcoListener(Essentials_Engine eco) {
			this.eco = eco;
		}
		
		@EventHandler()
		public void onPluginEnable(PluginEnableEvent event) {
			if(eco.eco == null) {
				Plugin ecoEn = plugin.getServer().getPluginManager().getPlugin("Essentials");
				
				if(ecoEn != null && ecoEn.isEnabled()) {
					eco.eco = (Essentials) ecoEn;
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully hooked into Essentials economy!");
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			if(eco.eco != null) {
				if(event.getPlugin().getDescription().getName().equals("Essentials")) {
					eco.eco = null;
					plugin.getLogger().log(Level.INFO, "[" + plugin.getPubName() + "] Succesfully unhooked into Essentials economy!");
				}
			}
		}
	}
}
