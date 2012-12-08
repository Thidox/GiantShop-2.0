package nl.giantit.minecraft.GiantShop.API.GSW.Server.resultHandlers;

import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class RegisterHandler implements ResultHandler {
	
	private String appName;
	private Player p;
	private Messages mH;
	
	public RegisterHandler(String appName, Player p) {
		this.appName = appName;
		this.p = p;
		this.mH = GiantShop.getPlugin().getMsgHandler();
	}
	
	@Override
	public void handle(String resultData) {
		if(resultData.startsWith("ERROR")) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("app", appName);
			data.put("error", resultData.substring(6));
			Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "gswAPIWriteError", data));
			Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "error", data));
		}else if(resultData.startsWith("KEY")) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("app", appName);
			data.put("key", resultData.substring(4));
			
			Heraut.say(p, mH.getMsg(Messages.msgType.MAIN, "gswAPIRegKey", data));
			GSWAPI.getInstance().getTrustedApp(appName);
		}
	}
}
