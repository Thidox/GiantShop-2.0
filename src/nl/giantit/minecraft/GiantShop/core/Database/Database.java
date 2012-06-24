package nl.giantit.minecraft.GiantShop.core.Database;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.*;

import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class Database {
	
	private enum dbType {
		MySQL("MySQL"),
		SQLite("SQLite");
		
		String value;
		
		private dbType(String s) {
			this.value = s;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	private static HashMap<String, Database> instance = new HashMap<String, Database>();
	private GiantShop plugin;
	private iDriver dbDriver;
	private dbType t;
	
	private Database(HashMap<String, String> conf, String instance) {
		if(instance == null)
			instance = "0";
		
		if(conf.get("driver").equalsIgnoreCase("MySQL")) {
			t = dbType.MySQL;
			this.dbDriver = MySQL.Obtain(conf, instance);
		}else{
			t = dbType.SQLite;
			this.dbDriver = SQLite.Obtain(conf, instance);
		}
	}
	
	public iDriver getEngine() {
		return this.dbDriver;
	}
	
	public String getType() {
		return t.toString();
	}
	
	public static Database Obtain() {
		String instance = "0";
		
		if(!Database.instance.containsKey(instance))
			return null;
		
		return Database.instance.get(instance);
	}
	
	public static Database Obtain(String instance) {
		if(instance == null)
			instance = "0";
		
		if(!Database.instance.containsKey(instance))
			return null;
		
		return Database.instance.get(instance);
	}
	
	public static Database Obtain(String instance, HashMap<String, String> conf) {
		if(instance == null)
			instance = "0";
		
		if(!Database.instance.containsKey(instance))
			Database.instance.put(instance, new Database(conf, instance));
		
		return Database.instance.get(instance);
	}
}
