package nl.giantit.minecraft.GiantShop.core.perms.Engines;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.perms.Permission;

import org.bukkit.entity.Player;

import java.util.logging.Level;

public class npEngine implements Permission {

	private GiantShop plugin;
	private Boolean opHasPerms;
	
	public npEngine(GiantShop plugin, Boolean opHasPerms) {
		this.plugin = plugin;
		this.opHasPerms = opHasPerms;
		
		plugin.getLogger().log(Level.INFO, "Not using any permissions!");
	}
	
	@Override
	public boolean has(String p, String perm) {
		Player player = plugin.getServer().getPlayer(p);
		if(player != null)
			return this.has(player, perm);
		
		return false;
	}

	@Override
	public boolean has(Player p, String perm) {
		if(opHasPerms && p.isOp())
			return true;
		
		return false;
	}
	
	@Override
	public boolean has(String p, String perm, String world) {
		Player player = plugin.getServer().getPlayer(p);
		if(player != null)
			return this.has(player, perm, world);
		
		return false;
	}

	@Override
	public boolean has(Player p, String perm, String world) {
		if(opHasPerms && p.isOp())
			return true;
		
		return false;
	}

	@Override
	public boolean groupHasPerm(String group, String perm) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return false;
	}

	@Override
	public boolean groupHasPerm(String group, String perm, String world) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return false;
	}

	@Override
	public boolean inGroup(String p, String group) {
		Player player = plugin.getServer().getPlayer(p);
		if(player != null)
			return this.inGroup(player, group);
		
		return false;
	}

	@Override
	public boolean inGroup(Player p, String group) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return false;
	}
	
	@Override
	public boolean inGroup(String p, String group, String world) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return false;
	}

	@Override
	public boolean inGroup(Player p, String group, String world) {
		return this.inGroup(p.getName(), group, world);
	}

	@Override
	public String getGroup(String p) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return null;
	}

	@Override
	public String getGroup(Player p) {
		return this.getGroup(p.getName());
	}

	@Override
	public String getGroup(String p, String world) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return null;
	}

	@Override
	public String getGroup(Player p, String world) {
		return this.getGroup(p.getName(), world);
	}

	@Override
	public String[] getGroups(String p) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return null;
	}

	@Override
	public String[] getGroups(Player p) {
		return this.getGroups(p.getName());
	}

	@Override
	public String[] getGroups(String p, String world) {
		plugin.getLogger().log(Level.WARNING, "NoPerms does not support groups!");
		return null;
	}

	@Override
	public String[] getGroups(Player p, String world) {
		return this.getGroups(p.getName(), world);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}
