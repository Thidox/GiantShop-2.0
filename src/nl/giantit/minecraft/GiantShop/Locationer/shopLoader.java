package nl.giantit.minecraft.GiantShop.Locationer;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.giantcore.Database.QueryResult;
import nl.giantit.minecraft.giantcore.Database.iDriver;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

/**
 *
 * @author Giant
 */
public class shopLoader {
	
	private GiantShop plugin;
	private Locationer lH;
	
	private void loadShops() {
		iDriver DB = plugin.getDB().getEngine();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("name");
		fields.add("world");
		fields.add("locMinX");
		fields.add("locMinY");
		fields.add("locMinZ");
		fields.add("locMaxX");
		fields.add("locMaxY");
		fields.add("locMaxZ");
		
		QueryResult QRes = DB.select(fields).from("#__shops").execQuery();
		QueryResult.QueryRow QR;
		while(null != (QR = QRes.getRow())) {
			String name = QR.getString("name");
			World world = plugin.getSrvr().getWorld(QR.getString("world"));
			double minX = QR.getDouble("locminx");
			double minY = QR.getDouble("locminy");
			double minZ = QR.getDouble("locminz");
			double maxX = QR.getDouble("locmaxx");
			double maxY = QR.getDouble("locmaxy");
			double maxZ = QR.getDouble("locmaxz");

			Location min = new Location(world, minX, minY, minZ);
			Location max = new Location(world, maxX, maxY, maxZ);
			ArrayList<Location> locs = new ArrayList<Location>();
			locs.add(min);
			locs.add(max);

			lH.addShop(locs, name);
		}
	}
	
	public shopLoader(GiantShop plugin, Locationer lH) {
		this.plugin = plugin;
		this.lH = lH;
		
		this.loadShops();
	}
}
