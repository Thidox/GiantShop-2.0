package nl.giantit.minecraft.GiantShop.API.GSL;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Locationer.Locationer;
import nl.giantit.minecraft.GiantShop.core.Database.db;

public class GSLAPI {

	private GiantShop plugin;
	private Locationer loc;
	
	public GSLAPI(GiantShop plugin) {
		this.plugin = plugin;
		this.loc = plugin.getLocHandler();
	}
	
	
	
}
