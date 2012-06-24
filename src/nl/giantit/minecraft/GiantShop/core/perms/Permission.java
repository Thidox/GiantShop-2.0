package nl.giantit.minecraft.GiantShop.core.perms;

import org.bukkit.entity.Player;

public interface Permission {

	public boolean has(String p, String perm);
	public boolean has(Player p, String perm);
	public boolean has(String p, String perm, String world);
	public boolean has(Player p, String perm, String world);
	
	public boolean groupHasPerm(String group, String perm);
	public boolean groupHasPerm(String group, String world, String perm);
	
	public boolean inGroup(String p, String group);
	public boolean inGroup(Player p, String group);
	public boolean inGroup(String p, String group, String world);
	public boolean inGroup(Player p, String group, String world);

	public String getGroup(String p);
	public String getGroup(Player p);
	public String getGroup(String p, String world);
	public String getGroup(Player p, String world);

	public String[] getGroups(String p);
	public String[] getGroups(Player p);
	public String[] getGroups(String p, String world);
	public String[] getGroups(Player p, String world);
	
	public boolean isEnabled();
}
