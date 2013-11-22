package nl.giantit.minecraft.GiantShop.core.Tools.dbInit.Updates;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.giantcore.Database.iDriver;

import java.util.HashMap;
import java.util.logging.Level;

public class Items {  
	
	private static void update1_1() {
		iDriver db = GiantShop.getPlugin().getDB().getEngine();
		
		HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("TYPE", "INT");
		data.put("LENGTH", "3");
		data.put("NULL", "false");
		data.put("DEFAULT", "-1");
		fields.put("maxStock", data);

		db.alter("#__items").add(fields).updateQuery();
		
		data = new HashMap<String, String>();
		data.put("version", "1.1");
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("tableName", "items");
		
		db.update("#__versions").set(data).where(where).updateQuery();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating items table to version 1.1");
	}
	
	public static void run(double curV) {
		if(curV < 1.1)
			update1_1();
	}
}
