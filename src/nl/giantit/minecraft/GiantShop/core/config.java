package nl.giantit.minecraft.GiantShop.core;

import nl.giantit.minecraft.GiantShop.GiantShop;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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
	private HashMap<String, Object> conf = new HashMap<String, Object>();
	
	private YamlConfiguration configuration;
	private File file;
	private double yamlVersion = 0.8;
	
	private config() {
	}
	
	public void loadConfig(File file) {
		this.file = file;
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
		
		double v = this.getDouble("GiantShop.global.version");
		if(v < this.yamlVersion) {
			GiantShop.getPlugin().getLogger().log(Level.INFO, "Your conf.yml has ran out of date. Updating now!");
			File oconfigFile = new File(GiantShop.getPlugin().getDir(), "conf.yml." + v + ".bak");
			this.file.renameTo(oconfigFile);
			GiantShop.getPlugin().extract("conf.yml");
			this.configuration = YamlConfiguration.loadConfiguration(this.file);
		}
	}
	
	public String getString(String setting) {
		return this.configuration.getString(setting, "");
	}
	
	public List<String> getStringList(String setting) {
		return this.configuration.getStringList(setting);
	}
	
	public Boolean getBoolean(String setting) {
		return this.configuration.getBoolean(setting, false);
	}
	
	public Integer getInt(String setting) {
		return this.configuration.getInt(setting, 0);
	}
	
	public Double getDouble(String setting) {
		return this.configuration.getDouble(setting, 0);
	}

	public HashMap<String, String> getMap(String setting) {
		HashMap<String, String> m = new HashMap<String, String>();
		Set<String> t = this.configuration.getConfigurationSection(setting).getKeys(false);
		if(t == null) {
			GiantShop.getPlugin().getLogger().log(Level.SEVERE, "Section " + setting + " was not found in the conf.yml! It might be broken...");
		}
		
		for(String i : t) {
			m.put(i, String.valueOf(this.configuration.get(setting + "." + i)));
		}
		
		return m;
	}
	
	public static config Obtain() {
		if(config.instance == null)
			config.instance = new config();
		
		return config.instance;
	}
	
	public static void Kill() {
		if(config.instance != null)
			config.instance = null;
	}
}
