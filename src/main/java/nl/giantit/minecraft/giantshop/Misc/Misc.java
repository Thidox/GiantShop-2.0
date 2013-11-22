package nl.giantit.minecraft.GiantShop.Misc;

import nl.giantit.minecraft.GiantShop.GiantShop;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.giantit.minecraft.GiantShop.core.config;
/**
 *
 * @author Giant
 */
public class Misc {
	
	private static HashMap<String, OfflinePlayer> players = new HashMap<String, OfflinePlayer>();

	private static OfflinePlayer getOfflinePlayer(final String name) {
	
		OfflinePlayer found = null;
		int lastLength = Integer.MAX_VALUE;
		for(OfflinePlayer p : GiantShop.getPlugin().getSrvr().getOfflinePlayers()) {
			if(p.getFirstPlayed() <= 0)
				continue;
			
			if (p.getName().toLowerCase().startsWith(name.toLowerCase())) {
				int length = p.getName().length() - name.length();
				if(length < lastLength) {
					found = p;
					lastLength = length;
				}
				
				if(length == 0)
					break;
			}
		}
		
		if(found != null)
			players.put(name, found);
		
		return found;
	}
	
	public static boolean isEither(String target, String is, String either) {
		if(target.equals(is) || target.equals(either))
			return true;
		
		return false;
	}
	
	public static boolean isEitherIgnoreCase(String target, String is, String either) {
		if(target.equalsIgnoreCase(is) || target.equalsIgnoreCase(either))
			return true;
		
		return false;
	}
	
	public static boolean isAny(String target, String... arr) {
		for(String is : arr) {
			if(target.equals(is))
				return true;
		}
		
		return false;
	}
	
	public static boolean isAnyIgnoreCase(String target, String... arr) {
		for(String is : arr) {
			if(target.equalsIgnoreCase(is))
				return true;
		}
		
		return false;
	}
	
	public static Boolean contains(List<String> haystack, String needle) {
		for(String hay : haystack) {
			hay = hay.replace("[", "");
			hay = hay.replace("]", "");
			if(hay.toLowerCase().equalsIgnoreCase(needle.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public static String join(String glue, String... input) {
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for(String string : input) {
			sb.append(string);
			if(i < input.length - 1)
				sb.append(glue);
			
			i++;
		}
		
		return sb.toString();
    }
	
	public static double Round(double r, int precision) {
		if(precision < 1)
			precision = 1;
		
		double p = (double)Math.pow(10,precision);
		
		return Math.round(r * p) / p;
	}
	
	public static float Round(float r, int precision) {
		if(precision < 1)
			precision = 1;
		
		float p = (float)Math.pow(10,precision);
		
		return Math.round(r * p) / p;
	}
	
	public static OfflinePlayer getPlayer(String name) {
		if(players.containsKey(name))
			return players.get(name);
		
		return getOfflinePlayer(name);
	}
	
	public static boolean constainsKeyIgnoreCase(Set<String> haystack, String needle) {
		for(String straw : haystack) {
			if(straw.equalsIgnoreCase(needle))
				return true;
		}
		
		return false;
	}
	
	public static Object getIgnoreCase(Map<String, ?> haystack, String needle) {
		for(String straw : haystack.keySet()) {
			if(straw.equalsIgnoreCase(needle))
				return haystack.get(straw);
		}
		
		return null;
	}
	
	public static double getPrice(double cost, int stock, int maxStock, int quantity) {
		config conf = config.Obtain();
		
		if(conf.getBoolean("GiantShop.stock.useStock") && conf.getBoolean("GiantShop.stock.stockDefinesCost") && maxStock != -1 && stock != -1) {
			double maxInfl = conf.getDouble("GiantShop.stock.maxInflation");
			double maxDefl = conf.getDouble("GiantShop.stock.maxDeflation");
			int atmi = conf.getInt("GiantShop.stock.amountTillMaxInflation");
			int atmd = conf.getInt("GiantShop.stock.amountTillMaxDeflation");
			double split = Math.round((atmi + atmd) / 2);
			if(maxStock <= atmi + atmd) {
				split = maxStock / 2;
				atmi = 0;
				atmd = maxStock;
			}
			
			if(stock >= atmd) {
				cost = (cost * (1.0 - maxDefl / 100.0)) * (double) quantity;
			}else if(stock <= atmi) {
				cost = (cost * (1.0 + maxInfl / 100.0)) * (double) quantity;
			}else{
				if(stock < split) {
					cost = (double)Math.round(((cost * (1.0 + (maxInfl / stock) / 100)) * (double) quantity) * 100.0) / 100.0;
				}else if(stock > split) {
					maxDefl = maxDefl * (1.0 - ((100 - ((stock - split) / split * 100)) / 100));
					cost = (double)Math.round((cost * (1.0 - maxDefl / 100.0)) * (double) quantity * 100.0) / 100.0;
				}
			}
		}else{
			cost = cost * quantity;
		}
		
		return cost;
	}
}
