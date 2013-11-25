package nl.giantit.minecraft.giantshop.core.Tools.dbInit.Updates;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.database.query.AlterQuery;
import nl.giantit.minecraft.giantcore.database.query.Column;
import nl.giantit.minecraft.giantcore.database.query.CreateQuery;
import nl.giantit.minecraft.giantcore.database.query.UpdateQuery;

import java.util.logging.Level;

public class Discounts {
	
	private static void update1_1() {
		Driver db = GiantShop.getPlugin().getDB().getEngine();
		
		AlterQuery aQ = db.alter("#__discounts");
		Column c = aQ.addColumn("type");
		c.setDataType(Column.DataType.INT);
		c.setLength(3);
		c.setRawDefault("-1");
		
		aQ.exec();
		
		UpdateQuery uQ = db.update("#__versions");
		uQ.set("version", "1.1", UpdateQuery.ValueType.SET);
		uQ.where("tableName", "discounts");
		uQ.exec();
		
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating discounts table to version 1.1");
	}
	
	private static void update1_2() {
		Driver db = GiantShop.getPlugin().getDB().getEngine();
		
		db.drop("#__discounts").exec();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Dropping old discounts table!");
		
		CreateQuery cQ = db.create("#__discounts");
		Column id = cQ.addColumn("id");
		id.setDataType(Column.DataType.INT);
		id.setLength(3);
		id.setAutoIncr();
		id.setPrimaryKey();

		Column iID = cQ.addColumn("itemID");
		iID.setDataType(Column.DataType.INT);
		iID.setLength(3);

		Column t = cQ.addColumn("type");
		t.setDataType(Column.DataType.INT);
		t.setLength(3);
		t.setRawDefault("-1");

		Column d = cQ.addColumn("discount");
		d.setDataType(Column.DataType.INT);
		d.setLength(3);
		d.setRawDefault("10");

		Column u = cQ.addColumn("user");
		u.setDataType(Column.DataType.VARCHAR);
		u.setLength(100);
		u.setNull();

		Column grp = cQ.addColumn("itemID");
		grp.setDataType(Column.DataType.VARCHAR);
		grp.setLength(100);
		grp.setNull();

		cQ.exec();
		
		UpdateQuery uQ = db.update("#__versions");
		uQ.set("version", "1.2", UpdateQuery.ValueType.SET);
		uQ.where("tableName", "discounts");
		uQ.exec();
		GiantShop.getPlugin().getLogger().log(Level.INFO, "Updating discounts table to version 1.2");
	}
	
	public static void run(double curV) {
		if(curV < 1.1)
			update1_1();
		
		if(curV < 1.2)
			update1_2();
	}
}
