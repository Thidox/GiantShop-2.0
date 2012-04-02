package nl.giantit.minecraft.GiantShop.core.Database;

import nl.giantit.minecraft.GiantShop.core.Database.drivers.SQLite;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.MySQL;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.drivers.*;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.core.config;

/**
 *
 * @author Giant
 */
public class db {
	
	private static db instance;
	private GiantShop plugin;
	private config conf = config.Obtain();
	private MySQL mysql;
	private SQLite sqlite;
	private iDriver dbDriver;
	private String driver;
	
	private void dbInitMySQL() {
		if(!this.dbDriver.tableExists("#__versions")){
			this.dbDriver.buildQuery("CREATE TABLE #__versions \n");
			this.dbDriver.buildQuery("(tableName VARCHAR(100) NOT NULL, version DOUBLE NOT NULL DEFAULT '1.0');", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Revisions table successfully created!");
		}
		
		if(!this.dbDriver.tableExists("#__shops")){
			this.dbDriver.buildQuery("INSERT INTO #__versions \n");
			this.dbDriver.buildQuery("(tableName, version) \n", true);
			this.dbDriver.buildQuery("VALUES \n", true);
			this.dbDriver.buildQuery("('shops', '1.0');", true, true);
			this.dbDriver.updateQuery();
			
			this.dbDriver.buildQuery("CREATE TABLE #__shops \n");
			this.dbDriver.buildQuery("(id INT(3) NOT NULL AUTO_INCREMENT, name VARCHAR(100) NOT NULL, perms VARCHAR(100) DEFAULT NULL, world VARCHAR(100) NOT NULL, ", true);
			this.dbDriver.buildQuery("locMinX DOUBLE NOT NULL, locMinY DOUBLE NOT NULL, locMinZ DOUBLE NOT NULL, ", true);
			this.dbDriver.buildQuery("locMaxX DOUBLE NOT NULL, locMaxY DOUBLE NOT NULL, locMaxZ DOUBLE NOT NULL, ", true);
			this.dbDriver.buildQuery("PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Shops table successfully created!");
		}
		
		if(!this.dbDriver.tableExists("#__items")){
			this.dbDriver.buildQuery("INSERT INTO #__versions \n");
			this.dbDriver.buildQuery("(tableName, version) \n", true);
			this.dbDriver.buildQuery("VALUES \n", true);
			this.dbDriver.buildQuery("('items', '1.0');", true, true);
			this.dbDriver.updateQuery();
			
			this.dbDriver.buildQuery("CREATE TABLE #__items \n");
			this.dbDriver.buildQuery("(id INT(3) PRIMARY KEY, itemID INT(3) NOT NULL, type INT(3) default '-1', ", true);
			this.dbDriver.buildQuery("sellFor DOUBLE DEFAULT '-1', buyFor DOUBLE DEFAULT '-1', ", true);
			this.dbDriver.buildQuery("stock INT(3) DEFAULT '-1', perStack int(3) DEFAULT '1', ", true);
			this.dbDriver.buildQuery("shops VARCHAR(100) DEFAULT NULL);", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Items table successfully created!");
		}
		
		if(!this.dbDriver.tableExists("#__discounts")){
			this.dbDriver.buildQuery("INSERT INTO #__versions \n");
			this.dbDriver.buildQuery("(tableName, version) \n", true);
			this.dbDriver.buildQuery("VALUES \n", true);
			this.dbDriver.buildQuery("('discounts', '1.0');", true, true);
			this.dbDriver.updateQuery();
			
			this.dbDriver.buildQuery("CREATE TABLE #__discounts \n");
			this.dbDriver.buildQuery("(id INT(3) PRIMARY KEY, itemID INT(3) NOT NULL, discount INT(3) DEFAULT '10', ", true);
			this.dbDriver.buildQuery("user VARCHAR(100) DEFAULT NULL, `group` VARCHAR(100) DEFAULT NULL);", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Discounts table successfully created!");
		}
	}
	
	private void dbInitSQLite() {
		if(!this.dbDriver.tableExists("#__versions")){
			this.dbDriver.buildQuery("CREATE TABLE #__versions \n");
			this.dbDriver.buildQuery("(tableName VARCHAR(100) NOT NULL, version DOUBLE NOT NULL DEFAULT '1.0');", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Revisions table successfully created!");
		}
		
		if(!this.dbDriver.tableExists("#__shops")){
			this.dbDriver.buildQuery("INSERT INTO #__versions \n");
			this.dbDriver.buildQuery("(tableName, version) \n", true);
			this.dbDriver.buildQuery("VALUES \n", true);
			this.dbDriver.buildQuery("('shops', '1.0');", true, true);
			this.dbDriver.updateQuery();
			
			this.dbDriver.buildQuery("CREATE TABLE #__shops \n");
			this.dbDriver.buildQuery("(id INTEGER PRIMARY KEY, name VARCHAR(100) NOT NULL, perms VARCHAR(100) DEFAULT NULL, world VARCHAR(100) NOT NULL, ", true);
			this.dbDriver.buildQuery("locMinX DOUBLE NOT NULL, locMinY DOUBLE NOT NULL, locMinZ DOUBLE NOT NULL, ", true);
			this.dbDriver.buildQuery("locMaxX DOUBLE NOT NULL, locMaxY DOUBLE NOT NULL, locMaxZ DOUBLE NOT NULL);", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Shops table successfully created!");
		}
		
		if(!this.dbDriver.tableExists("#__items")){
			this.dbDriver.buildQuery("INSERT INTO #__versions \n");
			this.dbDriver.buildQuery("(tableName, version) \n", true);
			this.dbDriver.buildQuery("VALUES \n", true);
			this.dbDriver.buildQuery("('items', '1.0');", true, true);
			this.dbDriver.updateQuery();
			
			this.dbDriver.buildQuery("CREATE TABLE #__items \n");
			this.dbDriver.buildQuery("(id INTEGER PRIMARY KEY, itemID INT(3) NOT NULL, type INT(3) default '-1', ", true);
			this.dbDriver.buildQuery("sellFor DOUBLE DEFAULT '-1', buyFor DOUBLE DEFAULT '-1', ", true);
			this.dbDriver.buildQuery("stock INT(3) DEFAULT '-1', perStack int(3) DEFAULT '1', ", true);
			this.dbDriver.buildQuery("shops VARCHAR(100) DEFAULT NULL);", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Items table successfully created!");
		}
		
		if(!this.dbDriver.tableExists("#__discounts")){
			this.dbDriver.buildQuery("INSERT INTO #__versions \n");
			this.dbDriver.buildQuery("(tableName, version) \n", true);
			this.dbDriver.buildQuery("VALUES \n", true);
			this.dbDriver.buildQuery("('discounts', '1.0');", true, true);
			this.dbDriver.updateQuery();
			
			this.dbDriver.buildQuery("CREATE TABLE #__discounts \n");
			this.dbDriver.buildQuery("(id INTEGER PRIMARY KEY, itemID INT(3) NOT NULL, discount INT(3) DEFAULT '10', ", true);
			this.dbDriver.buildQuery("user VARCHAR(100) DEFAULT NULL, `group` VARCHAR(100) DEFAULT NULL);", true, true, false);
			this.dbDriver.updateQuery();
			
			plugin.log.log(Level.INFO, "Discounts table successfully created!");
		}
	}
	
	private void dbUpdateMySQL() {
		//do some update stuff
		this.dbDriver.buildQuery("SELECT tableName, version FROM #__versions");
		ArrayList<HashMap<String, String>> res = this.mysql.execQuery();
		for(int i = 0; i < res.size(); i++) {
			HashMap<String, String> row = res.get(i);
			String table = row.get("tableName");
			Double version = Double.parseDouble(row.get("version"));
			
			if(table.equalsIgnoreCase("shops") && version < 1.0)
				dbUpdater.updateShop();
			else if(table.equalsIgnoreCase("items") && version < 1.0)
				dbUpdater.updateItems();
			else if(table.equalsIgnoreCase("discounts") && version < 1.0)
				dbUpdater.updateDisc();
		}
	}
	
	private void dbUpdateSQLite() {
		//do some update stuff
		this.dbDriver.buildQuery("SELECT tableName, version FROM #__versions");
		ArrayList<HashMap<String, String>> res = this.sqlite.execQuery();
		for(int i = 0; i < res.size(); i++) {
			HashMap<String, String> row = res.get(i);
			String table = row.get("tableName");
			Double version = Double.parseDouble(row.get("version"));
			
			if(table.equalsIgnoreCase("shops") && version < 1.0)
				dbUpdater.updateShop();
			else if(table.equalsIgnoreCase("items") && version < 1.0)
				dbUpdater.updateItems();
			else if(table.equalsIgnoreCase("discounts") && version < 1.0)
				dbUpdater.updateDisc();
		}
	}
	
	private void init() {
		driver = conf.getString("GiantShop.db.driver");
		
		if(driver.equalsIgnoreCase("MySQL")) {
			this.mysql = MySQL.Obtain();
			this.dbDriver = MySQL.Obtain();
			this.dbInitMySQL();
			this.dbUpdateMySQL();
		}else{
			this.sqlite = SQLite.Obtain();
			this.dbDriver = SQLite.Obtain();
			this.dbInitSQLite();
			this.dbUpdateSQLite();
		}
		
		db.instance = this;
	}
	
	public db(GiantShop plugin) {
		this.plugin = plugin;
		this.init();
	};
	
	public void buildQuery(String string) {
		buildQuery(string, false, false, false);
		return;
	}
	
	public void buildQuery(String string, Boolean add) {
		buildQuery(string, add, false, false);
		return;
	}
	
	public void buildQuery(String string, Boolean add, Boolean finalize) {
		buildQuery(string, add, finalize, false);
		return;
	}
	
	public void buildQuery(String string, Boolean add, Boolean finalize, Boolean debug) {
		this.dbDriver.buildQuery(string, add, finalize, debug);
	}
	
	public void buildQuery(String string, Integer add) {
		buildQuery(string, add, false, false);
		return;
	}
	
	public void buildQuery(String string, Integer add, Boolean finalize) {
		buildQuery(string, add, finalize, false);
		return;
	}
	
	public void buildQuery(String string, Integer add, Boolean finalize, Boolean debug) {
		this.dbDriver.buildQuery(string, add, finalize, debug);
	}
	
	public ArrayList<HashMap<String, String>> execQuery() {
		return this.dbDriver.execQuery();
	}
	
	public ArrayList<HashMap<String, String>> execQuery(Integer qID) {
		return this.dbDriver.execQuery(qID);
	}
	
	public void updateQuery() {
		this.dbDriver.updateQuery();
	}
	
	public void updateQuery(Integer qID) {
		this.dbDriver.updateQuery(qID);
	}
	
	public int countResult() {
		return this.dbDriver.countResult();
	}
	
	public int countResult(Integer qID) {
		return this.dbDriver.countResult(qID);
	}
	
	public iDriver select(String field) {
		return this.dbDriver.select(field);
	}
	
	public iDriver select(ArrayList<String> fields) {
		return this.dbDriver.select(fields);
	}
	
	public iDriver select(HashMap<String, String> fields) {
		return this.dbDriver.select(fields);
	}
	
	public iDriver from(String table) {
		return this.dbDriver.from(table);
	}
	
	public iDriver where(HashMap<String, String> fields) {
		return this.dbDriver.where(fields);
	}
	
	public iDriver where(HashMap<String, HashMap<String, String>> fields, Boolean shite) {
		return this.dbDriver.where(fields, shite);
	}
	
	public iDriver orderBy(HashMap<String, String> fields) {
		return this.dbDriver.orderBy(fields);
	}
	
	public iDriver limit(int limit) {
		return this.dbDriver.limit(limit);
	}
	
	public iDriver limit(int limit, Integer start) {
		return this.dbDriver.limit(limit, start);
	}
	
	public iDriver insert(String table, ArrayList<String> fields, ArrayList<HashMap<Integer, HashMap<String, String>>> values) {
		return this.dbDriver.insert(table, fields, values);
	}
	
	public iDriver update(String table) {
		return this.dbDriver.update(table);
	}
	
	public iDriver set(HashMap<String, String> fields) {
		return this.dbDriver.set(fields);
	}
	
	public iDriver set(HashMap<String, HashMap<String, String>> fields, Boolean shite) {
		return this.dbDriver.set(fields, shite);
	}
	
	public iDriver delete(String table) {
		return this.dbDriver.delete(table);
	}
	
	public static db Obtain() {
		if(db.instance != null)
			return db.instance;
		
		return null;
	}
}
