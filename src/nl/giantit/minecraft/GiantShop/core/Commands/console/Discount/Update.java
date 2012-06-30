package nl.giantit.minecraft.GiantShop.core.Commands.console.Discount;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Tools.Discount.Discount;
import nl.giantit.minecraft.GiantShop.core.Tools.Discount.Discounter;

import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class Update {

	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	private static Discounter dH = GiantShop.getPlugin().getDiscounter();
	
	public static void exec(CommandSender p, String[] args) {
		int id = 0;
		Integer itemID = null;
		Integer type = null;
		int discount = 0;
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
			}else if(args[i].startsWith("-d:")) {
				try{
					discount = Integer.parseInt(args[i].replaceFirst("-d:", ""));
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
		
		if(discount > 0) {
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
				int res = dH.updateDiscount(id, discount);
				if(res == 0) {
					//Success!
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("id", String.valueOf(id));
					data.put("newDiscount", String.valueOf(discount));
					
					Heraut.say(p, mH.getConsoleMsg(Messages.msgType.ADMIN, "discountUpdated", data));
					return;
				}
			}
			
			//Discount not found!
			Heraut.say(p, mH.getConsoleMsg(Messages.msgType.ERROR, "discountNotFound"));
		}else{
			Heraut.say(p, mH.getConsoleMsg(Messages.msgType.ERROR, "discountZero"));
		}
	}
}
