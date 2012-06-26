package nl.giantit.minecraft.GiantShop.core.Tools.Discount;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;

public class Discount {

	private GiantShop plugin;
	private config conf = config.Obtain();
	private String owner;
	private String group;
	private Boolean hasGroup = true;
	private int id;
	private int type;
	private int disc;
	
	public Discount(GiantShop plugin, int id, int type, int disc, String data) {
		this(plugin, id, type, disc, data, true);
	}
	
	public Discount(GiantShop plugin, int id, int type, int disc, String data, Boolean group) {
		this.plugin = plugin;
		
		this.id = id;
		this.type = type;
		this.disc = disc;
		if(!group) {
			this.hasGroup = false;
			this.owner = data;
		}else{
			this.hasGroup = true;
			this.group = data;
			
			if(plugin.getPermHandler().getEngineName().equalsIgnoreCase("No Permissions") || plugin.getPermHandler().getEngineName().equalsIgnoreCase("Bukkit Superperms")) {
				if(conf.getBoolean(plugin.getName() + ".global.debug")) {
					plugin.getLogger().warning("Groups not supported! Discount for item " + plugin.getItemHandler().getItemNameByID(id, type) + " will not work for group " + data + "!");
				}
			}
		}
	}
	
	public Boolean hasGroup() {
		return this.hasGroup;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public int getDiscount() {
		return this.disc;
	}
	
	public Boolean forItem(int id, int type) {
		return this.id == id && this.type == type;
	}
}