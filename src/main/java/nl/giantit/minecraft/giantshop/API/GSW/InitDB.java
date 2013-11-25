package nl.giantit.minecraft.giantshop.API.GSW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.database.query.Column;
import nl.giantit.minecraft.giantcore.database.query.CreateQuery;
import nl.giantit.minecraft.giantcore.database.query.InsertQuery;

/**
 *
 * @author Giant
 */
public class InitDB {
	
	public static void init() {
		Driver db = GiantShop.getPlugin().getDB().getEngine();
		if(!db.tableExists("#__api_gsw_pickups")){
			ArrayList<String> field = new ArrayList<String>();
			field.add("tablename");
			field.add("version");
			
			InsertQuery iQ = db.insert("#__versions");
			iQ.addFields(field);
			iQ.addRow();
			iQ.assignValue("tablename", "api_gsw_pickups");
			iQ.assignValue("version", "1.0", InsertQuery.ValueType.RAW);
			
			iQ.exec();
			
			CreateQuery cQ = db.create("#__api_gsw_pickups");
			Column id = cQ.addColumn("id");
			id.setDataType(Column.DataType.INT);
			id.setLength(3);
			id.setAutoIncr();
			id.setPrimaryKey();
			
			Column tID = cQ.addColumn("transactionID");
			tID.setDataType(Column.DataType.VARCHAR);
			tID.setLength(100);
			
			Column p = cQ.addColumn("player");
			p.setDataType(Column.DataType.VARCHAR);
			p.setLength(100);
			
			Column a = cQ.addColumn("amount");
			a.setDataType(Column.DataType.INT);
			a.setLength(10);
			
			Column iID = cQ.addColumn("ItemID");
			iID.setDataType(Column.DataType.INT);
			iID.setLength(10);
			
			Column iT = cQ.addColumn("itemType");
			iT.setDataType(Column.DataType.INT);
			iT.setLength(10);
			iT.setNull();
			
			cQ.exec();
			
			GiantShop.log.log(Level.INFO, "[GSWAPI] Store pickup table successfully created!");
		}
	}
}
