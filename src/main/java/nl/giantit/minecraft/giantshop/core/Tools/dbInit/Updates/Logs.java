package nl.giantit.minecraft.GiantShop.core.Tools.dbInit.Updates;

import java.util.HashMap;
import java.util.logging.Level;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.giantcore.Database.iDriver;

/**
 *
 * @author Giant
 */
public class Logs {
	
	private static void update1_1() {
		iDriver db = GiantShop.getPlugin().getDB().getEngine();
		
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
		data.put("NULL", "true");
		fields.put("type", data);

		data = new HashMap<String, String>();
		data.put("TYPE", "VARCHAR");
		data.put("LENGTH", "100");
		data.put("NULL", "true");
		fields.put("user", data);

		data = new HashMap<String, String>();
		data.put("TYPE", "TEXT");
		data.put("LENGTH", null);
		data.put("NULL", "true");
		fields.put("data", data);

		data = new HashMap<String, String>();
		data.put("TYPE", "BIGINT");
		data.put("LENGTH", "50");
		data.put("NULL", "false");
		data.put("DEFAULT", "0");
		fields.put("date", data);

		db.create("#__log_bac").fields(fields).Finalize();
		db.updateQuery();
		
		db.buildQuery("INSERT INTO #__log_bac (type, user, data, date) SELECT type, user,data, date FROM #__log", false, false, false, true);
		db.updateQuery();
		
		db.buildQuery("DROP TABLE #__log", false, false, false, true);
		db.updateQuery();
		
		db.create("#__log").fields(fields).Finalize();
		db.updateQuery();
		
		db.buildQuery("INSERT INTO #__log (type, user, data, date) SELECT type, user,data, date FROM #__log_bac", false, false, false, true);
		db.updateQuery();
		
		db.buildQuery("DROP TABLE #__log_bac", false, false, false, true);
		db.updateQuery();
		
		// Update versions table
		data = new HashMap<String, String>();
		data.put("version", "1.1");
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("tableName", "log");
		
		db.update("#__versions").set(data).where(where).updateQuery();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating logs table to version 1.1");
	}
	
	public static void run(double curV) {
		if(curV < 1.1)
			update1_1();
	}
}
