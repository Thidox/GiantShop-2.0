package nl.giantit.minecraft.giantshop.core.Logger;

public enum LoggerType {
	BUY(1, "buy"),
	GIFT(2, "gift"),
	SELL(3, "sell"),
	ADD(4, "add"),
	UPDATE(5, "update"),
	REMOVE(6, "remove"),
	APISTOCKUPDATE(7, "apistockupdate"),
	APIMAXSTOCKUPDATE(8, "apimaxstockupdate"),
	GSWAPITRANSACTION(9, "gswapitransaction"),
	UNKNOWN(20, "unknown");
	
	private int id;
	private String name;
	
	private LoggerType(Integer i, String n) {
		id = i;
		name = n;
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
