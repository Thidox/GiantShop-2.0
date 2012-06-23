package nl.giantit.minecraft.GiantShop.API.stock;

import nl.giantit.minecraft.GiantShop.API.stock.core.itemStock;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;
import nl.giantit.minecraft.GiantShop.core.Items.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	
	public final ArrayList<itemStock> getItemStock(ArrayList<String> names) throws ItemNotFoundException {
		ArrayList<itemStock> temp = new ArrayList<itemStock>();
		for(String name : names) {
			temp.add(this.getItemStock(name));
		}
		
		return temp;
	}
	
	public final itemStock getItemStock(String name) throws ItemNotFoundException {
		ItemID IID = iH.getItemIDByName(name);
		if(IID != null) {
			int id = IID.getId();
			Integer type = IID.getType();
			return new itemStock(id, type);
		}
		
		return null;
	}
	
	public final ArrayList<itemStock> getItemStock(HashMap<Integer, Integer> data) throws ItemNotFoundException {
		ArrayList<itemStock> temp = new ArrayList<itemStock>();
		for(Map.Entry<Integer, Integer> entry : data.entrySet()) {
			temp.add(this.getItemStock(entry.getKey(), entry.getValue()));
		}
		
		return temp;
	}
	
	public final itemStock getItemStock(int id, Integer type) throws ItemNotFoundException {
		if(iH.isValidItem(id, type)) {
			return new itemStock(id, type);
		}
		
		return null;
	}
	
	public final HashMap<String, itemStock> getItemStocks() throws ItemNotFoundException {
		HashMap<String, itemStock> stocks = new HashMap<String, itemStock>();
		
		iDriver DB = Database.Obtain().getEngine();
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
