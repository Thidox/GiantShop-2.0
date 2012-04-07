package nl.giantit.minecraft.GiantShop.Locationer;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.db;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class shopLoader {
	
	private GiantShop plugin;
	private Locationer lH;
	
	private void loadShops() {
		db DB = db.Obtain();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("name");
		fields.add("world");
		fields.add("locMinX");
		fields.add("locMinY");
		fields.add("locMinZ");
		fields.add("locMaxX");
		fields.add("locMaxY");
		fields.add("locMaxZ");
		
		ArrayList<HashMap<String, String>> resSet = DB.select(fields).from("#__shops").execQuery();
		if(resSet.size() > 0) {
			for(HashMap<String, String> res : resSet) {
				try {
					String name = res.get("name");
					World world = plugin.getSrvr().getWorld(res.get("world"));
					double minX = Double.valueOf(res.get("locMinX"));
					double minY = Double.valueOf(res.get("locMinY"));
					double minZ = Double.valueOf(res.get("locMinZ"));
					double maxX = Double.valueOf(res.get("locMaxX"));
					double maxY = Double.valueOf(res.get("locMaxY"));
					double maxZ = Double.valueOf(res.get("locMaxZ"));

					Location min = new Location(world, minX, minY, minZ);
					Location max = new Location(world, maxX, maxY, maxZ);
					ArrayList<Location> locs = new ArrayList<Location>();
					locs.add(min);
					locs.add(max);

					lH.addShop(locs, name);
				}catch(NumberFormatException e) {
					plugin.log.log(Level.WARNING, "[GiantShopLocation] Invalid shop passed during load!");
				}
			}
		}
	}
	
	public shopLoader(GiantShop plugin, Locationer lH) {
		this.plugin = plugin;
		this.lH = lH;
		
		this.loadShops();
	}
}
