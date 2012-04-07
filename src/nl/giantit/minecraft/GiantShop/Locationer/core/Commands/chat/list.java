package nl.giantit.minecraft.GiantShop.Locationer.core.Commands.chat;

import nl.giantit.minecraft.GiantShop.Misc.Heraut;

import org.bukkit.entity.Player;

/**
 *
 * @author Giant
 */
public class list {
	
	public static void list(Player player, String[] args) {
		/*int perPag = (conf.getInt("GiantShop.Location.perPage") > 0) ? conf.getInt("GiantShop.Location.perPage") : 5;
		int pag;
		if(args.length >= 3) {
			try {
				pag = Integer.valueOf(args[2].toString());
			}catch(Exception e) {
				pag = 1;
			}
		}else if(args.length >= 2) {
			try {
				pag = Integer.valueOf(args[1].toString());
			}catch(Exception e) {
				pag = 1;
			}
		}else
			pag = 1;
		
		int curPag = (pag > 0) ? pag : 1;
		int itemCount = shops.size();

		int pages = ((int)Math.ceil((double)itemCount / (double)perPag) < 1) ? 1 : (int)Math.ceil((double)itemCount / (double)perPag);
		int start = (curPag * perPag) - perPag;

		if(shops.isEmpty()) {
			Heraut.say("&d[&f" + conf.getString("GiantShop.global.name") + "&d]&c Sorry no shops yet :(");
			return;
		}else if(curPag > pages) {
			Heraut.say("&d[&f" + conf.getString("GiantShop.global.name") + "&d]&c My shop list only has " + pages + " pages!!");
			return;
		}else {
			Heraut.say("&d[&f" + conf.getString("GiantShop.global.name") + "&d]&f Showing available shops. Page &e" + curPag + "&f/&e" + pages);
			for(int i = start; i < (((start + perPag) > itemCount) ? itemCount : (start + perPag)); i++) {
				Indaface shop = shops.get(i);
				
				Heraut.say("&eID: &f" + shop.getID() + " &eName: &f" + shop.getName() + " &eWorld: &f" + shop.getWorldName() + " &eMinX: &f"
							+ (shop.getLocation()).get(0).getBlockX()	+ " &eMinY: &f"	+ (shop.getLocation()).get(0).getBlockY() + " &eMinZ: &f"
							+ (shop.getLocation()).get(0).getBlockZ() + " &eMaxX: &f" + (shop.getLocation()).get(1).getBlockX() + " &eMaxY: &f"
							+ (shop.getLocation()).get(1).getBlockY() + " &eMaxZ: &f" + (shop.getLocation()).get(1).getBlockZ());
			}
		}*/
	}
}
