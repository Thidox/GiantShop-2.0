package nl.giantit.minecraft.giantshop.core;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.Updater.UpdateType;
import nl.giantit.minecraft.giantshop.core.Updater.Config.confUpdate;

import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class config {
	
	private static config instance = null;

	private GiantShop plugin;
	private YamlConfiguration c;
	private File file;
	private double version = 1.2;

	private config(GiantShop p) {
		this.plugin = p;
	}
	
	public boolean isLoaded() {
		return null != c;
	}
	
	public void loadConfig(File file) {
		this.file = file;
		this.c = YamlConfiguration.loadConfiguration(this.file);
		
		double v = this.getDouble(plugin.getName() + ".global.version");
		if(v < this.version) {
			confUpdate cU = (confUpdate) plugin.getUpdater().getUpdater(UpdateType.CONFIG);
			File oconfigFile = new File(plugin.getDir(), "conf.yml." + v + ".bak");
			try {
				Files.copy(file, oconfigFile);
				cU.Update(v, c);
				
				this.c = YamlConfiguration.loadConfiguration(new File(plugin.getDir(), "conf.yml"));
			}catch(IOException e) {
				plugin.getLogger().severe("Failed to update config file!");
				if(c.getBoolean("GiantShop.global.debug", true) == true) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void reload() {
		this.c = null;
		this.c = YamlConfiguration.loadConfiguration(this.file);
		
		double v = this.getDouble(plugin.getName() + ".global.version");
		if(v < this.version) {
			confUpdate cU = (confUpdate) plugin.getUpdater().getUpdater(UpdateType.CONFIG);
			File oconfigFile = new File(plugin.getDir(), "conf.yml." + v + ".bak");
			try {
				Files.copy(file, oconfigFile);
				cU.Update(v, c);
				
				this.c = YamlConfiguration.loadConfiguration(new File(plugin.getDir(), "conf.yml"));
			}catch(IOException e) {
				plugin.getLogger().severe("Failed to update config file!");
				if(c.getBoolean("GiantShop.global.debug", true) == true) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getString(String setting) {
		return this.c.getString(setting, "");
	}
	
	public List<String> getStringList(String setting) {
		return this.c.getStringList(setting);
	}
	
	public Boolean getBoolean(String setting) {
		return this.c.getBoolean(setting, false);
	}
	
	public Boolean getBoolean(String setting, Boolean b) {
		return null != this.c ? this.c.getBoolean(setting, b) : b;
	}
	
	public Integer getInt(String setting) {
		return this.c.getInt(setting, 0);
	}
	
	public Double getDouble(String setting) {
		return this.c.getDouble(setting, 0);
	}

	public HashMap<String, String> getMap(String setting) {
		HashMap<String, String> m = new HashMap<String, String>();
		Set<String> t = this.c.getConfigurationSection(setting).getKeys(false);
		if(t == null) {
			GiantShop.getPlugin().getLogger().log(Level.SEVERE, "Section " + setting + " was not found in the conf.yml! It might be broken...");
		}
		
		for(String i : t) {
			m.put(i, String.valueOf(this.c.get(setting + "." + i)));
		}
		
		return m;
	}
	
	public static config Obtain(GiantShop p) {
		if(config.instance == null)
			config.instance = new config(p);
		
		return config.instance;
	}
	
	public static config Obtain() {
		if(config.instance == null)
			config.instance = new config(GiantShop.getPlugin());
		
		return config.instance;
	}
	
	public static void Kill() {
		if(config.instance != null)
			config.instance = null;
	}
}
