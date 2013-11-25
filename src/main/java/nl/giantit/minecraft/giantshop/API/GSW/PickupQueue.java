package nl.giantit.minecraft.giantshop.API.GSW;

import nl.giantit.minecraft.giantcore.Misc.Heraut;
import nl.giantit.minecraft.giantcore.Misc.Messages;
import nl.giantit.minecraft.giantcore.database.Driver;
import nl.giantit.minecraft.giantcore.database.QueryResult;
import nl.giantit.minecraft.giantcore.database.QueryResult.QueryRow;
import nl.giantit.minecraft.giantcore.database.query.DeleteQuery;
import nl.giantit.minecraft.giantcore.database.query.InsertQuery;
import nl.giantit.minecraft.giantcore.database.query.SelectQuery;

import nl.giantit.minecraft.giantshop.API.conf;
import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.Logger.Logger;
import nl.giantit.minecraft.giantshop.core.Logger.LoggerType;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Giant
 */
public class PickupQueue {
	
	private GiantShop p;
	private Messages mH;
	
	private HashMap<String, ArrayList<Queued>> queue;
	
	private void loadQueue() {
		Driver db = p.getDB().getEngine();
		this.queue = new HashMap<String, ArrayList<Queued>>();
		
		SelectQuery sQ = db.select("player", "transactionID", "itemID", "itemType", "amount");
		sQ.from("#__api_gsw_pickups");
		sQ.orderBy("player", SelectQuery.Order.ASC);
		sQ.orderBy("transactionID", SelectQuery.Order.ASC);
		QueryResult QRes = sQ.exec();
		//ArrayList<HashMap<String, String>> resSet = db.execQuery();
		String lP = "";
		ArrayList<Queued> qList = new ArrayList<Queued>();
		QueryRow QR;
		while(null != (QR = QRes.getRow())) {
			if(!QR.getString("player").equals(lP)) {
				if(!lP.isEmpty()) {
					this.queue.put(lP, qList);
				}
				
				lP = QR.getString("player");
				qList = new ArrayList<Queued>();
			}
			
			int id;
			int type;
			int amount;
			try {
				id = QR.getInt("itemid");
				type = QR.getInt("itemtype");
				amount = QR.getInt("amount");
				Queued q = new Queued(id, type, amount, QR.getString("transactionid"));
				qList.add(q);
				//p.getLogger().severe("Player: " + lP + "; Queued: " + q.toString());
			}catch(NumberFormatException e) {
				p.getLogger().warning("[GSWAPI][PickupQueue] Transaction " + QR.getString("transactionid") + " for player " + lP + " is corrupt!");
				continue;
			}
		}
		
		// Last player's data never gets added in the loop...
		// So we do it here! :)
		this.queue.put(lP, qList);
	}
	
	public PickupQueue(GiantShop p) {
		this.p = p;
		this.mH = p.getMsgHandler();
		
		this.loadQueue();
	}
	
	public void addToQueue(String transactionID, String player, int amount, int id, int type) {
		// Add purchase to database and take money
		Driver db = p.getDB().getEngine();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("transactionID");
		fields.add("player");
		fields.add("amount");
		fields.add("itemID");
		fields.add("itemType");
		
		// Insert transaction into database as ready pickup!
		InsertQuery iQ = db.insert("#__api_gsw_pickups");
		iQ.addFields(fields);
		iQ.addRow();
		iQ.assignValue("transactionID", transactionID);
		iQ.assignValue("player", player);
		iQ.assignValue("amount", String.valueOf(amount), InsertQuery.ValueType.RAW);
		iQ.assignValue("itemID", String.valueOf(id), InsertQuery.ValueType.RAW);
		iQ.assignValue("itemType", String.valueOf(type), InsertQuery.ValueType.RAW);
		
		iQ.exec();
		
		ArrayList<Queued> q;
		if(this.queue.containsKey(player)) {
			q = this.queue.remove(player);
		}else{
			q = new ArrayList<Queued>();
		}
		
		Queued Q = new Queued(id, type, amount, transactionID);
		q.add(Q);
		this.queue.put(player, q);
		
		this.stalkUser(player);
	}
	
	// Seperate method because delivery should not call remove from Queue.
	public void removeFromDB(String player, String transactionID) {
		Driver db = p.getDB().getEngine();
		
		DeleteQuery dQ = db.delete("#__api_gsw_pickups");
		dQ.where("player", player);
		dQ.where("transactionID", transactionID);
		dQ.exec();
	}
	
