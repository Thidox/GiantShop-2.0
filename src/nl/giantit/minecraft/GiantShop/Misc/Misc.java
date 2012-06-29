package nl.giantit.minecraft.GiantShop.Misc;

import nl.giantit.minecraft.GiantShop.GiantShop;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.List;
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
}
