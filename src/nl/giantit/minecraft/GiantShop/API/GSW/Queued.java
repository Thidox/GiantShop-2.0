package nl.giantit.minecraft.GiantShop.API.GSW;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;

/**
 *
 * @author Giant
 */
public class Queued {
	
	private int id;
	private int type;
	private int amount;
	private String transactionID;
	
	public Queued(int id, int type, int amount, String transactionID) {
		this.id = id;
		this.type = type;
		this.amount = amount;
		this.transactionID = transactionID;
	}
	
	public int getItemID() {
		return this.id;
	}
	
	public int getItemType() {
		return this.type;
	}
	
	public ItemID getItemIDOBJ() {
		return GiantShop.getPlugin().getItemHandler().getItemIDByName(GiantShop.getPlugin().getItemHandler().getItemNameByID(id, type));
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public String getTransactionID() {
		return this.transactionID;
	}
	
}
