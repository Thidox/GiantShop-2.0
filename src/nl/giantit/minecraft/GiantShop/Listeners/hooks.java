package nl.giantit.minecraft.GiantShop.Listeners;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.perm;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/*import com.iConomy.*;
import com.nijikokun.bukkit.Permissions.*;
import com.nijiko.permissions.*;

import ru.tehkode.permissions.bukkit.*;
import ru.tehkode.permissions.*;*/

/**
 *
 * @author Giant
 */
public class hooks implements Listener {
	
	private GiantShop plugin;
	private config conf = config.Obtain();
	
	public hooks(GiantShop plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		if (plugin.getPermMan() != null) {
			if (event.getPlugin().getDescription().getName().equals("Permissions")) {
				plugin.setPermMan(null);
				plugin.log.log(Level.INFO, "[" + plugin.getName() + "] un-hooked from Permissions.");
				plugin.getPluginLoader().disablePlugin(plugin);
			}
			
			if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
				plugin.setPermMan(null);
				plugin.log.log(Level.INFO, "[" + plugin.getName() + "] un-hooked from PermissionsEX.");
				plugin.getPluginLoader().disablePlugin(plugin);
			}
		}
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		/*if(conf.getBoolean("GiantShop.permissions.usePermissions") == true) {
			if(conf.getString("GiantShop.permissions.permissionEngine").equals("Permissions")) {
				if (plugin.getPermMan() == null) {
					Plugin Permission = plugin.getServer().getPluginManager().getPlugin("Permissions");

					if (Permission != null) {
						if (Permission.isEnabled() && Permission.getClass().getName().equals("com.nijikokun.bukkit.Permissions.Permissions")) {
							Permissions per = (Permissions)Permission;
							plugin.setPermMan(new perm((PermissionHandler)per.getHandler()));
							plugin.log.log(Level.INFO, "[" + plugin.getName() + "] hooked into Permissions.");
						}
					}
				}
			}else if(conf.getString("GiantShop.permissions.permissionEngine").equals("PEX")) {
				if (plugin.getPermMan() == null) {
					Plugin PermissionsEx = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");

					if (PermissionsEx != null) {
						if (PermissionsEx.isEnabled() && PermissionsEx.getClass().getName().equals("ru.tehkode.permissions.bukkit.PermissionsEx")) {
							PermissionsEx pex = (PermissionsEx)PermissionsEx;
							plugin.setPermMan(new perm((PermissionManager)pex.getPermissionManager()));
							plugin.log.log(Level.INFO, "[" + plugin.getName() + "] hooked into PermissionsEX.");
						}
					}
				}
			}
		}*/
	}
}
