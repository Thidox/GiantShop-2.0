package nl.giantit.minecraft.GiantShop.core.Eco.Engines;

import nl.giantit.minecraft.GiantShop.GiantShop;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Giant
 */
public class AEco_Engine implements iEco {
	
	private GiantShop plugin;
	private Integer eco;
	
	public AEco_Engine(GiantShop plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EcoListener(this), plugin);
		
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
		return 0.0;
	}
	
	@Override
	public boolean withdraw(Player player, double amount) {
		return this.withdraw(player.getName(), amount);
	}
	
	@Override
	public boolean withdraw(String player, double amount) {
		return false;
	}
	
	@Override
	public boolean deposit(Player player, double amount) {
		return this.deposit(player.getName(), amount);
	}
	
	@Override
	public boolean deposit(String player, double amount) {
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
					eco.eco = 0;
				}
			}
		}
		
		@EventHandler()
		public void onPluginDisable(PluginDisableEvent event) {
			
		}
	}
}
