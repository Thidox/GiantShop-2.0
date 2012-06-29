package nl.giantit.minecraft.GiantShop.core.Commands.chat.Discount;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.perms.Permission;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class Add {

	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	
	public static void exec(Player p, String[] args) {
		if(perms.has(p, "giantshop.admin.discount.add")) {
			int id = 0;
			Integer type = null;
			int discount = 0;
			String user = null;
			String group = null;
			Boolean UnF = false; //User not found
			
			for(int i = 2; i < args.length; i++) {
				if(args[i].startsWith("-i:")) {
					try{
						id = Integer.parseInt(args[i].replaceFirst("-i:", ""));
					}catch(NumberFormatException e) {
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
			
			if(iH.isValidItem(id, type)) {
				ItemID iID = iH.getItemIDByName(iH.getItemNameByID(id, type));
				if(discount > 0) {
					if(user != null && !UnF) {
						int a = GiantShop.getPlugin().getDiscounter().addDiscount(iID, discount, user, false);
						if(a == 0) {
							HashMap<String, String> data = new HashMap<String, String>();
							data.put("discount", String.valueOf(discount));
							data.put("item", iH.getItemNameByID(id, type));
							data.put("grplay", "player");
							data.put("for", user);
							
							Heraut.say(p, mH.getMsg(Messages.msgType.ADMIN, "discountAdd", data));
						}else{
							Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "discountDupe"));
						}
					}else if(group != null) {
						int a = GiantShop.getPlugin().getDiscounter().addDiscount(iID, discount, group, true);
						if(a == 0) {
							HashMap<String, String> data = new HashMap<String, String>();
							data.put("discount", String.valueOf(discount));
							data.put("item", iH.getItemNameByID(id, type));
							data.put("grplay", "group");
							data.put("for", group);
							
							Heraut.say(p, mH.getMsg(Messages.msgType.ADMIN, "discountAdd", data));
						}else{
							Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "discountDupe"));
						}
					}else{
						if(!UnF) {
							Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "discountNoOwner"));
						}else{
							HashMap<String, String> data = new HashMap<String, String>();
							data.put("player", user);
							
							Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "playerNotFound", data));
						}
					}
				}else{
					Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "discountZero"));
				}
			}else{
				Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "discount add");

			Heraut.say(p, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
