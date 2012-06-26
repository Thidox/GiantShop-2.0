package nl.giantit.minecraft.GiantShop.core.Tools.Discount;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.perms.Permission;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Discounter {

	private GiantShop plugin;
	private config conf;
	private Permission perm;
	private Set<Discount> discounts = new HashSet<Discount>();
	
	private class Discount {
		
		private String owner;
		private String group;
		private Boolean hasGroup = true;
		private int id;
		private int type;
		private int disc;
		
		public Discount(int id, int type, int disc, String data) {
			this(id, type, disc, data, true);
		}
		
		public Discount(int id, int type, int disc, String data, Boolean group) {
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
	
	private void loadDiscounts() {
		iDriver db = plugin.getDB().getEngine();
		ArrayList<HashMap<String, String>> resSet = db.select("itemID", "type", "discount", "user", "grp").from("#__discounts").execQuery();
		for(HashMap<String, String> res : resSet) {
			int id = Integer.parseInt(res.get("itemID"));
			int type = Integer.parseInt(res.get("type"));
			type = (type <= 0) ? 0 : type;
			
			int discount = Integer.parseInt(res.get("discount"));
			
			if(res.get("grp") == null || res.get("grp").equalsIgnoreCase("")) {
				discounts.add(new Discount(id, type, discount, res.get("user"), false));
			}else{
				discounts.add(new Discount(id, type, discount, res.get("grp"), true));
			}
		}
	}
	
	public Discounter(GiantShop plugin) {
		this.plugin = plugin;
		this.conf = config.Obtain();
		perm = plugin.getPermHandler().getEngine();
		
		if(this.conf.getBoolean(plugin.getName() + ".discounts.useDiscounts")) {
			this.loadDiscounts();
		}
	}
	
	public Boolean hasDiscount(ItemID iID, Player p) {
		Discount t = null;
		for(Discount discount : this.discounts) {
			if(discount.hasGroup && !this.perm.inGroup(p, discount.getGroup()))
				continue;
			
			if(!discount.hasGroup && !discount.getOwner().equalsIgnoreCase(p.getName()))
				continue;
			
			if(discount.forItem(iID.getId(), (iID.getType() == null ? 0 : iID.getType()))) {
				if(t == null || !(t instanceof Discount)) {
					t = discount;
				}else{
					if(t.getDiscount() < discount.getDiscount()) {
						t = discount;
					}
				}
			}
		}
		
		return t != null;
	}
	
	public int getDiscount(ItemID iID, Player p) {
		Discount t = null;
		for(Discount discount : this.discounts) {
			if(discount.hasGroup && !this.perm.inGroup(p, discount.getGroup()))
				continue;
			
			if(!discount.hasGroup && !discount.getOwner().equalsIgnoreCase(p.getName()))
				continue;
			
			if(discount.forItem(iID.getId(), (iID.getType() == null ? 0 : iID.getType()))) {
				if(t == null || !(t instanceof Discount)) {
					t = discount;
				}else{
					if(t.getDiscount() < discount.getDiscount()) {
						t = discount;
					}
				}
			}
		}
		
		return (t != null) ? t.getDiscount() : 0;
	}
}
