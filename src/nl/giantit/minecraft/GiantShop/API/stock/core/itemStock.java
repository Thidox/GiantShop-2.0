package nl.giantit.minecraft.GiantShop.API.stock.core;

import nl.giantit.minecraft.GiantShop.API.stock.Events.*;
import nl.giantit.minecraft.GiantShop.API.stock.stockResponse;
import nl.giantit.minecraft.GiantShop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;
import nl.giantit.minecraft.GiantShop.core.Logger.*;

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
		iDriver DB = Database.Obtain().getEngine();
		
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("stock");
		fields.add("maxStock");
		fields.add("perStack");
		fields.add("sellFor");
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("itemID", String.valueOf(id));
		where.put("type", (type == null || type.intValue() == 0 || type.intValue() == -1) ? "-1" : String.valueOf(type.intValue()));
		
		ArrayList<HashMap<String, String>> resSet = DB.select(fields).from("#__items").where(where).execQuery();
		if(resSet.size() >= 1) {
			HashMap<String, String> res = resSet.get(0);
			stock = Integer.parseInt(res.get("stock"));
			maxStock = Integer.parseInt(res.get("maxstock"));
			perStack = Integer.parseInt(res.get("perstack"));
			sellFor = Double.parseDouble(res.get("sellfor"));
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
		double cost = this.sellFor * amount;
		int quantity = perStack * amount;
		if(conf.getBoolean("GiantShop.stock.useStock") && conf.getBoolean("GiantShop.stock.stockDefinesCost") && maxStock != -1 && stock != -1) {
			double maxInfl = conf.getDouble("GiantShop.stock.maxInflation");
			double maxDefl = conf.getDouble("GiantShop.stock.maxDeflation");
			int atmi = conf.getInt("GiantShop.stock.amountTillMaxInflation");
			int atmd = conf.getInt("GiantShop.stock.amountTillMaxDeflation");
			double split = Math.round((atmi + atmd) / 2);
			if(maxStock <= atmi + atmd) {
				split = maxStock / 2;
				atmi = 0;
				atmd = maxStock;
			}

			if(stock >= atmd) {
				cost = (sellFor * (1.0 - maxDefl / 100.0)) * (double) quantity; 
			}else if(stock <= atmi) {
				cost = (sellFor * (1.0 + maxInfl / 100.0)) * (double) quantity; 
			}else{
				if(stock < split) {
					cost = (double)Math.round(((sellFor * (1.0 + (maxInfl / stock) / 100)) * (double) quantity) * 100.0) / 100.0;
				}else if(stock > split) {
					cost = 2.0 + (double)Math.round(((sellFor / (maxDefl * stock / 100)) * (double) quantity) * 100.0) / 100.0;
				}
			}
		}
		return cost;
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
		
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("stock", String.valueOf(value));
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("itemID", String.valueOf(id));
		where.put("type", (type == null || type.intValue() == 0 || type.intValue() == -1) ? "-1" : String.valueOf(type.intValue()));
		
		iDriver DB = Database.Obtain().getEngine();
		DB.update("#__items").set(fields).where(where).updateQuery();
		
		Logger.Log(LoggerType.APISTOCKUPDATE, 
					"{id: " + String.valueOf(this.id) + "; " +
					"type:" + String.valueOf((this.type == null || this.type <= 0) ? -1 : this.type) + "; " +
					"oS:" + String.valueOf(oS) + "; " +
					"nS:" + String.valueOf(this.stock) + ";}");
		
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
		
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("maxStock", String.valueOf(value));
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("itemID", String.valueOf(id));
		where.put("type", (type == null || type.intValue() == 0 || type.intValue() == -1) ? "-1" : String.valueOf(type.intValue()));
		
		iDriver DB = Database.Obtain().getEngine();
		DB.update("#__items").set(fields).where(where).updateQuery();
		
		Logger.Log(LoggerType.APIMAXSTOCKUPDATE, 
				"{id: " + String.valueOf(this.id) + "; " +
				"type:" + String.valueOf((this.type == null || this.type <= 0) ? -1 : this.type) + "; " +
				"oS:" + String.valueOf(oS) + "; " +
				"nS:" + String.valueOf(this.maxStock) + ";}");
		
		return stockResponse.MAXSTOCKUPDATED;
	}
}
