package nl.giantit.minecraft.giantshop.core.Commands.Console.Discount;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.core.Items.Items;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.Tools.Discount.Discount;
import nl.giantit.minecraft.giantshop.core.Tools.Discount.Discounter;

import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class Remove {

	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	private static Discounter dH = GiantShop.getPlugin().getDiscounter();
	
	public static void exec(CommandSender p, String[] args) {
		int id = 0;
		Integer itemID = null;
		Integer type = null;
		String user = null;
		String group = null;
		Boolean UnF = false; //User not found
		
		for(int i = 2; i < args.length; i++) {
			if(args[i].startsWith("-id:")) {
				try{
					id = Integer.parseInt(args[i].replaceFirst("-id:", ""));
				}catch(NumberFormatException e) {
					//ignore
				}
				continue;
			}else if(args[i].startsWith("-i:")) {
				try{
					itemID = Integer.parseInt(args[i].replaceFirst("-i:", ""));
				}catch(NumberFormatException e) {
					//ignore
				}
				continue;
			}else if(args[i].startsWith("-t:")) {
				try{
					type = Integer.parseInt(args[i].replaceFirst("-t:", ""));
					type = (type <= 0) ? null : type;
				}catch(NumberFormatException e) {
					//ignore
				}
				continue;
			}else if(args[i].startsWith("-u:")) {
				user = args[i].replaceFirst("-u:", "");
				if(Misc.getPlayer(user) != null) {
					user = Misc.getPlayer(user).getName();
				}else{
					UnF = true;
				}
				continue;
			}else if(args[i].startsWith("-g:")) {
				group = args[i].replaceFirst("-g:", "");
				continue;
			}
		}
		
		if(id <= 0 && itemID != null) {
			if(iH.isValidItem(id, type)) {
				if(user != null && !UnF) {
					Discount d = dH.getDiscount(iH.getItemIDByName(iH.getItemNameByID(itemID, type)), user, false);
					if(d != null) {
						id = d.getDiscountID();
					}else{
						id = 0;
					}
				}else if(group != null) {
					Discount d = dH.getDiscount(iH.getItemIDByName(iH.getItemNameByID(itemID, type)), group, true);
					if(d != null) {
						id = d.getDiscountID();
					}else{
						id = 0;
					}
				}
			}else{
				//Invalid item!
				Heraut.say(p, mH.getConsoleMsg(Messages.msgType.ERROR, "itemNotFound"));
				return;
			}
		}
		
		if(id > 0) {
			int res = dH.removeDiscount(id);
			if(res == 0) {
				//Success!
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("id", String.valueOf(id));
				
				Heraut.say(p, mH.getConsoleMsg(Messages.msgType.ADMIN, "discountRemoved", data));
				return;
			}
		}
		
		//Discount not found!
		Heraut.say(p, mH.getConsoleMsg(Messages.msgType.ERROR, "discountNotFound"));
	}
}
