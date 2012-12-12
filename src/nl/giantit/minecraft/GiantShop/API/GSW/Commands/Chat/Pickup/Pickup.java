package nl.giantit.minecraft.GiantShop.API.GSW.Commands.Chat.Pickup;

import java.util.HashMap;
import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.API.GSW.PickupQueue;
import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.core.perms.Permission;
import org.bukkit.entity.Player;

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
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("transactionID", transactionID);

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noTransactionForID", data));
			}
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
		}
	}
}
