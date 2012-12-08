package nl.giantit.minecraft.GiantShop.core.Updater.Config;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Updater.iUpdater;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class confUpdate implements iUpdater {
	
	private void export(File file, FileConfiguration c) {
		try {
			InputStream iS = new ByteArrayInputStream(c.saveToString().replace("\n", "\r\n").replace("  ", "    ").getBytes("UTF-8"));
			GiantShop.getPlugin().extract(file, iS);
		}catch(UnsupportedEncodingException e) {
			GiantShop.getPlugin().getLogger().severe("Failed to update config file!");
			if(c.getBoolean("GiantShop.global.debug", true) == true) {
				GiantShop.getPlugin().getLogger().log(Level.INFO, e.getMessage(), e);
			}
		}
	}

	private void update1_1(FileConfiguration c) {
		Map<String, Boolean> section = new HashMap<String, Boolean>();
		section.put("useGSWAPI", true);
		section.put("useGSLAPI", true);
		section.put("useStockAPI", true);
		c.createSection("GiantShop.API", section);
		
		c.set("GiantShop.global.version", 1.1);
		this.export(new File(GiantShop.getPlugin().getDir(), "conf.yml"), c);
	}
	
	public void Update(double curV, FileConfiguration c) {
		if(curV < 1.1) {
			GiantShop.getPlugin().getLogger().log(Level.INFO, "Your conf.yml has ran out of date. Updating to 1.1 now!");
			update1_1(c);
		}
	}
	
}
