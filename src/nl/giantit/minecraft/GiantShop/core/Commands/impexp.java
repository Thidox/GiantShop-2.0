package nl.giantit.minecraft.GiantShop.core.Commands;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.Misc.*;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;

import org.bukkit.command.CommandSender;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class impexp {
	
	private static db DB = db.Obtain();
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
									if(!line.equals("itemID, itemType, sellFor, buyFor, perStack, stock, shops")) {
										Heraut.say(sender, "The given file is not a proper items file!");
										br.close();
										return;
									}
									continue;	
								}

								//break comma separated line using ", "
								String[] st = line.split(", ");
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
					
					values = new ArrayList<HashMap<Integer, HashMap<String, String>>>();
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
							Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + item.toString() + ")");
							continue;
						}
						
						if(iH.isValidItem(itemID, itemType)) {
							HashMap<Integer, HashMap<String, String>> tmp = new HashMap<Integer, HashMap<String, String>>();
							for(String field : fields) {
								HashMap<String, String> temp = new HashMap<String, String>();
								if(field.equalsIgnoreCase("itemID")) {
									temp.put("kind", "INT");
									temp.put("data", "" + itemID);
									tmp.put(0, temp);
								}else if(field.equalsIgnoreCase("type")) {
									temp.put("kind", "INT");
									temp.put("data", "" + ((itemType == null) ? -1 : itemType));
									tmp.put(1, temp);
								}else if(field.equalsIgnoreCase("sellFor")) {
									temp.put("data", "" + sellFor);
									tmp.put(2, temp);
								}else if(field.equalsIgnoreCase("buyFor")) {
									temp.put("data", "" + buyFor);
									tmp.put(3, temp);
								}else if(field.equalsIgnoreCase("stock")) {
									temp.put("kind", "INT");
									temp.put("data", "" + stock);
									tmp.put(4, temp);
								}else if(field.equalsIgnoreCase("perStack")) {
									temp.put("kind", "INT");
									temp.put("data", "" + perStack);
									tmp.put(5, temp);
								}else if(field.equalsIgnoreCase("shops")) {
									if(item.length == 7)
										temp.put("data", (item[6].equals("null") ? "" : item[6]));
									else
										temp.put("data", "");
									
									tmp.put(6, temp);
								}
							}
							values.add(tmp);
						}else{
							err = true;
							Heraut.say(sender, "Invalid entry detected! (" + lineNumber + ":" + item.toString() + ")");
							continue;
						}
					}
					
					if(!commence) {
						Heraut.say(sender, "Found " + values.size() + " items to import!");
					}else{
						Heraut.say(sender, "Truncating items table!");
						DB.Truncate("#__items").updateQuery();
						
						Heraut.say(sender, "Importing " + values.size() + " items...");
						DB.insert("#__items", fields, values).updateQuery();
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
					
					
					fields = new ArrayList<String>();
					
					values = new ArrayList<HashMap<Integer, HashMap<String, String>>>();
				}else{
					Heraut.say(sender, "Requested file does not exist!");
				}
			}else if(Misc.isEitherIgnoreCase(type, "discounts", "d")) {
				file = (file == null) ? "discounts.csv" : file;
				
				File f = new File(path + File.separator + file);
				if(f.exists()) {
					
					
					fields = new ArrayList<String>();
					
					values = new ArrayList<HashMap<Integer, HashMap<String, String>>>();
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
							String shops = (!data.get("shops").isEmpty()) ? data.get("shops") : "null";

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
