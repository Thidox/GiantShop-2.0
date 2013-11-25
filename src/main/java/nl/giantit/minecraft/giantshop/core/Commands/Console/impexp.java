package nl.giantit.minecraft.giantshop.core.Commands.Console;

import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.database.query.InsertQuery;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.Items.Items;

import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class impexp {
	
	private static Driver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	
	public static void imp(CommandSender sender, String[] args) {
		if(args.length > 1) {
			ArrayList<String> fields;
			ArrayList<HashMap<Integer, HashMap<String, String>>> values;
			String type = "items";
			String path = GiantShop.getPlugin().getDir() + File.separator + "csvs";
			String file = null;
			Boolean commence = true;
			Boolean err = false;
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
				
				File f = new File(path + File.separator + file);
				if(f.exists()) {
					Heraut.say(sender, "Starting importing items...");
					
					ArrayList<String[]> items = new ArrayList<String[]>();
					String line;
					try {
						BufferedReader br = new BufferedReader(new FileReader(path + File.separator + file));
						if(br.ready()) {
							int	lineNumber = 0;

							while((line = br.readLine()) != null) {
								lineNumber++;
								
								if(lineNumber <= 1) {
									if(!line.equals("itemID,itemType,sellFor,buyFor,perStack,stock,shops") 
											&& !line.equals("itemID, itemType, sellFor, buyFor, perStack, stock, shops")) {
										Heraut.say(sender, "The given file is not a proper items file!");
										br.close();
										return;
									}
									continue;	
								}

								//break comma separated line using ", "
								String[] st = line.replaceAll(" ", "").split(",");
								if(st.length >= 6) {
									items.add(st);
								}else{
									err = true;
									Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + line + ")");
									continue;
								}
							}
						}
						br.close();
					}catch(IOException e) {
						Heraut.say(sender, "Failed items import!");
						GiantShop.getPlugin().getLogger().log(Level.SEVERE, "" + e);
						return;
					}
					
					fields = new ArrayList<String>();
					fields.add("itemID");
					fields.add("type");
					fields.add("sellFor");
					fields.add("buyFor");
					fields.add("stock");
					fields.add("perStack");
					fields.add("shops");
					
					InsertQuery iQ = DB.insert("#__items");
					iQ.addFields(fields);
					int lineNumber = 0;
					for(String[] item : items) {
						lineNumber++;
						
						int itemID, stock, perStack;
						Integer itemType;
						Double sellFor, buyFor;
						try {
							itemID = Integer.parseInt(item[0]);
							if(!item[1].equals("null")) {
								itemType = Integer.parseInt(item[1]);
							}else{
								itemType = null;
							}
							
							sellFor = Double.parseDouble(item[2]);
							buyFor = Double.parseDouble(item[3]);
							perStack = Integer.parseInt(item[4]);
							stock = Integer.parseInt(item[5]);
						}catch(NumberFormatException e) {
							err = true;
							Heraut.say(sender, "Invalid entry detected! (line " + lineNumber + ": " + Misc.join(", ", item) + ")");
							continue;
						}
						
						if(iH.isValidItem(itemID, itemType)) {
							iQ.addRow();
							iQ.assignValue("itemID", String.valueOf(itemID), InsertQuery.ValueType.RAW);
							iQ.assignValue("type", String.valueOf(((itemType == null) ? -1 : itemType)), InsertQuery.ValueType.RAW);
							iQ.assignValue("sellFor", String.valueOf(sellFor), InsertQuery.ValueType.RAW);
							iQ.assignValue("buyFor", String.valueOf(buyFor), InsertQuery.ValueType.RAW);
							iQ.assignValue("stock", String.valueOf(stock), InsertQuery.ValueType.RAW);
							iQ.assignValue("perStack", String.valueOf(perStack), InsertQuery.ValueType.RAW);
							
							if(item.length == 7) {
								iQ.assignValue("shops", String.valueOf((item[6].equals("null") ? "" : item[6])));
							}else{
								iQ.assignValue("shops", "");
							}
						}else{
							err = true;
							Heraut.say(sender, "Invalid entry detected! (line " + lineNumber + ": " + Misc.join(", ", item) + ")");
							continue;
						}
					}
					
					if(!commence) {
						Heraut.say(sender, "Found " + lineNumber + " items to import!");
					}else{
						Heraut.say(sender, "Truncating items table!");
						DB.Truncate("#__items").exec();
						
						Heraut.say(sender, "Importing " + lineNumber + " items...");
						iQ.exec();
					}
					
					if(err) {
						Heraut.say(sender, "Finished importing items, though some errors occured!");
					}else{
						Heraut.say(sender, "Finished importing items!");
					}
				}else{
					Heraut.say(sender, "Requested file does not exist!");
				}
			}else if(Misc.isEitherIgnoreCase(type, "shops", "s")) {
				file = (file == null) ? "shops.csv" : file;
				
				File f = new File(path + File.separator + file);
				if(f.exists()) {
					Heraut.say(sender, "Starting importing shops...");
					
					ArrayList<String[]> items = new ArrayList<String[]>();
					String line;
					try {
						BufferedReader br = new BufferedReader(new FileReader(path + File.separator + file));
						if(br.ready()) {
							int	lineNumber = 0;

							while((line = br.readLine()) != null) {
								lineNumber++;
								
								if(lineNumber <= 1) {
									if(!line.equals("name, perms, world, locMinX, locMinY, locMinZ, locMaxX, locMaxY, locMaxZ")) {
										Heraut.say(sender, "The given file is not a proper shops file!");
										br.close();
										return;
									}
									continue;	
								}

								//break comma separated line using ", "
								String[] st = line.split(", ");
								if(st.length == 9) {
									items.add(st);
								}else{
									err = true;
									Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + line + ")");
									continue;
								}
							}
						}
						br.close();
					}catch(IOException e) {
						Heraut.say(sender, "Failed shops import!");
						GiantShop.getPlugin().getLogger().log(Level.SEVERE, "" + e);
						return;
					}
					
					fields = new ArrayList<String>();
					fields.add("name");
					//fields.add("perms");
					fields.add("world");
					fields.add("locMinX");
					fields.add("locMinY");
					fields.add("locMinZ");
					fields.add("locMaxX");
					fields.add("locMaxY");
					fields.add("locMaxZ");
					
					InsertQuery iQ = DB.insert("#__shops");
					iQ.addFields(fields);
					int i = 0;
					for(String[] item : items) {
						++i;
						iQ.addRow();
						iQ.assignValue("name", item[0]);
						iQ.assignValue("world", item[2]);
						iQ.assignValue("locMinX", item[3], InsertQuery.ValueType.RAW);
						iQ.assignValue("locMinY", item[4], InsertQuery.ValueType.RAW);
						iQ.assignValue("locMinZ", item[5], InsertQuery.ValueType.RAW);
						iQ.assignValue("locMaxX", item[6], InsertQuery.ValueType.RAW);
						iQ.assignValue("locMaxY", item[7], InsertQuery.ValueType.RAW);
						iQ.assignValue("locMaxZ", item[8], InsertQuery.ValueType.RAW);
					}
					
					if(!commence) {
						Heraut.say(sender, "Found " + i + " shops to import!");
					}else{
						Heraut.say(sender, "Truncating shops table!");
						DB.Truncate("#__shops").exec();
						
						Heraut.say(sender, "Importing " + i + " shops...");
						iQ.exec();
					}
					
					GiantShop.getPlugin().getLocHandler().reload();
					
					if(err) {
						Heraut.say(sender, "Finished importing shops, though some errors occured!");
					}else{
						Heraut.say(sender, "Finished importing shops!");
					}
				}else{
					Heraut.say(sender, "Requested file does not exist!");
				}
			}else if(Misc.isEitherIgnoreCase(type, "discounts", "d")) {
				file = (file == null) ? "discounts.csv" : file;
				
				File f = new File(path + File.separator + file);
				if(f.exists()) {
					Heraut.say(sender, "Starting importing discounts...");
					
					ArrayList<String[]> items = new ArrayList<String[]>();
					String line;
					try {
						BufferedReader br = new BufferedReader(new FileReader(path + File.separator + file));
						if(br.ready()) {
							int	lineNumber = 0;

							while((line = br.readLine()) != null) {
								lineNumber++;
								
								if(lineNumber <= 1) {
									if(!line.equals("itemID, type, discount, user, group")) {
										Heraut.say(sender, "The given file is not a proper discounts file!");
										br.close();
										return;
									}
									continue;	
								}

								//break comma separated line using ", "
								String[] st = line.split(", ");
								if(st.length == 5) {
									items.add(st);
								}else{
									err = true;
									Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + line + ")");
									continue;
								}
							}
						}
						br.close();
					}catch(IOException e) {
						Heraut.say(sender, "Failed discounts import!");
						GiantShop.getPlugin().getLogger().log(Level.SEVERE, "" + e);
						return;
					}
					
					fields = new ArrayList<String>();
					fields.add("itemID");
					fields.add("type");
					fields.add("discount");
					fields.add("user");
					fields.add("grp");
					
					InsertQuery iQ = DB.insert("#__discounts");
					iQ.addFields(fields);
					values = new ArrayList<HashMap<Integer, HashMap<String, String>>>();
					int lineNumber;
					for(lineNumber = 0; lineNumber < items.size(); lineNumber++) {
						String[] item = items.get(lineNumber);
						iQ.addRow();
						iQ.assignValue("itemID", item[0], InsertQuery.ValueType.RAW);
						iQ.assignValue("type", item[1], InsertQuery.ValueType.RAW);
						iQ.assignValue("discount", item[2], InsertQuery.ValueType.RAW);
						iQ.assignValue("user", item[3], InsertQuery.ValueType.RAW);
						iQ.assignValue("grp", item[4], InsertQuery.ValueType.RAW);
					}
					
					if(!commence) {
						Heraut.say(sender, "Found " + lineNumber + " discounts to import!");
					}else{
						Heraut.say(sender, "Truncating discounts table!");
						DB.Truncate("#__discounts").exec();
						
						Heraut.say(sender, "Importing " + lineNumber + " discounts...");
						iQ.exec();
					}
					
					if(err) {
						Heraut.say(sender, "Finished importing discounts, though some errors occured!");
					}else{
						Heraut.say(sender, "Finished importing discounts!");
					}
				}else{
					Heraut.say(sender, "Requested file does not exist!");
				}
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
		ArrayList<String> fields;
		ArrayList<HashMap<Integer, HashMap<String, String>>> values;
		String path = GiantShop.getPlugin().getDir() + File.separator + "csvs";
		String file = "data.csv";
		Boolean commence = true;
		Boolean err = false;
		if(args.length > 1) {
			for(int i = 0; i < args.length; i++) {
				if(args[i].startsWith("-p:")) {
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
		}
		
		Heraut.say(sender, "Beginning legacy import...");
		File f = new File(path + File.separator + file);
		if(f.exists()) {
			ArrayList<String[]> items = new ArrayList<String[]>();
			String line;
			try {
				BufferedReader br = new BufferedReader(new FileReader(path + File.separator + file));
				if(br.ready()) {
					int	lineNumber = 0;

					while((line = br.readLine()) != null) {
						lineNumber++;

						if(lineNumber <= 1) {
							if(!line.equals("id, dataType, sellFor, buyFor, amount")) {
								Heraut.say(sender, "The given file is not a proper items file!");
								br.close();
								return;
							}
							continue;	
						}

						//break comma separated line using ", "
						String[] st = line.split(", ");
						if(st.length == 5) {
							items.add(st);
						}else{
							err = true;
							Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + line + ")");
							continue;
						}
					}
				}
				br.close();
			}catch(IOException e) {
				Heraut.say(sender, "Legacy import failed!");
				GiantShop.getPlugin().getLogger().log(Level.SEVERE, "" + e);
				return;
			}
			
			fields = new ArrayList<String>();
			fields.add("itemID");
			fields.add("type");
			fields.add("sellFor");
			fields.add("buyFor");
			fields.add("perStack");

			
			InsertQuery iQ = DB.insert("#__items");
			iQ.addFields(fields);
			int lineNumber = 0;
			for(String[] item : items) {
				lineNumber++;

				int itemID, perStack;
				Integer itemType;
				Double sellFor, buyFor;
				try {
					itemID = Integer.parseInt(item[0]);
					if(!item[1].equals("-1") && !item[1].equals("0")) {
						itemType = Integer.parseInt(item[1]);
					}else{
						itemType = null;
					}

					sellFor = Double.parseDouble(item[2]);
					buyFor = Double.parseDouble(item[3]);
					perStack = Integer.parseInt(item[4]);
				}catch(NumberFormatException e) {
					err = true;
					Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + item.toString() + ")");
					continue;
				}

				if(iH.isValidItem(itemID, itemType)) {
					iQ.addRow();
					iQ.assignValue("itemID", String.valueOf(itemID), InsertQuery.ValueType.RAW);
					iQ.assignValue("type", String.valueOf(((itemType == null) ? -1 : itemType)), InsertQuery.ValueType.RAW);
					iQ.assignValue("sellFor", String.valueOf(sellFor), InsertQuery.ValueType.RAW);
					iQ.assignValue("buyFor", String.valueOf(buyFor), InsertQuery.ValueType.RAW);
					iQ.assignValue("perStack", String.valueOf(perStack), InsertQuery.ValueType.RAW);
				}else{
					err = true;
					Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + item.toString() + ")");
					continue;
				}
			}

			if(!commence) {
				Heraut.say(sender, "Found " + lineNumber + " items to import!");
			}else{
				Heraut.say(sender, "Truncating items table!");
				DB.Truncate("#__items").exec();

				Heraut.say(sender, "Importing " + lineNumber + " items...");
				iQ.exec();
			}

			if(err) {
				Heraut.say(sender, "Finished legacy import, though some errors occured!");
			}else{
				Heraut.say(sender, "Finished legacy import!");
			}
		}else{
			Heraut.say(sender, "Legacy import failed! File (" + path + File.separator + file + ") not found!");
		}
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
				QueryResult QRes = DB.select(fields).from("#__items").exec();
				ArrayList<HashMap<String, String>> iResSet = QRes.getRawData();
				Heraut.say(sender, "Found " + iResSet.size() + " items to export!");
				
				if(iResSet.size() > 0) {
					Heraut.say(sender, "Starting item export...");
					if(!impexp.expItem(iResSet, GiantShop.getPlugin().getDir() + File.separator + "csvs", "items.csv")){
						Heraut.say(sender, "Failed item export!");
					}else{
						Heraut.say(sender, "Finished item export!");
					}
				}
			}else if(Misc.isEitherIgnoreCase(args[1], "shops", "s") || args[1].equalsIgnoreCase("x")) {
				QueryResult QRes = DB.select(fields).from("#__shops").exec();
				ArrayList<HashMap<String, String>> sResSet = QRes.getRawData();
				Heraut.say(sender, "Found " + sResSet.size() + " shops to export!");

				if(sResSet.size() > 0) {
					Heraut.say(sender, "Starting shops export...");
					if(!impexp.expShop(sResSet, GiantShop.getPlugin().getDir() + File.separator + "csvs", "shops.csv")){
						Heraut.say(sender, "Failed shops export!");
					}else{
						Heraut.say(sender, "Finished shops export!");
					}
				}
			}else if(Misc.isEitherIgnoreCase(args[1], "discounts", "d")) {
				QueryResult QRes = DB.select(fields).from("#__discounts").exec();
				ArrayList<HashMap<String, String>> dResSet = QRes.getRawData();
				Heraut.say(sender, "Found " + dResSet.size() + " discounts to export!");

				if(dResSet.size() > 0) {
					Heraut.say(sender, "Starting discounts export...");
					if(!impexp.expDiscount(dResSet, GiantShop.getPlugin().getDir() + File.separator + "csvs", "discounts.csv")){
						Heraut.say(sender, "Failed discounts export!");
					}else{
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

			QueryResult iResSet = DB.select(fields).from("#__items").exec();
			Heraut.say(sender, "Found " + iResSet.size() + " items to export!");

			QueryResult sResSet = DB.select(fields).from("#__shops").exec();
			Heraut.say(sender, "Found " + sResSet.size() + " shops to export!");

			QueryResult dResSet = DB.select(fields).from("#__discounts").exec();
			Heraut.say(sender, "Found " + dResSet.size() + " discounts to export!");
			
			ArrayList<HashMap<String, String>> iRS = iResSet.getRawData();
			ArrayList<HashMap<String, String>> sRS = sResSet.getRawData();
			ArrayList<HashMap<String, String>> dRS = dResSet.getRawData();

			if(iResSet.size() > 0) {
				Heraut.say(sender, "Starting item export...");
				if(!impexp.expItem(iRS, GiantShop.getPlugin().getDir() + File.separator + "csvs", "items.csv")){
					Heraut.say(sender, "Failed item export!");
				}else{
					Heraut.say(sender, "Finished item export!");
				}
			}

			if(sResSet.size() > 0) {
				Heraut.say(sender, "Starting shops export...");
				if(!impexp.expShop(sRS, GiantShop.getPlugin().getDir() + File.separator + "csvs", "shops.csv")){
					Heraut.say(sender, "Failed shops export!");
				}else{
					Heraut.say(sender, "Finished shops export!");
				}
			}

			if(dResSet.size() > 0) {
				Heraut.say(sender, "Starting discounts export...");
				if(!impexp.expDiscount(dRS, GiantShop.getPlugin().getDir() + File.separator + "csvs", "discounts.csv")){
					Heraut.say(sender, "Failed discounts export!");
				}else{
					Heraut.say(sender, "Finished discounts export!");
				}
			}

			Heraut.say(sender, "[GiantShop] Finished export!");
		}
	}
	
	private static boolean expItem(ArrayList<HashMap<String, String>> iResSet, String dir, String file) {
		try{
			BufferedWriter f = new BufferedWriter(new FileWriter(dir + File.separator + file));
			f.write("itemID,itemType,sellFor,buyFor,perStack,stock,shops");
			f.newLine();
			for(int i = 0; i < iResSet.size(); i++) {
				HashMap<String, String> data = iResSet.get(i);

				String itemId = data.get("itemid");
				String dataType = (!data.get("type").equals("-1")) ? data.get("type") : "null";
				String sellFor = data.get("sellfor");
				String buyFor = data.get("buyfor");
				String amount = data.get("perstack");
				String stock = data.get("stock");
				String shops = (!data.get("shops").isEmpty()) ? data.get("shops") : "null";

				f.write(itemId + "," + dataType + "," + sellFor + "," + buyFor + "," + amount + "," + stock + "," + shops);
				f.newLine();
			}
			f.flush();
			f.close();
		}catch(IOException e) {
			return false;
		}
		
		return true;
	}
	
	private static boolean expShop(ArrayList<HashMap<String, String>> sResSet, String dir, String file) {
		try{
			BufferedWriter f = new BufferedWriter(new FileWriter(dir + File.separator + file));
			f.write("name, perms, world, locMinX, locMinY, locMinZ, locMaxX, locMaxY, locMaxZ");
			f.newLine();
			for(int i = 0; i < sResSet.size(); i++) {
				HashMap<String, String> data = sResSet.get(i);

				String name = data.get("name");
				String perms = data.get("perms");
				String world = data.get("world");
				String locMinX = data.get("locminx");
				String locMinY = data.get("locminy");
				String locMinZ = data.get("locminz");
				String locMaxX = data.get("locmaxx");
				String locMaxY = data.get("locmaxy");
				String locMaxZ = data.get("locmaxz");

				f.write(name + ", " + perms + ", " + world + ", " + locMinX + ", " + locMinY + ", " + locMinZ + ", " + locMaxX + ", " + locMaxY + ", " + locMaxZ);
				f.newLine();
			}
			f.flush();
			f.close();
		}catch(IOException e) {
			return false;
		}
			
		return true;
	}
	
	private static boolean expDiscount(ArrayList<HashMap<String, String>> dResSet, String dir, String file) {
		try{
			BufferedWriter f = new BufferedWriter(new FileWriter(dir + File.separator + file));
			f.write("itemID, type, discount, user, group");
			f.newLine();
			for(int i = 0; i < dResSet.size(); i++) {
				HashMap<String, String> data = dResSet.get(i);

				String itemID = data.get("itemid");
				String type = data.get("type");
				String dicount = data.get("discount");
				String user = data.get("user");
				String group = data.get("grp");

				f.write(itemID + ", " + type + ", " + dicount + ", " + user + ", " + group);
				f.newLine();
			}
			f.flush();
			f.close();
		}catch(IOException e) {
			return false;
		}
		
		return true;
	}
}
