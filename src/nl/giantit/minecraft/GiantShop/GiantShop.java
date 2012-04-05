package nl.giantit.minecraft.GiantShop;

import nl.giantit.minecraft.GiantShop.core.perm;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Database.db;
import nl.giantit.minecraft.GiantShop.core.Eco.Eco;
import nl.giantit.minecraft.GiantShop.core.Items.Items;
import nl.giantit.minecraft.GiantShop.Misc.Messages;
import nl.giantit.minecraft.GiantShop.Misc.Misc;
import nl.giantit.minecraft.GiantShop.Executors.*;
import nl.giantit.minecraft.GiantShop.Listeners.*;
import nl.giantit.minecraft.GiantShop.Locationer.Locationer;
import nl.giantit.minecraft.GiantShop.Locationer.Listeners.*;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 * @author Giant
 */
public class GiantShop extends JavaPlugin {
	
	private static GiantShop plugin;
	private static Server Server;
	private db database;
	private perm perms;
	private chat chat;
	private console console;
	private Items itemHandler;
	private Eco econHandler;
	private Messages msgHandler;
	private String name, dir, pubName;
	private String bName = "Moulin Rouge";
	
	public static final Logger log = Logger.getLogger("Minecraft");
	public Locationer loc;
	public boolean useLoc = false;
	public List<String> cmds;
	
	private void setPlugin() {
		GiantShop.plugin = this;
	}
	
	public GiantShop() {
		this.setPlugin();
	}
	
	@Override
	public void onEnable() {
		Server = this.getServer();
		
		this.name = getDescription().getName();
		this.dir = getDataFolder().toString();
		
		File configFile = new File(getDataFolder(), "conf.yml");
		if(!configFile.exists()) {
			getDataFolder().mkdir();
			getDataFolder().setWritable(true);
			getDataFolder().setExecutable(true);
			
			extractDefaultFile("conf.yml");
		}
		config conf = config.Obtain();
		try {
			conf.loadConfig(configFile);
			this.database = new db(this);
			
			getServer().getPluginManager().registerEvents(new hooks(this), this);
			if(conf.getBoolean("GiantShop.permissions.usePermissions") == true) {
				if(conf.getString("GiantShop.permissions.permissionEngine").equals("sperm")) {
					setPermMan(new perm());
				}
			}
			
			if(conf.getBoolean("GiantShop.Location.useGiantShopLocation")) {
				useLoc = true;
				loc = new Locationer(this);
				cmds = conf.getStringList("GiantShop.Location.protected.Commands");
				
				if(conf.getBoolean("GiantShop.Location.showPlayerEnteredShop"))
					getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
				
			}
			pubName = conf.getString("GiantShop.global.name");
			chat = new chat(this);
			console = new console(this);
			itemHandler = new Items(this);
			econHandler = new Eco(this);
			msgHandler = new Messages(this);
			
			if(econHandler.isLoaded()) {
				log.log(Level.INFO, "[" + this.name + "](" + this.bName + ") Was successfully enabled!");
			}else{
				log.log(Level.WARNING, "[" + this.name + "] Could not load economy engine yet!");
				log.log(Level.WARNING, "[" + this.name + "] Errors might occur if you do not see '[GiantShop]Successfully hooked into (whichever) Engine!' after this message!");
			}
		}catch(Exception e) {
			log.log(Level.SEVERE, "[" + this.name + "](" + this.bName + ") Failed to load!");
			if(conf.getBoolean("GiantShop.global.debug"))
				log.log(Level.INFO, "" + e);
			Server.getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable() {
		log.log(Level.INFO, "[" + this.name + "] Was successfully dissabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (Misc.isEitherIgnoreCase(cmd.getName(), "shop", "s")) {
			if(!(sender instanceof Player)){
				return console.exec(sender, cmd, commandLabel, args);
			}
			
			return chat.exec(sender, cmd, commandLabel, args);
		}else if (cmd.getName().equalsIgnoreCase("loc")) {
			return loc.onCommand(sender, cmd, commandLabel, args);
		}
		
		return false;
	}
	
	public String getPubName() {
		return this.pubName;
	}
	
	public String getDir() {
		return this.dir;
	}
	
	public String getSeparator() {
		return File.separator;
	}
	
	public db getDB() {
		return this.database;
	}
	
	public perm getPermMan() {
		return this.perms;
	}
	
	public void setPermMan(perm perm) {
		this.perms = perm;
	}
	
	public Server getSrvr() {
		return getServer();
	}
	
	public Items getItemHandler() {
		return this.itemHandler;
	}
	
	public Eco getEcoHandler() {
		return this.econHandler;
	}
	
	public Messages getMsgHandler() {
		return this.msgHandler;
	}
	
	public void extract(String file) {
		extractDefaultFile(file);
	}
	
	public static GiantShop getPlugin() {
		return GiantShop.plugin;
	}
	
	private void extractDefaultFile(String file) {
		File configFile = new File(getDataFolder(), file);
		if (!configFile.exists()) {
			InputStream input = this.getClass().getResourceAsStream("/nl/giantit/minecraft/" + name + "/core/Default/" + file);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(configFile);
					byte[] buf = new byte[8192];
					int length = 0;

					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					log.log(Level.INFO, "[" + name + "] copied default file: " + file);
				} catch (Exception e) {
					Server.getPluginManager().disablePlugin(this);
					log.log(Level.SEVERE, "[" + name + "] AAAAAAH!!! Can't extract the requested file!!", e);
					return;
				} finally {
					try {
						if (input != null) {
							input.close();
						}
					} catch (Exception e) {
						Server.getPluginManager().disablePlugin(this);
						log.log(Level.SEVERE, "[" + name + "] AAAAAAH!!! Severe error!!", e);	
					}
					try {
						if (output != null) {
							output.close();
						}
					} catch (Exception e) {
						Server.getPluginManager().disablePlugin(this);
						log.log(Level.SEVERE, "[" + name + "] AAAAAAH!!! Severe error!!", e);
					}
				}
			}
		}
	}
}
