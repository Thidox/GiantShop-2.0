package nl.giantit.minecraft.GiantShop.Misc;

import java.util.List;
/**
 *
 * @author Giant
 */
public class Misc {
	
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
	
	
}
