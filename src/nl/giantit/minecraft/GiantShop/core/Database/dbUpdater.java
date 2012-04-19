package nl.giantit.minecraft.GiantShop.core.Database;

import nl.giantit.minecraft.GiantShop.GiantShop;

import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class dbUpdater {
	
	public dbUpdater() {
		
	}
	
	public static void updateShop() {
		
	}
	
	public static void updateItemsMySQL(db DB, double curV, double v) {
		if(curV < 1.1) {
			DB.buildQuery("ALTER TABLE #__items \n", false, false, false, true);
			DB.buildQuery("ADD maxStock INT(3) NOT NULL DEFAULT '-1' \n", true, false, false);
			DB.buildQuery("AFTER stock", true, true, false);
			DB.updateQuery();
		}
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("version", String.valueOf(v));
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("tableName", "items");
		
		DB.update("#__versions").set(data).where(where).updateQuery();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating items table to version " + String.valueOf(v));
	}
	
	public static void updateItemsSQLite(db DB, double curV, double v) {
		if(curV < 1.1) {
			DB.buildQuery("ALTER TABLE #__items \n", false, false, false, true);
			DB.buildQuery("ADD maxStock INTEGER NOT NULL DEFAULT '-1'", true, true, false);
			DB.updateQuery();
		}
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("version", String.valueOf(v));
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("tableName", "items");
		
		DB.update("#__versions").set(data).where(where).updateQuery();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating items table to version " + String.valueOf(v));
	}
	
	public static void updateDisc() {
		
	}
}
