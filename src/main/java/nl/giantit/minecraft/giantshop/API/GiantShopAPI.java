package nl.giantit.minecraft.giantshop.API;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.API.stock.stockAPI;
import nl.giantit.minecraft.giantshop.API.GSL.GSLAPI;
import nl.giantit.minecraft.giantshop.API.GSW.GSWAPI;
import nl.giantit.minecraft.giantshop.core.Items.Items;
import nl.giantit.minecraft.giantshop.core.config;

/**
 *
 * @author Giant
 */
public class GiantShopAPI {
	
	private GiantShop plugin;
	private static GiantShopAPI instance;
	private static Items iH;
	
	private stockAPI sAPI;
	private GSLAPI gAPI;
	private GSWAPI gwAPI;
	
	private void setInstance() {
		GiantShopAPI.instance = this;
	}
	
	private GiantShopAPI(GiantShop plugin) {
		this.plugin = plugin;
		this.setInstance();
		config c = config.Obtain();
		if(!c.isLoaded()) {
			return;
		}
		
		if(c.getBoolean("GiantShop.API.useStockAPI")) {
			this.sAPI = new stockAPI(plugin);
		}
		
		if(c.getBoolean("GiantShop.API.useGSLAPI")) {
			this.gAPI = new GSLAPI(plugin);
		}
		
		if(c.getBoolean("GiantShop.API.useGSWAPI")) {
			this.gwAPI = new GSWAPI(plugin);
		}
		
		GiantShopAPI.iH = plugin.getItemHandler();
	}
	
	public void stop() {
		if(null != this.gwAPI) {
			this.gwAPI.shutdown();
		}
	}
	
	public stockAPI getStockAPI() {
		return this.sAPI;
	}
	
	public GSLAPI getGSLAPI() {
		return this.gAPI;
	}
	
	public GSWAPI getGSWAPI() {
		return this.gwAPI;
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
