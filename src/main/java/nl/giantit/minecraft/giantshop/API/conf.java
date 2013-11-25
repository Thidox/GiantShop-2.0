package nl.giantit.minecraft.giantshop.API;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import nl.giantit.minecraft.giantshop.GiantShop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Giant
 */
public class conf {
	
	private GiantShop p;
	private YamlConfiguration c;
	private File f;
	
	public conf(GiantShop p, File f) {
		this.p = p;
		if(f.exists()) {
			this.f = f;
			this.c = YamlConfiguration.loadConfiguration(f);
		}
	}
	
	public boolean isLoaded() {
		return null != c;
	}
	
	public void reload() {
		if(this.f.exists()) {
			this.c = YamlConfiguration.loadConfiguration(this.f);
		}
	}
	
	public void save() {
		try {
			InputStream iS = new ByteArrayInputStream(this.c.saveToString().replace("\n", "\r\n").replace("  ", "    ").getBytes("UTF-8"));
			GiantShop.getPlugin().extract(this.f, iS);
		}catch(UnsupportedEncodingException e) {
			GiantShop.getPlugin().getLogger().severe("Failed to update config file!");
			if(c.getBoolean("GiantShop.global.debug", true) == true) {
				GiantShop.getPlugin().getLogger().log(Level.INFO, "" + e, e);
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
	
	public ConfigurationSection getConfigurationSection(String section) {
		return this.c.getConfigurationSection(section);
	}
	
	public void set(String setting, Object val) {
		this.c.set(setting, val);
	}
}
