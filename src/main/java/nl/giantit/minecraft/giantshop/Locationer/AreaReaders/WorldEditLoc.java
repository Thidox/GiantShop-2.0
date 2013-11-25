package nl.giantit.minecraft.giantshop.Locationer.AreaReaders;

import nl.giantit.minecraft.giantshop.GiantShop;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

/**
 *
 * @author Giant
 */
public class WorldEditLoc implements Indaface {
	
	private GiantShop plugin;
	
	private int id;
	private ArrayList<Location> loc;
	private String name;
	private World world;
	
	public WorldEditLoc(GiantShop plugin) {
		this.plugin = plugin;
	}
	
	public WorldEditLoc(GiantShop plugin, ArrayList<Location> loc) {
		this.plugin = plugin;
		
		this.loc = loc;
		this.world = loc.get(0).getWorld();
	}
	
	public WorldEditLoc(GiantShop plugin, int id, ArrayList<Location> loc) {
		this.plugin = plugin;
		
		this.id = id;
		this.loc = loc;
		this.world = loc.get(0).getWorld();
	}
	
	public WorldEditLoc(GiantShop plugin, int id, ArrayList<Location> loc, String name) {
		this.plugin = plugin;
		
		this.id = id;
		this.loc = loc;
		this.name = name;
		this.world = loc.get(0).getWorld();
	}
	
	@Override
	public ArrayList<Indaface> getShops() {
		ArrayList<Indaface> shops = new ArrayList<Indaface>();
		
		return shops;
	}
	
	@Override
	public Indaface getShop(int id) {
		Indaface shop = new PlainJane(plugin);
		
		return shop;
	}
	
	@Override
	public Indaface getShop(String name, String world) {
		Indaface shop = new PlainJane(plugin);
		
		return shop;
	}
	
	@Override
	public Indaface newShop(ArrayList<Location> loc, String name) {
		Indaface shop = new PlainJane(plugin);
		
		return shop;
	}
	
	@Override
	public void remove(int id) {
		
	}
	
	@Override
	public void remove(String name, String world) {
		
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getID() {
		return this.id;
	}
	
	@Override
	public String getWorldName() {
		return this.world.getName();
	}
	
	@Override
	public ArrayList<Location> getLocation() {
		return this.loc;
	}
	
	@Override
	public boolean inShop(Location pLoc) {
		if(this.loc.size() < 2)
			return false;
		
		if(!pLoc.getWorld().equals(this.world))
			return false;
		
		
		Location min = this.loc.get(0);
		Location max = this.loc.get(1);
		
		return (pLoc.getBlockX() >= min.getBlockX() && 
				pLoc.getBlockY() >= min.getBlockY() && 
				pLoc.getBlockZ() >= min.getBlockZ() && 
				pLoc.getBlockX() <= max.getBlockX() && 
				pLoc.getBlockY() <= max.getBlockY() && 
				pLoc.getBlockZ() <= max.getBlockZ());
	}
}
