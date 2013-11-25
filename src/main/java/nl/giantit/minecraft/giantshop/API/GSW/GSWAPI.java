package nl.giantit.minecraft.giantshop.API.GSW;

import java.io.File;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.giantit.minecraft.giantshop.API.GSW.Commands.Executor;
import nl.giantit.minecraft.giantshop.API.GSW.Crypt.RSAMan;

import nl.giantit.minecraft.giantshop.API.GSW.Exceptions.RSAKeyGenException;
import nl.giantit.minecraft.giantshop.API.GSW.Exceptions.RSAKeyLoadException;
import nl.giantit.minecraft.giantshop.API.GSW.Exceptions.RSAKeySaveException;
import nl.giantit.minecraft.giantshop.API.GSW.Listeners.PlayerListener;
import nl.giantit.minecraft.giantshop.API.GSW.Server.ShopReceiver;
import nl.giantit.minecraft.giantshop.API.GSW.Server.ShopSender;
import nl.giantit.minecraft.giantshop.API.GiantShopAPI;
import nl.giantit.minecraft.giantshop.API.conf;
import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.Misc.Misc;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Giant
 */
public class GSWAPI {
	
	private final String APIVersion = "0.0.1";
	
	private GiantShop p;
	private static GSWAPI instance;
	
	private ShopReceiver sr;
	private Map<String, ShopSender> ss = new HashMap<String, ShopSender>();
	
	private conf c;
	private String d;
	private KeyPair kp;
	private PickupQueue pQ;
	
	private boolean loaded = false;
	
	private void init() {
		File dir = new File(this.d + "/rsa");
		if(!dir.exists()) {
			dir.mkdirs();
			if(dir.exists()) {
				try {
					this.kp = RSAMan.genKey(2048);
					RSAMan.save(dir, kp);
				}catch(RSAKeyGenException e) {
					this.p.getLogger().log(Level.SEVERE, "[GSWAPI] " + e.getMsg());
					return;
				}catch(RSAKeySaveException e) {
					this.p.getLogger().log(Level.SEVERE, "[GSWAPI] " + e.getMsg());
					return;
				}
			}else{
				this.p.getLogger().log(Level.SEVERE, "[GSWAPI] Failed to create required rsa directory!");
				this.p.getLogger().log(Level.INFO, "[GSWAPI] Please manually create the directory: " + dir.toString());
				return;
			}
		}else{
			try {
				this.kp = RSAMan.load(dir);
			} catch (RSAKeyLoadException e) {
				this.p.getLogger().log(Level.SEVERE, "[GSWAPI] " + e.getMsg());
				return;
			}
		}
		
		GSWAPI.instance = this;
	}
	
	public GSWAPI(GiantShop p) {
		if(null == GiantShopAPI.Obtain().getStockAPI()) {
			p.getLogger().severe("[GSWAPI] ItemStockAPI disabled! GiantShopWebAPI depends on this!");
			p.getLogger().warning("[GSWAPI] GiantShopWebAPI will not be functional! More errors might follow!");
			return;
		}
		
		if(null == GiantShopAPI.Obtain().getGSLAPI()) {
			p.getLogger().severe("[GSWAPI] GiantShopLocationAPI disabled! GiantShopWebAPI depends on this!");
			p.getLogger().warning("[GSWAPI] GiantShopWebAPI will not be functional! More errors might follow!");
			return;
		}
		
		this.p = p;
		this.d = p.getDir() + "/API/gsw";
		File dir = new File(this.d + "/conf");
		File confFile = new File(dir, "conf.yml");
		boolean firstTime = false;
		
		if(!dir.exists()) {
			dir.mkdirs();
			if(dir.exists()) {
				p.extract(confFile, "conf.yml", "/API/GSW/Default/");
				firstTime = true;
				
				if(!confFile.exists()) {
					p.getLogger().log(Level.SEVERE, "[GSWAPI] Failed to create required configuration file!");
					return;
				}
			}else{
				p.getLogger().log(Level.SEVERE, "[GSWAPI] Failed to create required configuration directory!");
				p.getLogger().log(Level.INFO, "[GSWAPI] Please manually create the directory: " + dir.toString());
				return;
			}
		}else{
			if(!confFile.exists()) {
				p.extract(confFile, "conf.yml", "/API/GSW/Default/");
				firstTime = true;
				
				if(!confFile.exists()) {
					p.getLogger().log(Level.SEVERE, "[GSWAPI] Failed to create required configuration file!");
					return;
				}
			}
		}
		
		conf c = new conf(p, confFile);
		if(!c.isLoaded()) {
			p.getLogger().log(Level.SEVERE, "[GSWAPI] Failed to load required configuration file!");
			return;
		}
		
		if(firstTime) {
			Logger l = p.getLogger();
			l.log(Level.INFO, "---___---___---___---___---___---___---___---___---");
			l.log(Level.INFO, "Telling GiantShopWeb API to listen on port 8698.");
			l.log(Level.INFO, "If you are hosted on a shared server please check");
			l.log(Level.INFO, "with your provider, to confirm if port is free!");
			l.log(Level.INFO, "There is a chance your provider will assign you a");
			l.log(Level.INFO, "different port! Please modify conf.yml accordingly!");
			l.log(Level.INFO, "---___---___---___---___---___---___---___---___---");
			
			String ip = p.getServer().getIp();
			if (ip == null || ip.length() == 0) {
				ip = "0.0.0.0";
			}
			c.set("GiantShopWeb.Server.ip", ip);
			c.set("GiantShopWeb.Global.UUID", UUID.randomUUID().toString());
			c.save();
		}
		
		this.init();
		if(null == this.kp) {
			p.getLogger().log(Level.SEVERE, "[GSWAPI] Failed to load required rsa key files!");
			return;
		}
		
		p.getCommand("gsw").setExecutor(new Executor());
		
		this.c = c;
		InitDB.init();
		this.pQ = new PickupQueue(p);
		
		this.reload();
		p.getServer().getPluginManager().registerEvents(new PlayerListener(this), p);
		
		this.loaded = true;
	}
	
