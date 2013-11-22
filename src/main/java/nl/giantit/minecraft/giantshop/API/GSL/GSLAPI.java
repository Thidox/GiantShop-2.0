package nl.giantit.minecraft.GiantShop.API.GSL;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Locationer.AreaReaders.Indaface;
import nl.giantit.minecraft.GiantShop.Locationer.Locationer;

import org.bukkit.Location;

import java.util.ArrayList;
import org.bukkit.entity.Player;

public class GSLAPI {

	private GiantShop plugin;
	private Locationer loc;
	
	public GSLAPI(GiantShop plugin) {
		this.plugin = plugin;
		this.loc = plugin.getLocHandler();
	}
	
	public boolean useLocation() {
		return this.plugin.useLocation();
	}
	
	public boolean inShop(Location loc) {
		if(!this.useLocation())
			return true;
		
		return this.loc.inShop(loc);
	}
	
	public boolean inShop(Location loc, String filter) {
		if(!this.useLocation())
			return true;
		
		return this.loc.inShop(loc, filter);
	}
	
	public int inShop(Location loc, int useless) {
		if(!this.useLocation())
			return 0;
		
		return this.loc.inShop(loc, useless);
	}
	
	public boolean addShop(ArrayList<Location> loc, String name) {
		if(!this.useLocation())
			return false;
		
		return this.loc.addShop(loc, name);
	}
	
	public boolean removeShop(String name, String World) {
		if(!this.useLocation())
			return false;
		
		return this.loc.removeShop(name, World);
	}

	public Indaface getShop(String name, String world) {
		if(!this.useLocation())
			return null;
		
		return this.loc.getShop(name, world);
	}
	
	public ArrayList<Indaface> getShops() {
		if(!this.useLocation())
			return null;
		
		return this.loc.getShops();
	}
	
	public String getShopName(Location loc) {
		if(!this.useLocation())
			return null;
		
		return this.loc.getShopName(loc);
	}
	
	public boolean isProtectedCommand(String cmd) {
		if(!this.useLocation()) {
			return false;
		}
		
		return plugin.cmds.contains(cmd);
	}
	
	public boolean canUse(Player p) {
		if(!this.useLocation()) {
			return false;
		}
		
		return this.loc.canUse(p);
	}
}