	public void removeFromQueue(String player, String transactionID) {
		if(this.inQueue(player)) {
			ArrayList<Queued> qList = this.queue.get(player);
			Iterator<Queued> qItr = qList.iterator();
			while(qItr.hasNext()) {
				Queued q = qItr.next();
				if(q.getTransactionID().equals(transactionID)) {
					this.removeFromDB(player, transactionID);
					qItr.remove();
					break;
				}
			}
		}
	}
	
	public boolean inQueue(String player) {
		if(this.queue.containsKey(player)) {
			return this.queue.get(player).size() > 0;
		}
		
		return false;
	}
	
	public boolean inQueue(String player, String transactionID) {
		if(this.inQueue(player)) {
			ArrayList<Queued> qList = this.queue.get(player);
			for(Queued q : qList) {
				if(q.getTransactionID().equals(transactionID)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void stalkUser(String player) {
		Player pl = p.getSrvr().getPlayerExact(player);
		if(null != pl) {
			// Player is online! Bug him about his new purchase now!
			conf c = GSWAPI.getInstance().getConfig();
			if(c.getBoolean("GiantShopWeb.Items.PickupsRequireCommand")) {
				Heraut.say(pl, mH.getMsg(Messages.msgType.MAIN, "itemWaitingForPickup"));
				Heraut.say(pl, mH.getMsg(Messages.msgType.MAIN, "itemPickupHelp"));
			}else{
				this.deliver(pl);
			}
		}
	}
	
	public Queued get(String player, String transactionID) {
		if(this.inQueue(player)) {
			ArrayList<Queued> qList = this.queue.get(player);
			for(Queued q : qList) {
				if(q.getTransactionID().equals(transactionID)) {
					return q;
				}
			}
		}
		
		return null;
	}
	
	public ArrayList<Queued> getAll(String player) {
		if(this.inQueue(player)) {
			return this.queue.get(player);
		}
		
		return null;
	}
	
	public void deliver(Player player) {
		ArrayList<Queued> qList = this.queue.remove(player.getName());
		for(Queued q : qList) {
			this.removeFromDB(player.getName(), q.getTransactionID());
			this.deliver(player, q);
		}
		
		Heraut.say(player, mH.getMsg(Messages.msgType.MAIN, "itemsDelivered"));
	}
	
	public void deliver(Player pl, Queued q) {
		if(null == q) {
			Heraut.say(pl, mH.getMsg(Messages.msgType.ERROR, "noTransaction"));
			return;
		}
		
		Inventory inv = pl.getInventory();
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("transactionID", q.getTransactionID());
		data.put("itemID", String.valueOf(q.getItemID()));
		data.put("itemType", String.valueOf(q.getItemType()));
		data.put("itemName", q.getItemName());
		data.put("amount", String.valueOf(q.getAmount()));

		Heraut.say(pl, mH.getMsg(Messages.msgType.MAIN, "itemDelivery", data));
		HashMap<String, String> d = new HashMap<String, String>();
		d.put("id", String.valueOf(q.getItemID()));
		d.put("type", String.valueOf(q.getItemType()));
		d.put("amount", String.valueOf(q.getAmount()));
		d.put("tID", q.getTransactionID());
		Logger.Log(LoggerType.GSWAPITRANSACTION, pl.getName(), d);

		ItemStack iStack;
		if(q.getItemType() != -1 && q.getItemType() != 0) {
			if(q.getItemID() != 373)
				iStack = new MaterialData(q.getItemID(), (byte) ((int) q.getItemType())).toItemStack(q.getStackAmount());
			else
				iStack = new ItemStack(q.getItemID(), q.getAmount(), (short) ((int) q.getItemType()));
		}else{
			iStack = new ItemStack(q.getItemID(), q.getStackAmount());
		}

		HashMap<Integer, ItemStack> left = inv.addItem(iStack);
		if(!left.isEmpty()) {
			Heraut.say(pl, mH.getMsg(Messages.msgType.ERROR, "infFull"));
			for(Map.Entry<Integer, ItemStack> stack : left.entrySet()) {
				pl.getWorld().dropItem(pl.getLocation(), stack.getValue());
			}
		}
	}
}
