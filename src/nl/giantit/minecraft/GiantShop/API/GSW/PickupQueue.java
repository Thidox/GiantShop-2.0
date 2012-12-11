package nl.giantit.minecraft.GiantShop.API.GSW;

import java.util.ArrayList;
import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;

/**
 *
 * @author Giant
 */
public class PickupQueue {
	
	private GiantShop p;
	private HashMap<String, ArrayList<Queued>> queue = new HashMap<String, ArrayList<Queued>>();
	
	public PickupQueue(GiantShop p) {
		this.p = p;
	}
	
	public void addToQueue(String transactionID, String player, int amount, int id, int type) {
		// Add purchase to database and take money
		iDriver db = p.getDB().getEngine();
		ArrayList<String> fields = new ArrayList<String>() {
			{
				add("transactionID");
				add("player");
				add("amount");
				add("itemID");
				add("itemType");
			}
		};
		
		HashMap<Integer, HashMap<String, String>> values = new HashMap<Integer, HashMap<String, String>>();
		int i = 0;
		
		for(String field : fields) {
			HashMap<String, String> temp = new HashMap<String, String>();
			if(field.equals("transactionID")) {
				temp.put("data", "'" + transactionID + "'");
				values.put(i, temp);
			}else if(field.equals("player")) {
				temp.put("data", player);
				values.put(i, temp);
			}else if(field.equals("amount")) {
				temp.put("data", "" + amount);
				values.put(i, temp);
			}else if(field.equals("itemID")) {
				temp.put("data", "" + id);
				values.put(i, temp);
			}else if(field.equals("itemType")) {
				temp.put("kind", "INT");
				temp.put("data", "" + type);
				values.put(i, temp);
			}
			
			i++;
		}
		
		// Insert transaction into database as ready pickup!
		db.insert("#__api_gsw_pickups", fields, values).updateQuery();
	}
	
	public boolean inQueue(String player) {
		
		return false;
	}
}
