package nl.giantit.minecraft.giantshop.core.Tools.Discount;

import nl.giantit.minecraft.giantcore.core.Items.ItemID;
import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.config;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.query.DeleteQuery;
import nl.giantit.minecraft.giantcore.database.query.Group;
import nl.giantit.minecraft.giantcore.database.query.InsertQuery;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;
import nl.giantit.minecraft.giantcore.database.query.UpdateQuery;
import nl.giantit.minecraft.giantcore.perms.Permission;

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
		Driver db = plugin.getDB().getEngine();
		HashMap<String, String> order = new HashMap<String, String>();
		order.put("itemID", "ASC");
		order.put("type", "ASC");
		order.put("id", "ASC");
		
		SelectQuery sQ = db.select("*");
		sQ.from("#__discounts");
		sQ.orderBy("ItemID", SelectQuery.Order.ASC);
		sQ.orderBy("type", SelectQuery.Order.ASC);
		sQ.orderBy("id", SelectQuery.Order.ASC);
		QueryResult QRes = sQ.exec();
		QueryResult.QueryRow QR;
		while(null != (QR = QRes.getRow())) {
			int id = QR.getInt("id");
			int itemId = QR.getInt("itemid");
			int type = QR.getInt("type");
			type = (type <= 0) ? 0 : type;
			
			int discount = QR.getInt("discount");
			
			if(QR.getString("grp") == null || QR.getString("grp").equalsIgnoreCase("") || QR.getString("grp").equalsIgnoreCase("null")) {
				discounts.add(new Discount(plugin, id, itemId, type, discount, QR.getString("user"), false));
			}else{
				discounts.add(new Discount(plugin, id, itemId, type, discount, QR.getString("grp"), true));
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
			Driver db = plugin.getDB().getEngine();
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("itemID");
			fields.add("type");
			fields.add("discount");
			
			if(!group) {
				fields.add("user");
			}else{
				fields.add("grp");
			}
			
			InsertQuery iQ = db.insert("#__discounts");
			iQ.addFields(fields);
			iQ.addRow();
			iQ.assignValue("itemID", String.valueOf(iID.getId()), InsertQuery.ValueType.RAW);
			iQ.assignValue("type", String.valueOf(((iID.getType() == null) ? -1 : iID.getType())), InsertQuery.ValueType.RAW);
			iQ.assignValue("discount", String.valueOf(disc), InsertQuery.ValueType.RAW);
			if(!group) {
				iQ.assignValue("user", data);
			}else{
				iQ.assignValue("grp", data);
			}
			iQ.exec();
			
			SelectQuery sQ = db.select("id");
			sQ.from("#__discounts");
			sQ.where("itemID", String.valueOf(iID.getId()), Group.ValueType.EQUALSRAW);
			sQ.where("type", String.valueOf(((iID.getType() == null) ? -1 : iID.getType())), Group.ValueType.EQUALSRAW);
			if(group) {
				sQ.where("grp", data);
			}else{
				sQ.where("user", data);
			}
			
			QueryResult QRes = sQ.exec();
			QueryRow QR = QRes.getRow();
			int id = QR.getInt("id");
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
			Driver db = plugin.getDB().getEngine();
			UpdateQuery uQ = db.update("#__discounts");
			uQ.set("discount", String.valueOf(newDiscount), UpdateQuery.ValueType.SETRAW);
			uQ.where("id", String.valueOf(discountID), Group.ValueType.EQUALSRAW);
			uQ.exec();
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
			Driver db = plugin.getDB().getEngine();
			this.discounts.remove(d);
			DeleteQuery dQ = db.delete("#__discounts");
			dQ.where("id", String.valueOf(discountID), Group.ValueType.EQUALSRAW);
			dQ.exec();
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
