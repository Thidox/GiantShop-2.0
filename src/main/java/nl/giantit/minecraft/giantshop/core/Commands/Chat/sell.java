package nl.giantit.minecraft.giantshop.core.Commands.Chat;

import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.Misc.Messages.msgType;
import nl.giantit.minecraft.giantcore.core.Eco.iEco;
import nl.giantit.minecraft.giantcore.database.query.Group;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;
import nl.giantit.minecraft.giantcore.database.query.UpdateQuery;
import nl.giantit.minecraft.giantcore.perms.Permission;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.API.GiantShopAPI;
import nl.giantit.minecraft.giantshop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.giantshop.API.stock.Events.StockUpdateEvent;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import nl.giantit.minecraft.giantshop.core.config;
import nl.giantit.minecraft.giantshop.core.Items.ItemID;
import nl.giantit.minecraft.giantshop.core.Items.Items;
import nl.giantit.minecraft.giantshop.core.Logger.Logger;
import nl.giantit.minecraft.giantshop.core.Logger.LoggerType;
import nl.giantit.minecraft.giantshop.core.Tools.InventoryHandler;
import nl.giantit.minecraft.giantshop.core.Tools.Discount.Discounter;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Giant
 */
public class sell {
	
	private  static config conf = config.Obtain();
	private static Driver DB = GiantShop.getPlugin().getDB().getEngine();
	private static Permission perms = GiantShop.getPlugin().getPermHandler().getEngine();
	private static Messages mH = GiantShop.getPlugin().getMsgHandler();
	private static Items iH = GiantShop.getPlugin().getItemHandler();
	private static iEco eH = GiantShop.getPlugin().getEcoHandler().getEngine();
	private static Discounter disc = GiantShop.getPlugin().getDiscounter();
	
	public static void sell(Player player, String[] args) {
		if(perms.has(player, "giantshop.shop.sell")) {
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
						data.put("command", "sell");

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
				
				Integer iT = ((itemType == null || itemType == -1 || itemType == 0) ? null : itemType);
				
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
				
				if(iH.isValidItem(itemID, iT)) {
					ArrayList<String> fields = new ArrayList<String>();
					fields.add("perStack");
					fields.add("buyFor");
					fields.add("stock");
					fields.add("maxStock");
					fields.add("shops");

					SelectQuery sQ = DB.select(fields);
					sQ.from("#__items");
					sQ.where("itemID", String.valueOf(itemID), Group.ValueType.EQUALSRAW);
					sQ.where("type", String.valueOf(((itemType == null) ? -1 : itemType)), Group.ValueType.EQUALSRAW);
					
					QueryResult QRes = sQ.exec();
					if(QRes.size() == 1) {
						QueryRow QR = QRes.getRow();
						if(!QR.getString("buyfor").equals("-1.0") && !QR.getString("buyfor").equals("-1")) {
							String name = iH.getItemNameByID(itemID, iT);

							int perStack = QR.getInt("perstack");
							int stock = QR.getInt("stock");
							int maxStock = QR.getInt("maxstock");
							double buyFor = QR.getDouble("buyfor");

							double cost = buyFor * (double) quantity;
							int amount = perStack * quantity;
							
							if(!conf.getBoolean("GiantShop.stock.useStock") || stock == -1 || maxStock == -1 || (stock + amount <= maxStock || conf.getBoolean("GiantShop.stock.allowOverStock"))) {
								cost = Misc.getPrice(buyFor, stock, maxStock, quantity);

								if(conf.getBoolean("GiantShop.discounts.affectsSales")) {
									int discount = disc.getDiscount(iH.getItemIDByName(iH.getItemNameByID(itemID, iT)), player);
									if(discount > 0) {
										double actualDiscount = (100 - discount) / 100D;
										cost = Misc.Round(cost * actualDiscount, 2);
									}
								}
								
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
								
								int stackAmt = InventoryHandler.hasAmount(inv, iStack);
								if(stackAmt >= amount) {
									eH.deposit(player, cost);
									
									HashMap<String, String> data = new HashMap<String, String>();
									data.put("amount", String.valueOf(amount));
									data.put("item", name);
									data.put("cash", String.valueOf(cost));
									data.put("player", player.getDisplayName());
									data.put("balance", String.valueOf(eH.getBalance(player)));

									if(conf.getBoolean("GiantShop.broadcast.sell"))
										Heraut.broadcast(mH.getMsg(msgType.MAIN, "broadcastSell", data));
	
									Heraut.say(player, mH.getMsg(msgType.MAIN, "sell", data));
									Heraut.say(player, mH.getMsg(msgType.MAIN, "newBalance", data));
									
									HashMap<String, String> d = new HashMap<String, String>();
									d.put("id", String.valueOf(itemID));
									d.put("type", String.valueOf((itemType == null || itemType <= 0) ? -1 : itemType));
									d.put("oS", String.valueOf(stock));
									d.put("nS", String.valueOf((stock != -1 ? stock + amount : stock)));
									d.put("amount", String.valueOf(amount));
									d.put("total", String.valueOf(cost));
									Logger.Log(LoggerType.SELL, player.getName(), d);
									
									InventoryHandler.removeItem(inv, iStack);
									
									if(conf.getBoolean("GiantShop.stock.useStock") && stock != -1) {
										UpdateQuery uQ = DB.update("#__items");
										uQ.set("stock", String.valueOf((stock + amount)), UpdateQuery.ValueType.SETRAW);
										uQ.where("itemID", String.valueOf(itemID), Group.ValueType.EQUALSRAW);
										uQ.where("type", String.valueOf(((itemType == null) ? -1 : itemType)), Group.ValueType.EQUALSRAW);

										uQ.exec();
										try {
											StockUpdateEvent event = new StockUpdateEvent(player, GiantShopAPI.Obtain().getStockAPI().getItemStock(itemID, itemType), StockUpdateEvent.StockUpdateType.INCREASE);
											GiantShop.getPlugin().getSrvr().getPluginManager().callEvent(event);
										}catch(ItemNotFoundException e) {
											// Won't ever occur.
										}catch(NullPointerException e) {
											// StockAPI isn't loaded!
										}
										
									}
								}else{
									HashMap<String, String> data = new HashMap<String, String>();
									data.put("needed", String.valueOf(amount));
									data.put("have", String.valueOf(stackAmt));
	
									Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "insufItems", data));
								}
							}else{
								Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "stockExeedsMaxStock"));
							}
						}else{
							Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noReturns"));
						}
					}else{
						Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noneOrMoreResults"));
					}
				}else{
					Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "itemNotFound"));
				}
			}else{
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("command", "sell");

				Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "syntaxError", data));
			}
		}else{
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("command", "sell");

			Heraut.say(player, mH.getMsg(Messages.msgType.ERROR, "noPermissions", data));
		}
	}
	
}
