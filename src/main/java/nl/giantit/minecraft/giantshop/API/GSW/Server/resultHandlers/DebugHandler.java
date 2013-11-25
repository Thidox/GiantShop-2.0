package nl.giantit.minecraft.giantshop.API.GSW.Server.resultHandlers;

import nl.giantit.minecraft.giantshop.GiantShop;

/**
 *
 * @author Giant
 */
public class DebugHandler implements ResultHandler {
	
	private String appName;
	
	public DebugHandler(String appName) {
		this.appName = appName;
	}
	
	@Override
	public void handle(String resultData) {
		if(resultData.startsWith("00")) {
			boolean error = false;
			boolean formatError = false;
			try{
				// Debug status parsing
				// Very not efficient this way, but I like it! :D
				
				// Avoid possibility of IndexOutOfBoundsException by using either the result length or 3 depending on smallest.
				int status = Integer.parseInt(resultData.substring(0, Math.min(resultData.length(), 3)));
				switch(status) {
					case 0x009:
					case 0x008:
					case 0x006:
					case 0x005:
					case 0x004:
					case 0x003:
					case 0x002:
					case 0x001:
						error = true;
						break;
					case 007:
						error = false;
						break;
					default:
						error = true;
						formatError = true;
						break;
				}
			}catch(NumberFormatException e) {
				error = true;
				formatError = true;
			}
			
			if(error) {
				GiantShop.getPlugin().getLogger().warning("[GSWAPI] An error occured while sending data to app " + this.appName + "!");
				if(formatError) {
					GiantShop.getPlugin().getLogger().warning("[GSWAPI] Aditionally an error occured while attempting to parse status code!");
				}
				GiantShop.getPlugin().getLogger().warning("[GSWAPI][" + this.appName + "][Debug] " + resultData);
				return;
			}
		}
		
		GiantShop.getPlugin().getLogger().info("[GSWAPI][" + this.appName + "][Debug] " + resultData);
	}
	
}
