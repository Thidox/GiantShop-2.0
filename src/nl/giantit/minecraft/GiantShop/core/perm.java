package nl.giantit.minecraft.GiantShop.core;

//import com.nijiko.permissions.PermissionHandler;*/
import ru.tehkode.permissions.PermissionManager;

import org.bukkit.entity.Player;
/**
 *
 * @author Giant
 */
public class perm {
	
	private static perm instance;
	//private PermissionHandler permh = null;
	private PermissionManager pex = null;
	private String engine;
	
	private void init() {
		perm.instance = this;
	}
	
	public perm() {
		this.init();
		this.engine = "sperm";
	}
	
	/*public perm(PermissionHandler permh) {
		this.init();
		this.permh = permh;
		this.engine = "permh";
	}*/
	
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
			
		/*else if(engine.equalsIgnoreCase("permh"))
			if(permh.has(player, perm))
				return true;*/
			
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
