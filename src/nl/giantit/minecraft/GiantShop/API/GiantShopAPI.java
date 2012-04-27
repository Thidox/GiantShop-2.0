package nl.giantit.minecraft.GiantShop.API;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.API.stock.stockAPI;
import nl.giantit.minecraft.GiantShop.core.Items.Items;

/**
 *
 * @author Giant
 */
public class GiantShopAPI {
	
	private GiantShop plugin;
	private static GiantShopAPI instance;
	private static Items iH;
	
	private stockAPI sAPI;
	
	private void setInstance() {
		instance = this;
	}
	
	private GiantShopAPI(GiantShop plugin) {
		this.plugin = plugin;
		this.setInstance();
		this.sAPI = new stockAPI(plugin);
		this.iH = plugin.getItemHandler();
	}
	
	public stockAPI getStockAPI() {
		return this.sAPI;
	}
	
	public Items getItemHandlerAPI() {
		return this.iH;
	}
	
	public static GiantShopAPI Obtain() {
		if(instance != null)
			return instance;
		
		return new GiantShopAPI(GiantShop.getPlugin());
	}
}
