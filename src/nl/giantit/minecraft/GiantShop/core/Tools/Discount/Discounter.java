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
		HashMap<String, String> order = new HashMap<String, String>();
		order.put("itemID", "ASC");
		order.put("type", "ASC");
		order.put("id", "ASC");
		
		ArrayList<HashMap<String, String>> resSet = db.select("*").from("#__discounts").orderBy(order).execQuery();
		for(HashMap<String, String> res : resSet) {
			int id = Integer.parseInt(res.get("id"));
			int itemId = Integer.parseInt(res.get("itemid"));
			int type = Integer.parseInt(res.get("type"));
			type = (type <= 0) ? 0 : type;
			
			int discount = Integer.parseInt(res.get("discount"));
			
			if(res.get("grp") == null || res.get("grp").equalsIgnoreCase("") || res.get("grp").equalsIgnoreCase("null")) {
				discounts.add(new Discount(plugin, id, itemId, type, discount, res.get("user"), false));
			}else{
				discounts.add(new Discount(plugin, id, itemId, type, discount, res.get("grp"), true));
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
	
	public int addDiscount(ItemID iID, int disc, String data) {
		return this.addDiscount(iID, disc, data, true);
	}
	
	public int addDiscount(ItemID iID, int disc, String data, Boolean group) {
		Boolean exists = false;
		for(Discount discount : this.discounts) {
			if(!discount.forItem(iID.getId(), (iID.getType() == null) ? 0 : iID.getType()))
				continue;
			
			if(group && (!discount.hasGroup() || !discount.getGroup().equalsIgnoreCase(data)))
				continue;
			
			if(!group && (discount.hasGroup() || !discount.getOwner().equalsIgnoreCase(data)))
				continue;
			
			exists = true;
			break;
		}
		
		if(exists) {
			return 1;
		}else{
			iDriver db = plugin.getDB().getEngine();
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("itemID");
			fields.add("type");
			fields.add("discount");
			
			if(!group) {
				fields.add("user");
			}else{
				fields.add("grp");
			}
			
			HashMap<Integer, HashMap<String, String>> values = new HashMap<Integer, HashMap<String, String>>();
			for(int i = 0; i < fields.size(); i++) {
				String field = fields.get(i);
				HashMap<String, String> value = new HashMap<String, String>();
				
				if(field.equalsIgnoreCase("itemID")) {
					value.put("kind", "INT");
					value.put("data", "" + iID.getId());
				}else if(field.equalsIgnoreCase("type")) {
					value.put("kind", "INT");
					value.put("data", "" + ((iID.getType() == null) ? -1 : iID.getType()));
				}else if(field.equalsIgnoreCase("discount")) {
					value.put("kind", "INT");
					value.put("data", "" + disc);
				}else if(field.equalsIgnoreCase("user")) {
					value.put("data", "" + data);
				}else if(field.equalsIgnoreCase("grp")) {
					value.put("data", "" + data);
				}
				
				values.put(i, value);
			}
			
			db.insert("#__discounts", fields, values).updateQuery();
			HashMap<String, String> where = new HashMap<String, String>();
			if(group) {
				where.put("grp", data);
			}else{
				where.put("user", data);
			}
			where.put("itemID", "" + iID.getId());
			where.put("type", "" + ((iID.getType() == null) ? -1 : iID.getType()));
			
			ArrayList<HashMap<String, String>> resSet = db.select("id").from("#__discounts").where(where).execQuery();
			int id = Integer.parseInt(resSet.get(0).get("id"));
			if(group) {
				this.discounts.add(new Discount(plugin, id, iID.getId(), ((iID.getType() == null) ? 0 : iID.getType()), disc, data, true));
				return 0;
			}else{
				this.discounts.add(new Discount(plugin, id, iID.getId(), ((iID.getType() == null) ? 0 : iID.getType()), disc, data, false));
				return 0;
			}
		}
	}
	
	public int updateDiscount(int discountID, int newDiscount) {
		Discount d = null;
		for(Discount discount : this.discounts) {
			if(discount.getDiscountID() != discountID)
				continue;
			
			d = discount;
			break;
		}
		
		if(d == null) {
			return 1;
		}else{
			iDriver db = plugin.getDB().getEngine();
			
			HashMap<String, HashMap<String, String>> where = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("kind", "INT");
			data.put("data", "" + discountID);
			where.put("id", data);
			
			HashMap<String, HashMap<String, String>> set = new HashMap<String, HashMap<String, String>>();
			data = new HashMap<String, String>();
			data.put("kind", "INT");
			data.put("data", "" + newDiscount);
			set.put("discount", data);
			
			db.update("#__discounts").set(set, true).where(where, true).updateQuery();
			d.setDiscount(newDiscount);
			return 0;
		}
	}
	
	public int removeDiscount(int discountID) {
		Discount d = null;
		for(Discount discount : this.discounts) {
			if(discount.getDiscountID() != discountID)
				continue;
			
			d = discount;
			break;
		}
		
		if(d == null) {
			return 1;
		}else{
			iDriver db = plugin.getDB().getEngine();
			
			HashMap<String, HashMap<String, String>> where = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("kind", "INT");
			data.put("data", "" + discountID);
			where.put("id", data);
			
			this.discounts.remove(d);
			db.delete("#__discounts").where(where, true).updateQuery();
			return 0;
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
	
	public Set<Discount> getAllDiscounts(String d) {
		return this.getAllDiscounts(d, true);
	}
	
	public Set<Discount> getAllDiscounts(String d, Boolean group) {
		Set<Discount> disc = new HashSet<Discount>();
		
		for(Discount discount : this.discounts) {
			if(group && (!discount.hasGroup() || !discount.getGroup().equalsIgnoreCase(d)))
				continue;

			if(!group && (discount.hasGroup() || !discount.getOwner().equalsIgnoreCase(d)))
				continue;
			
			disc.add(discount);
		}
		
		return disc;
	}
	
	public Discount getDiscount(ItemID iID, String d) {
		return this.getDiscount(iID, d, true);
	}
	
	public Discount getDiscount(ItemID iID, String d, Boolean group) {
		Discount disc = null;
		
		for(Discount discount : this.discounts) {
			if(group && (!discount.hasGroup() || !discount.getGroup().equalsIgnoreCase(d)))
				continue;

			if(!group && (discount.hasGroup() || !discount.getOwner().equalsIgnoreCase(d)))
				continue;

			if(discount.forItem(iID.getId(), (iID.getType() == null ? 0 : iID.getType()))) {
				disc = discount;
				break;
			}
		}
		
		return disc;
	}
	
}
