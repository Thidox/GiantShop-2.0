package nl.giantit.minecraft.giantshop.Locationer.AreaReaders;

import org.bukkit.Location;

import java.util.ArrayList;

/**
 *
 * @author Giant
 */
public interface Indaface {
	
	public ArrayList<Indaface> getShops();
	public Indaface getShop(int id);
	public Indaface getShop(String name, String World);
	
	public Indaface newShop(ArrayList<Location> loc, String name);
	
	public void remove(int id);
	public void remove(String name, String World);
	
	public String getName();
	public int getID();
	public String getWorldName();
	public ArrayList<Location> getLocation();
	public boolean inShop(Location pLoc);
}
