package nl.giantit.minecraft.GiantShop.API.stock;

import nl.giantit.minecraft.GiantShop.API.stock.core.itemStock;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.Items.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class stockAPI {
	
	private GiantShop plugin;
	private Items iH;
	
	public stockAPI(GiantShop plugin) {
		this.plugin = plugin;
		this.iH = plugin.getItemHandler();
	}
	
	public itemStock getItemStock(String name) throws ItemNotFoundException {
		ItemID IID = iH.getItemIDByName(name);
		if(IID != null) {
			int id = IID.getId();
			Integer type = IID.getType();
			return new itemStock(id, type);
		}
		
		return null;
	}
	
	public itemStock getItemStock(int id, Integer type) throws ItemNotFoundException {
		if(iH.isValidItem(id, type)) {
			return new itemStock(id, type);
		}
		
		return null;
	}
	
	public HashMap<String, itemStock> getItemStocks() throws ItemNotFoundException {
		HashMap<String, itemStock> stocks = new HashMap<String, itemStock>();
		
		db DB = db.Obtain();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("itemID");
		fields.add("type");
		
		ArrayList<HashMap<String, String>> resSet = DB.select(fields).from("#__items").execQuery();
		if(resSet.size() >= 1) {
			for(HashMap<String, String> res : resSet) {
				String name = iH.getItemNameByID(Integer.parseInt(res.get("itemID")), Integer.parseInt(res.get("type")));
				stocks.put(name, new itemStock(Integer.parseInt(res.get("itemID")), Integer.parseInt(res.get("type"))));
			}
		}
		
		return stocks;
	}
}
