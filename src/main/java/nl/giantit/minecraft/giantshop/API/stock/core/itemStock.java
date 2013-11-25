package nl.giantit.minecraft.giantshop.API.stock.core;

import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.query.Group;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;
import nl.giantit.minecraft.giantcore.database.query.UpdateQuery;

import nl.giantit.minecraft.giantshop.API.stock.Events.MaxStockUpdateEvent;
import nl.giantit.minecraft.giantshop.API.stock.Events.StockUpdateEvent;
import nl.giantit.minecraft.giantshop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.giantshop.API.stock.stockResponse;
import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.Logger.Logger;
import nl.giantit.minecraft.giantshop.core.Logger.LoggerType;
import nl.giantit.minecraft.giantshop.core.config;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class itemStock {
	
	private final config conf = config.Obtain();
	private int id;
	private Integer type;
	private int stock;
	private int maxStock;
	private int perStack;
	private double sellFor;
	
	private void loadStock() throws ItemNotFoundException {
		Driver DB = GiantShop.getPlugin().getDB().getEngine();
		
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("stock");
		fields.add("maxStock");
		fields.add("perStack");
		fields.add("sellFor");
		
		SelectQuery sQ = DB.select(fields);
		sQ.from("#__items");
		sQ.where("itemID", String.valueOf(id), Group.ValueType.EQUALSRAW);
		sQ.where("type", (type == null || type.intValue() == 0 || type.intValue() == -1) ? "-1" : String.valueOf(type.intValue()), Group.ValueType.EQUALSRAW);
		
		QueryResult QRes = sQ.exec();
		if(QRes.size() >= 1) {
			QueryRow QR = QRes.getRow();
			stock = QR.getInt("stock");
			maxStock = QR.getInt("maxstock");
			perStack = QR.getInt("perstack");
			sellFor = QR.getDouble("sellfor");
		}else{
			throw new ItemNotFoundException();
		}
	}
	
	public itemStock(int id, Integer type) throws ItemNotFoundException {
		this.id = id;
		this.type = type;
		this.loadStock();
	}
	
	public final int getStock() {
		return this.stock;
	}
	
	public final int getMaxStock() {
		return this.maxStock;
	}
	
	public final int getPerStack() {
		return this.perStack;
	}
	
	public final int getID() {
		return this.id;
	}
	
	public final Integer getType() {
		return this.type;
	}
	
	public final double getCost(int amount) {
		return Misc.getPrice(this.sellFor, this.stock, this.maxStock, amount);
	}
	
	public final stockResponse setStock(int value) {
		if(value < 0)
			return stockResponse.INVALIDSTOCKPASSED;
		
		if(this.stock == -1)
			return stockResponse.STOCKISUNLIMITED;
		
		
		if(value > this.maxStock && this.maxStock != -1)
			if(!conf.getBoolean("GiantShop.stock.allowOverStock"))
				return stockResponse.STOCKHIGHERTHENMAX;
		
		
		int oS = this.stock;
		this.stock = value;
		StockUpdateEvent.StockUpdateType t = (oS < value) ?	StockUpdateEvent.StockUpdateType.INCREASE : StockUpdateEvent.StockUpdateType.DECREASE;
		StockUpdateEvent event = new StockUpdateEvent(null, this, t);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		Driver DB = GiantShop.getPlugin().getDB().getEngine();
		UpdateQuery uQ = DB.update("#__items");
		uQ.set("stock", String.valueOf(value), UpdateQuery.ValueType.SETRAW);
		
		uQ.where("itemID", String.valueOf(id), Group.ValueType.EQUALSRAW);
		uQ.where("type", (type == null || type.intValue() == 0 || type.intValue() == -1) ? "-1" : String.valueOf(type.intValue()), Group.ValueType.EQUALSRAW);
		
		uQ.exec();
		
		HashMap<String, String> d = new HashMap<String, String>();
		d.put("id", String.valueOf(this.id));
		d.put("type", String.valueOf((this.type == null || this.type <= 0) ? -1 : this.type));
		d.put("oS", String.valueOf(oS));
		d.put("nS", String.valueOf(this.stock));
		Logger.Log(LoggerType.APISTOCKUPDATE, "Unknown (API)", d);
		
		return stockResponse.STOCKUPDATED;
	}
	
	public final stockResponse setMaxStock(int value) {
		if(value < 0)
			return stockResponse.INVALIDSTOCKPASSED;
		
		if(value < this.stock)
			if(!conf.getBoolean("GiantShop.stock.allowOverStock"))
				return stockResponse.MAXLOWERTHENCUR;
		
		int oS = this.maxStock;
		this.maxStock = value;
		MaxStockUpdateEvent.StockUpdateType t = (oS < value) ?	MaxStockUpdateEvent.StockUpdateType.INCREASE : MaxStockUpdateEvent.StockUpdateType.DECREASE;
		MaxStockUpdateEvent event = new MaxStockUpdateEvent(null, this, t);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		Driver DB = GiantShop.getPlugin().getDB().getEngine();
		UpdateQuery uQ = DB.update("#__items");
		uQ.set("maxStock", String.valueOf(value), UpdateQuery.ValueType.SETRAW);
		
		uQ.where("itemID", String.valueOf(id), Group.ValueType.EQUALSRAW);
		uQ.where("type", (type == null || type.intValue() == 0 || type.intValue() == -1) ? "-1" : String.valueOf(type.intValue()), Group.ValueType.EQUALSRAW);
		
		uQ.exec();
		
		HashMap<String, String> d = new HashMap<String, String>();
		d.put("id", String.valueOf(this.id));
		d.put("type", String.valueOf((this.type == null || this.type <= 0) ? -1 : this.type));
		d.put("oS", String.valueOf(oS));
		d.put("nS", String.valueOf(this.maxStock));
		Logger.Log(LoggerType.APIMAXSTOCKUPDATE, "Unknown (API)", d);
		
		return stockResponse.MAXSTOCKUPDATED;
	}
}
