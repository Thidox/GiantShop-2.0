package nl.giantit.minecraft.GiantShop.API.GSW;

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
	
}
