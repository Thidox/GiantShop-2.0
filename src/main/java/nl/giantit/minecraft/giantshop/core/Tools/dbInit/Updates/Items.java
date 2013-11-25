package nl.giantit.minecraft.giantshop.core.Tools.dbInit.Updates;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.database.query.AlterQuery;
import nl.giantit.minecraft.giantcore.database.query.Column;
import nl.giantit.minecraft.giantcore.database.query.UpdateQuery;

import java.util.logging.Level;

public class Items {  
	
	private static void update1_1() {
		Driver db = GiantShop.getPlugin().getDB().getEngine();
		AlterQuery aQ = db.alter("#__items");
		Column c = aQ.addColumn("maxStock");
		c.setDataType(Column.DataType.INT);
		c.setLength(3);
		c.setRawDefault("-1");
		
		aQ.exec();
		
		UpdateQuery uQ = db.update("#__versions");
		uQ.set("version", "1.1", UpdateQuery.ValueType.SET);
		uQ.where("tableName", "items");
		uQ.exec();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating items table to version 1.1");
	}
	
	public static void run(double curV) {
		if(curV < 1.1)
			update1_1();
	}
}
