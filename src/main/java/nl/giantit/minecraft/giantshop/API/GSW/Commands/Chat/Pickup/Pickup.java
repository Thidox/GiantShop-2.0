package nl.giantit.minecraft.giantshop.API.GSW.Commands.Chat.Pickup;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.giantshop.API.GSW.GSWAPI;
import nl.giantit.minecraft.giantshop.API.GSW.PickupQueue;
import nl.giantit.minecraft.giantshop.GiantShop;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class Pickup {
	
	public static void exec(Player player, String transactionID) {
		Messages mH = GiantShop.getPlugin().getMsgHandler();
		Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
		if(perms.has(player, "giantshop.api.web.pickup.pickup")) {
			PickupQueue pQ = GSWAPI.getInstance().getPickupQueue();
			if(pQ.inQueue(player.getName(), transactionID)) {
				pQ.deliver(player, pQ.get(player.getName(), transactionID));
				pQ.removeFromQueue(player.getName(), transactionID);
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("transactionID", transactionID);

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noTransactionForID", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "gsw pickup");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
	public static void getAll(Player player) {
		Messages mH = GiantShop.getPlugin().getMsgHandler();
		Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
		if(perms.has(player, "giantshop.api.web.pickup.all")) {
			PickupQueue pQ = GSWAPI.getInstance().getPickupQueue();
			if(pQ.inQueue(player.getName())) {
				pQ.deliver(player);
			}else{
				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "emptyQueue"));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "gsw pickup all");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
