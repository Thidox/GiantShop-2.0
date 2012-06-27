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
	
	private void loadDiscounts() {
		iDriver db = plugin.getDB().getEngine();
		ArrayList<HashMap<String, String>> resSet = db.select("itemID", "type", "discount", "user", "grp").from("#__discounts").execQuery();
		for(HashMap<String, String> res : resSet) {
			int id = Integer.parseInt(res.get("itemID"));
			int type = Integer.parseInt(res.get("type"));
			type = (type <= 0) ? 0 : type;
			
			int discount = Integer.parseInt(res.get("discount"));
			
			if(res.get("grp") == null || res.get("grp").equalsIgnoreCase("")) {
				discounts.add(new Discount(plugin, id, type, discount, res.get("user"), false));
			}else{
				discounts.add(new Discount(plugin, id, type, discount, res.get("grp"), true));
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
			if(discount.hasGroup() && !this.perm.inGroup(p, discount.getGroup()))
				continue;
			
			if(!discount.hasGroup() && !discount.getOwner().equalsIgnoreCase(p.getName()))
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
			if(discount.hasGroup() && !this.perm.inGroup(p, discount.getGroup()))
				continue;
			
			if(!discount.hasGroup() && !discount.getOwner().equalsIgnoreCase(p.getName()))
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
	
	public Set<Discount> getAllDiscounts() {
		return this.discounts;
	}
	
	public Set<Discount> getAllDiscounts(Player p) {
		Set<Discount> disc = new HashSet<Discount>();
		
		for(Discount discount : this.discounts) {
			if(discount.hasGroup() && !this.perm.inGroup(p, discount.getGroup())) 
				continue;
			
			if(!discount.hasGroup() && !discount.getOwner().equalsIgnoreCase(p.getName()))
				continue;
			
			disc.add(discount);
		}
		
		return disc;
	}
	
	
}
