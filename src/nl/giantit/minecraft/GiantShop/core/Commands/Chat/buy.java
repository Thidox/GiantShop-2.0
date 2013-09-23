package nl.giantit.minecraft.GiantShop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.core.Eco.iEco;
import nl.giantit.minecraft.giantcore.Database.QueryResult;
import nl.giantit.minecraft.giantcore.Database.QueryResult.QueryRow;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.API.stock.Events.StockUpdateEvent;
import nl.giantit.minecraft.GiantShop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.GiantShop.Misc.Heraut;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.Misc.Messages.msgType;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.core.Logger.Logger;
import nl.giantit.minecraft.GiantShop.core.Logger.LoggerType;
import nl.giantit.minecraft.GiantShop.core.Tools.Discount.Discounter;
import nl.giantit.minecraft.giantcore.Database.iDriver;
import nl.giantit.minecraft.giantcore.perms.Permission;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class buy {
	
	private static config conf = config.Obtain();
	private static iDriver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	private static iEco eH = GiantShop.getPlugin().getEcoHandler().getEngine();
	private static Discounter disc = GiantShop.getPlugin().getDiscounter();
	
	public static void buy(Player player, String[] args) {
		if(perms.has(player, "giantshop.shop.buy")) {
			if(args.length >= 2) {
				int itemID;
				Integer itemType = -1;
				int quantity;

				if(!args[1].matches("[0-9]+:[0-9]+")) {
					try {
						itemID = Integer.parseInt(args[1]);
						itemType = -1;
					}catch(NumberFormatException e) {
						ItemID key = iH.getItemIDByName(args[1]);
						if(key != null) {
							itemID = key.getId();
							itemType = key.getType();
						}else{
							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
							return;
						}
					}catch(Exception e) {
						if(conf.getBoolean("GiantShop.global.debug") == true) {
							GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
							GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
						}

						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "unknown"));
						return;
					}
				}else{
					try {
						String[] data = args[1].split(":");
						itemID = Integer.parseInt(data[0]);
						itemType = Integer.parseInt(data[1]);
					}catch(NumberFormatException e) {
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("command", "buy");

						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
						return;
					}catch(Exception e) {
						if(conf.getBoolean("GiantShop.global.debug") == true) {
							GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
							GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
						}

						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "unknown"));
						return;
					}
				}
				
				if(args.length >= 3) {
					try {
						quantity = Integer.parseInt(args[2]);
						quantity = (quantity > 0) ? quantity : 1;
					}catch(NumberFormatException e) {
						//Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "invQuantity"));
						Heraut.say(player, "As you did not specify a normal quantity, we'll just use 1 ok? :)");
						quantity = 1;
					}
				}else
					quantity = 1;
				
				Integer iT = ((itemType == null || itemType == -1 || itemType == 0) ? null : itemType);
				if(iH.isValidItem(itemID, iT)) {
					ArrayList<String> fields = new ArrayList<String>();
					fields.add("perStack");
					fields.add("sellFor");
					fields.add("stock");
					fields.add("maxStock");
					fields.add("shops");

					HashMap<String, String> where = new HashMap<String, String>();
					where.put("itemID", String.valueOf(itemID));
					where.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));

					QueryResult QRes = DB.select(fields).from("#__items").where(where).execQuery();
					if(QRes.size() == 1) {
						QueryRow QR = QRes.getRow();
						if(!QR.getString("sellfor").equals("-1.0") && !QR.getString("sellfor").equals("-1")) {
							String name = iH.getItemNameByID(itemID, iT);

							int perStack = QR.getInt("perstack");
							int stock = QR.getInt("stock");
							int maxStock = QR.getInt("maxstock");
							double sellFor = QR.getDouble("sellfor");
							double balance = eH.getBalance(player);

							double cost = sellFor * (double) quantity;
							int amount = perStack * quantity;

							if(!conf.getBoolean("GiantShop.stock.useStock") || stock == -1 || (stock - amount) >= 0) {
								cost = Misc.getPrice(sellFor, stock, maxStock, quantity);

								int discount = disc.getDiscount(iH.getItemIDByName(iH.getItemNameByID(itemID, iT)), player);
								if(discount > 0) {
									double actualDiscount = (100 - discount) / 100D;
									cost = Misc.Round(cost * actualDiscount, 2);
								}
								
								if((balance - cost) < 0) {
									HashMap<String, String> data = new HashMap<String, String>();
									data.put("needed", String.valueOf(cost));
									data.put("have", String.valueOf(balance));

									Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "insufFunds", data));
								}else{
									if(eH.withdraw(player, cost) || cost == 0) {
										ItemStack iStack;
										Inventory inv = player.getInventory();

										if(itemType != null && itemType != -1) {
											if(itemID != 373)
												iStack = new MaterialData(itemID, (byte) ((int) itemType)).toItemStack(amount);
											else
												iStack = new ItemStack(itemID, amount, (short) ((int) itemType));
										}else{
											iStack = new ItemStack(itemID, amount);
										}

										HashMap<String, String> data = new HashMap<String, String>();
										data.put("amount", String.valueOf(amount));
										data.put("item", name);
										data.put("cash", String.valueOf(cost));
										data.put("player", player.getDisplayName());
										data.put("balance", String.valueOf(eH.getBalance(player)));

										if(conf.getBoolean("GiantShop.broadcast.buy"))
											Heraut.broadcast(mH.getMsg(msgType.MAIN, "broadcastBuy", data));

										Heraut.say(player, mH.getMsg(msgType.MAIN, "buy", data));
										Heraut.say(player, mH.getMsg(msgType.MAIN, "newBalance", data));
										
										HashMap<String, String> d = new HashMap<String, String>();
										d.put("id", String.valueOf(itemID));
										d.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));
										d.put("oS", String.valueOf(stock));
										d.put("nS", String.valueOf((stock != -1 ? stock - amount : stock)));
										d.put("amount", String.valueOf(amount));
										d.put("total", String.valueOf(cost));
										Logger.Log(LoggerType.BUY, player.getName(), d);

										HashMap<Integer, ItemStack> left;
										left = inv.addItem(iStack);
										
										if(conf.getBoolean("GiantShop.stock.useStock") && stock != -1) {
											HashMap<String, String> t = new HashMap<String, String>();
											t.put("stock", String.valueOf((stock - amount)));

											DB.update("#__items").set(t).where(where).updateQuery();
											
											try {
												StockUpdateEvent event = new StockUpdateEvent(player, GiantShopAPI.Obtain().getStockAPI().getItemStock(itemID, itemType), StockUpdateEvent.StockUpdateType.DECREASE);
												GiantShop.getPlugin().getSrvr().getPluginManager().callEvent(event);
											}catch(ItemNotFoundException e) {
												// Won't ever occur.
											}catch(NullPointerException e) {
												// StockAPI isn't loaded!
											}
										}

										if(!left.isEmpty()) {
											Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "infFull"));
											for(Map.Entry<Integer, ItemStack> stack : left.entrySet()) {
												player.getWorld().dropItem(player.getLocation(), stack.getValue());
											}
										}
									}
								}
							}else{
								HashMap<String, String> data = new HashMap<String, String>();
								data.put("name", String.valueOf(cost));

								Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemOutOfStock", data));
							}

							//More future stuff
							/*if(conf.getBoolean("GiantShop.Location.useGiantShopLocation") == true) {
							 *		ArrayList<Indaface> shops = GiantShop.getPlugin().getLocationHandler().parseShops(res.get("shops"));
							 *		for(Indaface shop : shops) {
							 *			if(shop.inShop(player.getLocation())) {
							 *				//Player can get the item he wants! :D
							 *			}
							 *		}
							 * }else{
							 *		//Just a global store then :)
							 * }
							 */
						}else{
							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "notForSale"));
						}
					}else{
						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noneOrMoreResults"));
					}
				}else{
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "buy");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "buy");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
	public static void gift(Player player, String[] args) {
		if(perms.has(player, "giantshop.shop.gift")) {
			if(args.length >= 3) {
				Player giftReceiver = GiantShop.getPlugin().getServer().getPlayer(args[1]);
				if(giftReceiver == null) {
					Heraut.say(player, "Receiver does not exist!");
				}else if(!giftReceiver.isOnline()) {
					Heraut.say(player, "Gift receiver is not online!");
				}else{
					int itemID;
					Integer itemType = -1;
					int quantity;

					if(!args[2].matches("[0-9]+:[0-9]+")) {
						try {
							itemID = Integer.parseInt(args[2]);
							itemType = -1;
						}catch(NumberFormatException e) {
							ItemID key = iH.getItemIDByName(args[2]);
							if(key != null) {
								itemID = key.getId();
								itemType = key.getType();
							}else{
								Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
								return;
							}
						}catch(Exception e) {
							if(conf.getBoolean("GiantShop.global.debug") == true) {
								GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
								GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
							}

							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "unknown"));
							return;
						}
					}else{
						try {
							String[] data = args[2].split(":");
							itemID = Integer.parseInt(data[0]);
							itemType = Integer.parseInt(data[1]);
						}catch(NumberFormatException e) {
							HashMap<String, String> data = new HashMap<String, String>();
							data.put("command", "gift");

							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
							return;
						}catch(Exception e) {
							if(conf.getBoolean("GiantShop.global.debug") == true) {
								GiantShop.log.log(Level.SEVERE, "GiantShop Error: " + e.getMessage());
								GiantShop.log.log(Level.INFO, "Stacktrace: " + e.getStackTrace());
							}

							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "unknown"));
							return;
						}
					}

					if(args.length >= 4) {
						try {
							quantity = Integer.parseInt(args[3]);
							quantity = (quantity > 0) ? quantity : 1;
						}catch(NumberFormatException e) {
							//Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "invQuantity"));
							Heraut.say(player, "As you did not specify a normal quantity, we'll just use 1 ok? :)");
							quantity = 1;
						}
					}else
						quantity = 1;

					Integer iT = ((itemType == null || itemType == -1 || itemType == 0) ? null : itemType);
					if(iH.isValidItem(itemID, iT)) {
						ArrayList<String> fields = new ArrayList<String>();
						fields.add("perStack");
						fields.add("sellFor");
						fields.add("stock");
						fields.add("maxStock");
						fields.add("shops");

						HashMap<String, String> where = new HashMap<String, String>();
						where.put("itemID", String.valueOf(itemID));
						where.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));

						QueryResult QRes = DB.select(fields).from("#__items").where(where).execQuery();
						if(QRes.size() == 1) {
							QueryRow QR = QRes.getRow();
							if(!QR.getString("sellfor").equals("-1.0") && !QR.getString("sellfor").equals("-1")) {
								String name = iH.getItemNameByID(itemID, iT);

								int perStack = QR.getInt("perstack");
								int stock = QR.getInt("stock");
								int maxStock = QR.getInt("maxstock");
								double sellFor = QR.getDouble("sellfor");
								double balance = eH.getBalance(player);

								double cost = sellFor * (double) quantity;
								int amount = perStack * quantity;

								if(!conf.getBoolean("GiantShop.stock.useStock") || stock == -1 || (stock - amount) >= 0) {
									cost = Misc.getPrice(cost, stock, maxStock, 1);
									
									if((balance - cost) < 0) {
										HashMap<String, String> data = new HashMap<String, String>();
										data.put("needed", String.valueOf(cost));
										data.put("have", String.valueOf(balance));

										Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "insufFunds", data));
									}else{
										if(eH.withdraw(player, cost)) {
											ItemStack iStack;
											Inventory inv = giftReceiver.getInventory();

											if(itemType != null && itemType != -1) {
												iStack = new MaterialData(itemID, (byte) ((int) itemType)).toItemStack(amount);
											}else{
												iStack = new ItemStack(itemID, amount);
											}

											HashMap<String, String> data = new HashMap<String, String>();
											data.put("amount", String.valueOf(amount));
											data.put("item", name);
											data.put("giftReceiver", giftReceiver.getDisplayName());
											data.put("cash", String.valueOf(cost));
											data.put("giftSender", player.getDisplayName());
											data.put("balance", String.valueOf(eH.getBalance(player)));

											if(conf.getBoolean("GiantShop.broadcast.gift"))
												Heraut.broadcast(mH.getMsg(msgType.MAIN, "broadcastGift", data));

											Heraut.say(player, mH.getMsg(Messages.msgType.MAIN, "giftSender", data));
											Heraut.say(player, mH.getMsg(msgType.MAIN, "newBalance", data));

											Heraut.say(giftReceiver, mH.getMsg(Messages.msgType.MAIN, "giftReceiver", data));
											HashMap<String, String> d = new HashMap<String, String>();
											d.put("id", String.valueOf(itemID));
											d.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));
											d.put("oS", String.valueOf(stock));
											d.put("nS", String.valueOf((stock != -1 ? stock - amount : stock)));
											d.put("amount", String.valueOf(amount));
											d.put("total", String.valueOf(cost));
											d.put("receiver", giftReceiver.getName());
											Logger.Log(LoggerType.GIFT, player.getName(), d);

											HashMap<Integer, ItemStack> left;
											left = inv.addItem(iStack);
											
											if(conf.getBoolean("GiantShop.stock.useStock") && stock != -1) {
												HashMap<String, String> t = new HashMap<String, String>();
												t.put("stock", String.valueOf((stock - amount)));

												DB.update("#__items").set(t).where(where).updateQuery();
												
												try {
													StockUpdateEvent event = new StockUpdateEvent(player, GiantShopAPI.Obtain().getStockAPI().getItemStock(itemID, itemType), StockUpdateEvent.StockUpdateType.DECREASE);
													GiantShop.getPlugin().getSrvr().getPluginManager().callEvent(event);
												}catch(ItemNotFoundException e) {
													// Won't ever occur.
												}catch(NullPointerException e) {
													// StockAPI isn't loaded!
												}
											}

											if(!left.isEmpty()) {
												Heraut.say(giftReceiver, mH.getMsg(Messages.msgType.ERROR, "infFull"));
												for(Map.Entry<Integer, ItemStack> stack : left.entrySet()) {
													giftReceiver.getWorld().dropItem(giftReceiver.getLocation(), stack.getValue());
												}
											}
										}
									}
								}else{
									HashMap<String, String> data = new HashMap<String, String>();
									data.put("name", String.valueOf(cost));

									Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemOutOfStock", data));
								}
							}else{
								Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "notForSale"));
							}
						}else{
							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noneOrMoreResults"));
						}
					}else{
						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
					}
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "gift");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "gift");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
}
