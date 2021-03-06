package nl.giantit.minecraft.giantshop.API.GSW.Server;

import nl.giantit.minecraft.giantcore.core.Eco.iEco;

import nl.giantit.minecraft.giantshop.API.GSW.GSWAPI;
import nl.giantit.minecraft.giantshop.API.GSW.PickupQueue;
import nl.giantit.minecraft.giantshop.API.GiantShopAPI;
import nl.giantit.minecraft.giantshop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.giantshop.API.stock.core.itemStock;
import nl.giantit.minecraft.giantshop.API.stock.stockAPI;
import nl.giantit.minecraft.giantshop.GiantShop;
import org.bukkit.scheduler.BukkitRunnable;
//import com.google.gson.Gson;

/**
 *
 * @author Giant
 */
public class ShopWorker extends BukkitRunnable {
	
	private GiantShop p;
	
	private String[] data;
	private stockAPI sA;
	
	public ShopWorker(GiantShop p, String[] data) {
		this.p = p;
		this.data = data;
		this.sA = GiantShopAPI.Obtain().getStockAPI();
	}
	
	@Override
	public void run() {
		ShopSender ss = GSWAPI.getInstance().getTrustedApp(data[0]);
		iEco eH = this.p.getEcoHandler().getEngine();
		if(eH == null) {
			// Economy engine isn't loaded! Error out now!
			try {
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"001\", \"Error\":\"Economy engine not loaded!\"}");
			}catch(Exception e) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
			
			return;
		}
		
		if(!data[3].matches("[0-9]+:[0-9]+:[0-9]+")) {
			// invalid item format passed!
			try {
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"002\", \"Error\":\"Can not understand purchase!\"}");
			}catch(Exception e) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
			
			return;
		}
		
		/*String[] items = null;
		if(data[3].startsWith("[")) {
			items = (new Gson()).fromJson(data[3], String[].class);
		}else{
			items = new String[1];
			items[0] = data[3];
		}*/
		
		String[] purchaseData = data[3].split(":");
		int id;
		Integer type;
		int amount;
		try {
			id = Integer.parseInt(purchaseData[0]);
			type = Integer.parseInt(purchaseData[1]);
			if(type == 0 || type == -1) {
				type = null;
			}
			
			amount = Integer.parseInt(purchaseData[2]);
		}catch(NumberFormatException e) {
			// How did non integers even go through?!
			try {
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"003\", \"Error\":\"Somehow we failed parsing your integers into integers!\"}");
			}catch(Exception ex) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
			
			return;
		}
		
		itemStock iS;
		try {
			iS = sA.getItemStock(id, type);
		}catch(ItemNotFoundException e) {
			// How did non integers even go through?!
			try {
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"004\", \"Error\":\"Requested item does not exist!\"}");
			}catch(Exception ex) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
			
			return;
		}
		
		if(null == iS) {
			try {
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"004\", \"Error\":\"Requested item does not exist!\"}");
			}catch(Exception ex) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
			
			return;
		}
		
		if(iS.getStock() != -1 && iS.getStock() < (amount * iS.getPerStack())) {
			// Not enough quantity available for item
			try {
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"005\", \"Error\":\"Requested item does not have enough stock!\"}");
			}catch(Exception e) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
			
			return;
		}
		
		if(eH.getBalance(data[2]) < iS.getCost(amount)) {
			// Player has not enough money!
			// Might also occur if economy engine does not support offline players!
			try {
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"006\", \"Error\":\"Not enough money!\"}");
			}catch(Exception e) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
			
			return;
		}
		
		if(iS.getStock() != -1) {
			iS.setStock(iS.getStock() - (amount * iS.getPerStack()));
		}
		
		PickupQueue pQ = GSWAPI.getInstance().getPickupQueue();
		// System does not accept 2 transactions with same ID!
		if(!pQ.inQueue(data[2], data[4])) {
			eH.withdraw(data[2], iS.getCost(amount));
			pQ.addToQueue(data[4], data[2], amount, id, (null == type) ? -1 : type);
		
			try {
				// Purchase is valid, pass success status!
				// Purchase may still fail on bad database connection!
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Success\", \"statusCode\":\"007\"}");
			}catch(Exception e) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
		}else{
			try {
				// Purchase is valid, pass success status!
				// Purchase may still fail on bad database connection!
				ss.write("STATUS {\"action\":\"SHOP\", \"transactionID\":\"" + data[4] + "\", \"status\":\"Failed\", \"statusCode\":\"008\", \"Error\":\"A transaction with the same ID already exists!\"}");
			}catch(Exception e) {
				GiantShop.getPlugin().getLogger().severe("[GSWAPI] Error occured whilst attempting to write data to web app " + data[0]);
			}
		}
	}
}
