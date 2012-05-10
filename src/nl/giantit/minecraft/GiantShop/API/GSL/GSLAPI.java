package nl.giantit.minecraft.GiantShop.API.GSL;

import nl.giantit.minecraft.GiantShop.API.stock.core.itemStock;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.Items.*;

public class GSLAPI {

	private GiantShop plugin;
	private Items iH;
	
	public GSLAPI(GiantShop plugin) {
		this.plugin = plugin;
		this.iH = plugin.getItemHandler();
	}
	
}
