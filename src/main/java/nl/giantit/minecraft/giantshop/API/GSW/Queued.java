package nl.giantit.minecraft.giantshop.API.GSW;

import nl.giantit.minecraft.giantshop.API.GiantShopAPI;
import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.Items.ItemID;
import nl.giantit.minecraft.giantshop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.giantshop.API.stock.stockAPI;

/**
 *
 * @author Giant
 */
public class Queued {
	
	private int id;
	private int type;
	private Integer itemType;
	private int amount;
	private int perStack;
	private String transactionID;
	
	public Queued(int id, int type, int amount, String transactionID) {
		this.id = id;
		this.type = type;
		this.itemType = (type <= 0) ? null : type;
		this.amount = amount;
		this.transactionID = transactionID;
		
		stockAPI sA = GiantShopAPI.Obtain().getStockAPI();
		try {
			this.perStack = sA.getItemStock(id, this.itemType).getPerStack();
		}catch(ItemNotFoundException e) {
			this.perStack = 1;
		}catch(NullPointerException e) {
			this.perStack = 1;
		}
		
		this.perStack = this.perStack < 1 ? 1 : this.perStack;
	}
	
	public int getItemID() {
		return this.id;
	}
	
	public int getItemType() {
		return this.type;
	}
	
	public ItemID getItemIDOBJ() {
		return GiantShop.getPlugin().getItemHandler().getItemIDByName(GiantShop.getPlugin().getItemHandler().getItemNameByID(this.id, this.itemType));
	}
	
	public String getItemName() {
		return GiantShop.getPlugin().getItemHandler().getItemNameByID(this.id, this.itemType);
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public int getStackAmount() {
		return this.amount * this.perStack;
	}
	
	public String getTransactionID() {
		return this.transactionID;
	}
	
	@Override
	public String toString() {
		return "{transaction: " + this.transactionID + ", itemID: " + this.id + ", itemType: " + this.type + ", amount: " + this.amount + "}";
	}
	
}
