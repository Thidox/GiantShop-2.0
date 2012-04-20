package nl.giantit.minecraft.GiantShop.API;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.API.stock.stockAPI;

/**
 *
 * @author Giant
 */
public class GiantShopAPI {
	
	private GiantShop plugin;
	private static GiantShopAPI instance;
	
	private stockAPI sAPI;
	
	private void setInstance() {
		instance = this;
	}
	
	private GiantShopAPI(GiantShop plugin) {
		this.plugin = plugin;
		this.setInstance();
		this.sAPI = new stockAPI(plugin);
	}
	
	public stockAPI getStockAPI() {
		return sAPI;
	}
	
	public static GiantShopAPI Obtain() {
		if(instance != null)
			return instance;
		
		return new GiantShopAPI(GiantShop.getPlugin());
	}
}
