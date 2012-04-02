package nl.giantit.minecraft.GiantShop.core;

import ru.tehkode.permissions.PermissionManager;

import org.bukkit.entity.Player;
/**
 *
 * @author Giant
 */
public class perm {
	
	private static perm instance;
	private PermissionManager pex = null;
	private String engine;
	
	private void init() {
		perm.instance = this;
	}
	
	public perm() {
		this.init();
		this.engine = "sperm";
	}
	
	public perm(PermissionManager pex) {
		this.init();
		this.pex = pex;
		this.engine = "pex";
	}
	
	public boolean has(Player player, String perm) {
		config conf = config.Obtain();
		if(conf.getBoolean("GiantShop.global.opHasPerms") && player.isOp())
			return true;
		
		if(engine.equalsIgnoreCase("sperm"))
			if(player.hasPermission(perm))
				return true;
			
		else if(engine.equalsIgnoreCase("pex"))
			if(pex.has(player, perm))
				return true;
		
		return false;
	}
	
	public static perm Obtain() {
		if(perm.instance != null)
			return perm.instance;
		
		return null;
	}
}
