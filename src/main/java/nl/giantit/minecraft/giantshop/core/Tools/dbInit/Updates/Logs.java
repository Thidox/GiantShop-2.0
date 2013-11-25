package nl.giantit.minecraft.giantshop.core.Tools.dbInit.Updates;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantcore.database.Driver;

/**
 *
 * @author Giant
 */
public class Logs {
	
	private static void update1_1() {
		Driver db = GiantShop.getPlugin().getDB().getEngine();
		
	}
	
	public static void run(double curV) {
		if(curV < 1.1)
			update1_1();
	}
}
