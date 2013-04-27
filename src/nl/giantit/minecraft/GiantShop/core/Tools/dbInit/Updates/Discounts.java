package nl.giantit.minecraft.GiantShop.core.Tools.dbInit.Updates;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.giantcore.Database.iDriver;

import java.util.HashMap;
import java.util.logging.Level;

public class Discounts {
	
	private static void update1_1() {
		iDriver db = GiantShop.getPlugin().getDB().getEngine();
		
		HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("TYPE", "INT");
		data.put("LENGTH", "3");
		data.put("DEFAULT", "-1");
		fields.put("type", data);

		db.alter("#__discounts").add(fields).updateQuery();
		
		data = new HashMap<String, String>();
		data.put("version", "1.1");
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("tableName", "discounts");
		
		db.update("#__versions").set(data).where(where).updateQuery();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating discounts table to version 1.1");
	}
	
	private static void update1_2() {
		iDriver db = GiantShop.getPlugin().getDB().getEngine();
		
		db.buildQuery("DROP TABLE #__discounts", false, false, false, true);
		db.updateQuery();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Dropping old discounts table!");
		
		HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("TYPE", "INT");
		data.put("LENGTH", "3");
		data.put("NULL", "false");
		data.put("A_INCR", "true");
		data.put("P_KEY", "true");
		fields.put("id", data);
		
		data = new HashMap<String, String>();
		data.put("TYPE", "INT");
		data.put("LENGTH", "3");
		data.put("NULL", "false");
		fields.put("itemID", data);
		
		data = new HashMap<String, String>();
		data.put("TYPE", "INT");
		data.put("LENGTH", "3");
		data.put("DEFAULT", "-1");
		fields.put("type", data);
		
		data = new HashMap<String, String>();
		data.put("TYPE", "INT");
		data.put("LENGTH", "3");
		data.put("DEFAULT", "10");
		fields.put("discount", data);
		
		data = new HashMap<String, String>();
		data.put("TYPE", "VARCHAR");
		data.put("LENGTH", "100");
		data.put("NULL", "true");
		fields.put("user", data);
		
		data = new HashMap<String, String>();
		data.put("TYPE", "VARCHAR");
		data.put("LENGTH", "100");
		data.put("NULL", "true");
		fields.put("grp", data);
		
		db.create("#__discounts").fields(fields).Finalize();
		db.updateQuery();
		
		data = new HashMap<String, String>();
		data.put("version", "1.2");
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("tableName", "discounts");
		
		db.update("#__versions").set(data).where(where).updateQuery();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating discounts table to version 1.2");
	}
	
	public static void run(double curV) {
		if(curV < 1.1)
			update1_1();
		
		if(curV < 1.2)
			update1_2();
	}
}
