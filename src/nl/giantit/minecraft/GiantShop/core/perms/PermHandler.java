package nl.giantit.minecraft.GiantShop.core.perms;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.perms.Engines.*;

public class PermHandler {

	private Permission Engine = null;
	private GiantShop plugin;
	private Engines engine;
	
	public enum Engines {
		GROUP_MANAGER("Essentials Group Manager"),
		PERMISSIONSEX("Permissions Ex"),
		SPERM("Bukkit Superperms"),
		NOPERM("No Permissions");
		
		private String name;
		
		private Engines(String s) {
			this.name = s;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	private boolean packageExists(String...Packages) {
		try{
			for(String pckg : Packages) {
				Class.forName(pckg);
			}
			return true;
		}catch(ClassNotFoundException e) {
			return false;
		}
	}
	
	private Engines findEngine(String engine) {
		if(engine.equalsIgnoreCase("GROUP_MANAGER")) {
			return Engines.GROUP_MANAGER;
		}else if(engine.equalsIgnoreCase("PERMISSIONSEX")) {
			return Engines.PERMISSIONSEX;
		}else if(engine.equalsIgnoreCase("SPERM")) {
			return Engines.SPERM;
		}else if(engine.equalsIgnoreCase("NOPERM")) {
			return Engines.NOPERM;
		}
		
		return null;
	}
	
	public PermHandler(GiantShop plugin, String engine, Boolean opHasPerms) {
		this.plugin = plugin;
		this.engine = this.findEngine(engine);
		
		switch(this.engine) {
			case GROUP_MANAGER:
				if(packageExists("org.anjocaido.groupmanager.GroupManager")) {
					this.Engine = new gmEngine(this.plugin, opHasPerms);
				}
				break;
			case PERMISSIONSEX:
				if(packageExists("ru.tehkode.permissions.bukkit.PermissionsEx")) {
					this.Engine = new pexEngine(this.plugin, opHasPerms);
				}
				break;
			case NOPERM:
				this.Engine = new npEngine(this.plugin, opHasPerms);
				break;
			case SPERM:
			default:
				this.Engine = new spermEngine(this.plugin, opHasPerms);
				break;
		}
	}
	
	public Permission getEngine() {
		return Engine;
	}
	
	public String getEngineName() {
		return this.engine.toString();
	}
	
	public boolean isEnabled() {
		return (this.Engine != null && this.Engine.isEnabled());
	}
}