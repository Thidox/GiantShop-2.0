package nl.giantit.minecraft.GiantShop.API.GSW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.iDriver;

/**
 *
 * @author Giant
 */
public class InitDB {
	
	public static void init() {
		iDriver db = Database.Obtain().getEngine();
		if(!db.tableExists("#__api_gsw_pickups")){
			ArrayList<String> field = new ArrayList<String>();
			field.add("tablename");
			field.add("version");
			
			HashMap<Integer, HashMap<String, String>> d = new HashMap<Integer, HashMap<String, String>>();
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("data", "log");
			d.put(0, data);
			
			data = new HashMap<String, String>();
			data.put("data", "1.0");
			d.put(1, data);
			
			db.insert("#__versions", field, d).Finalize();
			db.updateQuery();
			
			HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
			data = new HashMap<String, String>();
			data.put("TYPE", "INT");
			data.put("LENGTH", "3");
			data.put("NULL", "false");
			data.put("A_INCR", "true");
			data.put("P_KEY", "true");
			fields.put("id", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "VARCHAR");
			data.put("LENGTH", "100");
			data.put("NULL", "false");
			fields.put("transactionID", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "VARCHAR");
			data.put("LENGTH", "100");
			data.put("NULL", "false");
			fields.put("player", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "INT");
			data.put("LENGTH", "10");
			data.put("NULL", "false");
			fields.put("amount", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "INT");
			data.put("LENGTH", "10");
			data.put("NULL", "false");
			fields.put("itemID", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "INT");
			data.put("LENGTH", "10");
			data.put("NULL", "true");
			fields.put("itemType", data);
			
			db.create("#__api_gsw_pickups").fields(fields).Finalize();
			db.updateQuery();
			
			GiantShop.log.log(Level.INFO, "[GSWAPI] Store pickup table successfully created!");
		}
	}
}
