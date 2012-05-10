package nl.giantit.minecraft.GiantShop.core.Logger;

public enum LoggerType {
	BUY(1),
	GIFT(2),
	SELL(3),
	ADD(4),
	UPDATE(5),
	REMOVE(6),
	APISTOCKUPDATE(7),
	APIMAXSTOCKUPDATE(8),
	UNKNOWN(20);
	
	
	private int id;
	
	private LoggerType(Integer i) {
		id = i;
	}
	
	public int getID() {
		return id;
	}
}
