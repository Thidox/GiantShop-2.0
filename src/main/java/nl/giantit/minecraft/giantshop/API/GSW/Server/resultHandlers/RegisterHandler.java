package nl.giantit.minecraft.giantshop.API.GSW.Server.resultHandlers;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;

import java.util.HashMap;
import nl.giantit.minecraft.giantshop.API.GSW.GSWAPI;
import nl.giantit.minecraft.giantshop.API.GSW.Server.ShopSender;
import nl.giantit.minecraft.giantshop.GiantShop;
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
			ShopSender ss = GSWAPI.getInstance().getTrustedApp(appName);
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("app", appName);
			data.put("key", resultData.substring(4));
			data.put("uri", ss.getActivationURI().replace("%key", data.get("key")));
			
			Heraut.say(p, mH.getMsg(Messages.msgType.MAIN, "gswAPIRegKey", data));
			Heraut.say(p, mH.getMsg(Messages.msgType.MAIN, "gswAPIRegURI", data));
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("app", appName);
			data.put("error", "Invalid return data received!");
			Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "gswAPIWriteError", data));
			Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "error", data));
		}
	}
}
