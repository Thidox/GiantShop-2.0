package nl.giantit.minecraft.GiantShop.core;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.List;
import java.io.File;

/**
 *
 * @author Giant
 */
public class config {
	
	private static config instance = null;
	private HashMap<String, Object> conf = new HashMap<String, Object>();
	
	private YamlConfiguration configuration;
	private File file;
	
	private config() {
	}
	
	public void loadConfig(File file) {
		this.file = file;
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
	}
	
	public String getString(String setting) {
		return this.configuration.getString(setting, "");
	}
	
	public List<String> getStringList(String setting) {
		String tmp = this.configuration.getString(setting, "");
		String[] splitted = tmp.split(", ");
		
		return java.util.Arrays.asList(splitted);
	}
	
	public Boolean getBoolean(String setting) {
		return this.configuration.getBoolean(setting, false);
	}
	
	public Integer getInt(String setting) {
		return this.configuration.getInt(setting, 0);
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
