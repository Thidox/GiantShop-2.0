package nl.giantit.minecraft.GiantShop.core.Commands;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.*;
import nl.giantit.minecraft.GiantShop.core.Database.db;

import org.bukkit.command.CommandSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class impexp {
	
	private static db DB = db.Obtain();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	
	public static void imp(CommandSender sender, String[] args) {
		if(args.length > 1) {
			String type = "items";
			String path = GiantShop.getPlugin().getDir() + File.separator + "csvs";
			String file = null;
			Boolean commence = true;
			for(int i = 0; i < args.length; i++) {
				if(args[i].startsWith("-t:")) {
					type = args[i].replaceFirst("-t:", "");
					continue;
				}else if(args[i].startsWith("-p:")) {
					path = args[i].replaceFirst("-p:", "");
					continue;
				}else if(args[i].startsWith("-c:")) {
					commence = Boolean.parseBoolean(args[i].replaceFirst("-c:", ""));
					continue;
				}else if(args[i].startsWith("-f:")) {
					file = args[i].replaceFirst("-f:", "");
					continue;
				}
			}
			
			if(Misc.isEitherIgnoreCase(type, "items", "i")) {
				file = (file == null) ? "items.csv" : file;
				
				
			}else if(Misc.isEitherIgnoreCase(type, "shops", "s")) {
				file = (file == null) ? "shops.csv" : file;
				
				
			}else if(Misc.isEitherIgnoreCase(type, "discounts", "d")) {
				file = (file == null) ? "discounts.csv" : file;
				
				
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "import");

				Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "import");

			Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
		}
	}
	
	public static void impLegacy(CommandSender sender, String[] args) {
		
	}
	
	public static void exp(CommandSender sender, String[] args) {
		File dir = new File(GiantShop.getPlugin().getDir() + File.separator + "csvs");
		if(!dir.exists()) {
			dir.mkdir();
		}else{		
			if(!dir.isDirectory()) {
				Heraut.say(sender, "Output directory is not a directory!");
			}
		}
		
		if(args.length > 1) {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("*");
			if(Misc.isEitherIgnoreCase(args[1], "items", "i")) {
				DB.select(fields).from("#__items");
				ArrayList<HashMap<String, String>> iResSet = DB.execQuery();
				Heraut.say(sender, "Found " + iResSet.size() + " items to export!");
				
				if(iResSet.size() > 0) {
					Heraut.say(sender, "Starting item export...");

					try{
						BufferedWriter f = new BufferedWriter(new FileWriter(GiantShop.getPlugin().getDir() + File.separator + "csvs" + File.separator + "items.csv"));
						f.write("itemID, itemType, sellFor, buyFor, perStack, stock, shops");
						f.newLine();
						for(int i = 0; i < iResSet.size(); i++) {
							HashMap<String, String> data = iResSet.get(i);

							String itemId = data.get("itemID");
							String dataType = data.get("itemType");
							String sellFor = data.get("sellFor");
							String buyFor = data.get("buyFor");
							String amount = data.get("perStack");
							String stock = data.get("stock");
							String shops = data.get("shops");

							f.write(itemId + ", " + dataType + ", " + sellFor + ", " + buyFor + ", " + amount + ", " + stock + ", " + shops);
							f.newLine();
						}
						f.flush();
						f.close();
					}catch(IOException e) {
						Heraut.say(sender, "Failed item export!");
					}finally{
						Heraut.say(sender, "Finished item export!");
					}
				}
			}else if(Misc.isEitherIgnoreCase(args[1], "shops", "s") || args[1].equalsIgnoreCase("x")) {
				DB.select(fields).from("#__shops");
				ArrayList<HashMap<String, String>> sResSet = DB.execQuery();
				Heraut.say(sender, "Found " + sResSet.size() + " shops to export!");

				if(sResSet.size() > 0) {
					Heraut.say(sender, "Starting shops export...");

					try{
						BufferedWriter f = new BufferedWriter(new FileWriter(GiantShop.getPlugin().getDir() + File.separator + "csvs" + File.separator + "shops.csv"));
						f.write("name, perms, world, locMinX, locMinY, locMinZ, locMaxX, locMaxY, locMaxZ");
						f.newLine();
						for(int i = 0; i < sResSet.size(); i++) {
							HashMap<String, String> data = sResSet.get(i);

							String name = data.get("name");
							String perms = data.get("perms");
							String world = data.get("world");
							String locMinX = data.get("locMinX");
							String locMinY = data.get("locMinY");
							String locMinZ = data.get("locMinZ");
							String locMaxX = data.get("locMaxX");
							String locMaxY = data.get("locMaxY");
							String locMaxZ = data.get("locMaxZ");

							f.write(name + ", " + perms + ", " + world + ", " + locMinX + ", " + locMinY + ", " + locMinZ + ", " + locMaxX + ", " + locMaxY + ", " + locMaxZ);
							f.newLine();
						}
						f.flush();
						f.close();
					}catch(IOException e) {
						Heraut.say(sender, "Failed shops export!");
					}finally{
						Heraut.say(sender, "Finished shops export!");
					}
				}
			}else if(Misc.isEitherIgnoreCase(args[1], "discounts", "d")) {
				DB.select(fields).from("#__discounts");
				ArrayList<HashMap<String, String>> dResSet = DB.execQuery();
				Heraut.say(sender, "Found " + dResSet.size() + " discounts to export!");

				if(dResSet.size() > 0) {
					Heraut.say(sender, "Starting discounts export...");

					try{
						BufferedWriter f = new BufferedWriter(new FileWriter(GiantShop.getPlugin().getDir() + File.separator + "csvs" + File.separator + "discounts.csv"));
						f.write("itemID, dicount, user, group");
						f.newLine();
						for(int i = 0; i < dResSet.size(); i++) {
							HashMap<String, String> data = dResSet.get(i);

							String itemID = data.get("itemID");
							String dicount = data.get("dicount");
							String user = data.get("user");
							String group = data.get("world");

							f.write(itemID + ", " + dicount + ", " + user + ", " + group);
							f.newLine();
						}
						f.flush();
						f.close();
					}catch(IOException e) {
						Heraut.say(sender, "Failed discounts export!");
					}finally{
						Heraut.say(sender, "Finished discounts export!");
					}
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "export");

				Heraut.say(sender, mH.getConsoleMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			Heraut.say(sender, "[GiantShop] Starting export...");
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("*");

			DB.select(fields).from("#__items");
			ArrayList<HashMap<String, String>> iResSet = DB.execQuery();
			Heraut.say(sender, "Found " + iResSet.size() + " items to export!");

			DB.select(fields).from("#__shops");
			ArrayList<HashMap<String, String>> sResSet = DB.execQuery();
			Heraut.say(sender, "Found " + sResSet.size() + " shops to export!");

			DB.select(fields).from("#__discounts");
			ArrayList<HashMap<String, String>> dResSet = DB.execQuery();
			Heraut.say(sender, "Found " + dResSet.size() + " discounts to export!");

			if(iResSet.size() > 0) {
				Heraut.say(sender, "Starting item export...");

				try{
					BufferedWriter f = new BufferedWriter(new FileWriter(GiantShop.getPlugin().getDir() + File.separator + "csvs" + File.separator + "items.csv"));
					f.write("itemID, itemType, sellFor, buyFor, perStack, stock, shops");
					f.newLine();
					for(int i = 0; i < iResSet.size(); i++) {
						HashMap<String, String> data = iResSet.get(i);

						String itemId = data.get("itemID");
						String dataType = data.get("itemType");
						String sellFor = data.get("sellFor");
						String buyFor = data.get("buyFor");
						String amount = data.get("perStack");
						String stock = data.get("stock");
						String shops = data.get("shops");

						f.write(itemId + ", " + dataType + ", " + sellFor + ", " + buyFor + ", " + amount + ", " + stock + ", " + shops);
						f.newLine();
					}
					f.flush();
					f.close();
				}catch(IOException e) {
					Heraut.say(sender, "Failed item export!");
				}finally{
					Heraut.say(sender, "Finished item export!");
				}
			}

			if(sResSet.size() > 0) {
				Heraut.say(sender, "Starting shops export...");

				try{
					BufferedWriter f = new BufferedWriter(new FileWriter(GiantShop.getPlugin().getDir() + File.separator + "csvs" + File.separator + "shops.csv"));
					f.write("name, perms, world, locMinX, locMinY, locMinZ, locMaxX, locMaxY, locMaxZ");
					f.newLine();
					for(int i = 0; i < sResSet.size(); i++) {
						HashMap<String, String> data = sResSet.get(i);

						String name = data.get("name");
						String perms = data.get("perms");
						String world = data.get("world");
						String locMinX = data.get("locMinX");
						String locMinY = data.get("locMinY");
						String locMinZ = data.get("locMinZ");
						String locMaxX = data.get("locMaxX");
						String locMaxY = data.get("locMaxY");
						String locMaxZ = data.get("locMaxZ");

						f.write(name + ", " + perms + ", " + world + ", " + locMinX + ", " + locMinY + ", " + locMinZ + ", " + locMaxX + ", " + locMaxY + ", " + locMaxZ);
						f.newLine();
					}
					f.flush();
					f.close();
				}catch(IOException e) {
					Heraut.say(sender, "Failed shops export!");
				}finally{
					Heraut.say(sender, "Finished shops export!");
				}
			}

			if(dResSet.size() > 0) {
				Heraut.say(sender, "Starting discounts export...");

				try{
					BufferedWriter f = new BufferedWriter(new FileWriter(GiantShop.getPlugin().getDir() + File.separator + "csvs" + File.separator + "discounts.csv"));
					f.write("itemID, dicount, user, group");
					f.newLine();
					for(int i = 0; i < dResSet.size(); i++) {
						HashMap<String, String> data = dResSet.get(i);

						String itemID = data.get("itemID");
						String dicount = data.get("dicount");
						String user = data.get("user");
						String group = data.get("world");

						f.write(itemID + ", " + dicount + ", " + user + ", " + group);
						f.newLine();
					}
					f.flush();
					f.close();
				}catch(IOException e) {
					Heraut.say(sender, "Failed discounts export!");
				}finally{
					Heraut.say(sender, "Finished discounts export!");
				}
			}

			Heraut.say(sender, "[GiantShop] Finished export!");
		}
	}
}