	public final void startReceiver() {
		this.sr = new ShopReceiver(this.p, this.c.getString("GiantShopWeb.Server.ip"), this.c.getInt("GiantShopWeb.Server.port"));
		sr.start();
	}
	
	public final void reload() {
		String ident = this.c.getString("GiantShopWeb.Global.UUID");
		
		ConfigurationSection cS = this.c.getConfigurationSection("GiantShopWeb.Trusted");
		if(null != cS) {
			this.ss = new HashMap<String, ShopSender>();
			Set<String> keys = cS.getKeys(false);
			for(String key : keys) {
				if(cS.getBoolean(key + ".enabled", false)) {
					if(!this.ss.containsKey(key)) {
						ShopSender sender = new ShopSender(
															this.p, 
															this, 
															key, 
															cS.getBoolean(key + ".useHTTPS", false), 
															cS.getString(key + ".host", ""), 
															cS.getInt(key + ".port", 80), 
															cS.getString(key + ".requestPath", ""), 
															cS.getString(key + ".activationPath", ""), 
															ident,
															cS.getBoolean(key + ".debug", false)
														  );
						sender.getPublicKey();
						this.ss.put(key, sender);
					}
				}
			}
		}
		
		this.shutdown();
		
		this.startReceiver();
	}
	
	public void shutdown() {
		if(null != this.sr) {
			this.sr.disable();
		}
	}
	
	public conf getConfig() {
		return this.c;
	}
	
	public ShopSender getTrustedApp(String appName) {
		if(loaded && this.isTrustedApp(appName))
			return (ShopSender) Misc.getIgnoreCase(this.ss, appName);
		
		return null;
	}
	
	public void removeTrustedApp(String appName) {
		if(loaded && this.ss.containsKey(appName)) {
			this.p.getLogger().info("[GSWAPI] Removing trusted app " + appName);
			this.ss.remove(appName);
		}
	}
	
	public boolean isTrustedApp(String appName) {
		return loaded && Misc.constainsKeyIgnoreCase(this.ss.keySet(), appName);
	}
	
	public Set<String> getTrustedApps() {
		if(loaded) {
			return this.ss.keySet();
		}
		
		return null;
	}
	
	public Map<String, ShopSender> getTrustedAppsMap() {
		if(loaded) {
			return this.ss;
		}
		
		return null;
	}
	
	public String getGSVersion() {
		return this.p.getVersion();
	}
	
	public String getAPIVersion() {
		return this.APIVersion;
	}
	
	public PickupQueue getPickupQueue() {
		if(loaded) {
			return this.pQ;
		}
		
		return null;
	}
	
	public KeyPair getKeyPair() {
		if(loaded) {
			return this.kp;
		}
		
		return null;
	}
	
	public static GSWAPI getInstance() {
		return GSWAPI.instance;
	}
}
