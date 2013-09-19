package nl.giantit.minecraft.GiantShop.API.GSW.Server;

import nl.giantit.minecraft.giantcore.lib.gson.Gson;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.API.GSW.Crypt.Crypt;
import nl.giantit.minecraft.GiantShop.API.GSW.Crypt.RSAMan;
import nl.giantit.minecraft.GiantShop.API.GSW.Exceptions.RSAKeyLoadException;
import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.API.GSW.Server.lib.AESEncJSON;
import nl.giantit.minecraft.GiantShop.API.GSW.Server.resultHandlers.DebugHandler;
import nl.giantit.minecraft.GiantShop.API.GSW.Server.resultHandlers.ResultHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Giant
 */
public class ShopSender {
	
	private GiantShop p;
	private GSWAPI a;
	private String appName;
	private boolean useHTTPS;
	private String host;
	private int port;
	private String requestPath;
	private String activPath;
	private String activURI;
	private String u;
	private String ident;
	private boolean dbg = false;
	
	private ResultHandler dbgHandler;
	private ResultHandler callBack;
	
	private PublicKey pk;
	
	private boolean requirePK = true;
	private String method = "POST";
	private byte[] data;
	private String output;
	
	private boolean finished = false;
	
	public ShopSender(final GiantShop p, final GSWAPI a, String appName, boolean useHTTPS, String host, int port, String requestPath, String activPath, String ident, boolean dbg) {
		this.p = p;
		this.a = a;
		this.appName = appName;
		this.useHTTPS = useHTTPS;
		this.host = host;
		this.port = port;
		this.requestPath = requestPath;
		this.activPath = activPath;
		this.ident = ident;
		this.dbg = dbg;
		
		if(dbg) {
			this.dbgHandler = new DebugHandler(appName);
		}
		
		if(this.useHTTPS) {
			if(this.port != 443) {
				this.u = "https://" + this.host + ":" + this.port + "/" + requestPath;
				if(!activPath.startsWith("http://") && !activPath.startsWith("https://")) {
					this.activURI = "https://" + this.host + ":" + this.port + "/" + activPath;
				}else{
					this.activURI = activPath;
				}
			}else{
				this.u = "https://" + this.host + "/" + requestPath;
				if(!activPath.startsWith("http://") && !activPath.startsWith("https://")) {
					this.activURI = "https://" + this.host + "/" + activPath;
				}else{
					this.activURI = activPath;
				}
			}
		}else{
			if(this.port != 80) {
				this.u = "http://" + this.host + ":" + this.port + "/" + requestPath;
				if(!activPath.startsWith("http://") && !activPath.startsWith("https://")) {
					this.activURI = "http//" + this.host + ":" + this.port +  "/" + activPath;
				}else{
					this.activURI = activPath;
				}
			}else{
				this.u = "http://" + this.host + "/" + requestPath;
				if(!activPath.startsWith("http://") && !activPath.startsWith("https://")) {
					this.activURI = "http://" + this.host + "/" + activPath;
				}else{
					this.activURI = activPath;
				}
			}
		}
	}
	
	public String getAppName() {
		return this.appName;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getRequestPath() {
		return this.requestPath;
	}
	
	public String getActivationPath() {
		return this.activPath;
	}
	
	public String getActivationURI() {
		return this.activURI;
	}
	
	public String getHostURI() {
		if(this.useHTTPS) {
			if(this.port != 443) {
				return "https://" + this.host + ":" + this.port + "/";
			}else{
				return "https://" + this.host + "/";
			}
		}else{
			if(this.port != 80) {
				return "http://" + this.host + ":" + this.port + "/";
			}else{
				return "http://" + this.host + "/";
			}
		}
	}
	
	public void getPublicKey() {
		//this.p.getLogger().severe(this.ident);
		this.data = ("version=" + GSWAPI.getInstance().getAPIVersion() + "&ident=" + this.ident + "&data=getPubKey").getBytes();
		this.requirePK = false;
		
		Sender s = new Sender(null);
		s.runTaskAsynchronously(p);
	}
	
	public void write(String write) throws Exception {
		this.write(write, null);
	}
	
	public void write(String write, ResultHandler h) throws Exception {
		write = this.ident + "\n" + write;
		byte[] enc;
		if(write.getBytes().length > 256) {
			// Data longer then 256 bytes!
			// Encrypt using AES!
			String key = org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(16);
			String raw;
			try {
				raw = "{\"aesEnc\":"
					+ "\"" + URLEncoder.encode(new String(Base64.encodeBase64(Crypt.encryptAES(write.getBytes(), key.getBytes()))), "UTF-8").getBytes() + "\""
					+"\"aesKey\":"
					+ "\"" + URLEncoder.encode(new String(Base64.encodeBase64(Crypt.encrypt(key.getBytes(), this.pk))), "UTF-8").getBytes() + "\""
					+ "}";
			}catch(UnsupportedEncodingException e) {
				// This should hopefully never happen!
				p.getLogger().warning("[GSWAPI] Failed to use UTF-8 encoding! This is most likely not a good thing!");
				p.getLogger().warning("[GSWAPI] Falling back to unencoded msgs! This most likely WILL fail!");
				raw = "{\"aesEnc\":"
					+ "\""+ new String(Base64.encodeBase64(Crypt.encryptAES(write.getBytes(), key.getBytes()))).getBytes() + "\""
					+"\"aesKey\":"
					+ "\"" + new String(Base64.encodeBase64(Crypt.encrypt(key.getBytes(), this.pk))).getBytes() + "\""
					+ "}";
			}
			
			enc = raw.getBytes();
		}else{
			try {
				enc = URLEncoder.encode(new String(Base64.encodeBase64(Crypt.encrypt(write.getBytes(), this.pk))), "UTF-8").getBytes();
			}catch(UnsupportedEncodingException e) {
				// This should hopefully never happen!
				p.getLogger().warning("[GSWAPI] Failed to use UTF-8 encoding! This is most likely not a good thing!");
				p.getLogger().warning("[GSWAPI] Falling back to unencoded msgs! This most likely WILL fail!");
				enc = new String(Base64.encodeBase64(Crypt.encrypt(write.getBytes(), this.pk))).getBytes();
			}
		}

		byte[] param = ("version=" + GSWAPI.getInstance().getAPIVersion() + "&ident=" + this.ident + "&data=").getBytes();
		byte[] d = new byte[(param.length + enc.length)];
		
		for(int i = 0; i < d.length; ++i) {
			d[i] = i < param.length ? param[i] : enc[i - param.length];
		}

		this.data = d;
		this.setCallback(h);
		
		Sender s = new Sender(this.callBack);
		s.runTaskAsynchronously(p);
	}
	
	public boolean setCallback(ResultHandler h) {
		if(null != h) {
			this.callBack = h;
			return true;
		}
		
		this.callBack = null;
		return false;
	}
	
	public void setMethod(String m) {
		this.method = m;
	}
	
	public boolean isFinished() {
		return this.finished;
	}
	
	public String getOutput() {
		return this.output;
	}
	
	private class Sender extends BukkitRunnable {
		
		private ResultHandler callBack;
		
		private Sender(ResultHandler callBack) {
			this.callBack = callBack;
		}
	
		@Override
		public void run() {
			if(null == pk && requirePK) {
				if(dbg) {
					dbgHandler.handle("No public key found!");
				}
				//p.getLogger().severe("[GSWAPI] No public key found!");

				this.cancel();
				return;
			}

			boolean obtain = false;
			if(!requirePK) {
				requirePK = true;
				obtain = true;
			}

			if(null == data || data.length == 0) {
				if(dbg) {
					dbgHandler.handle("Unable to send data of 0 length!");
				}
				//p.getLogger().severe("[GSWAPI] Unable to send data of 0 length!");

				this.cancel();
				return;
			}

			try {
				/*if(!obtain) {
					p.getLogger().severe("[GSWAPI] " + new String(this.data));
				}*/
				finished = false;
				URL url = new URL(u); 
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod(method);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Content-Length", "" + Integer.toString(data.length));
				connection.setUseCaches (false);

				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.write(data);
				wr.flush();
				wr.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String decodedString = "";
				String dS;
				while ((dS = in.readLine()) != null) {
					decodedString += dS + "\n";
				}
				in.close();

				connection.disconnect();
				//p.getLogger().severe(decodedString);

				if(obtain) {
					try {
						//p.getLogger().severe(decodedString);
						if(decodedString.startsWith("{")) {
							// We are handling JSON data here!
							// This means our data is encrypted in AES, and our AES key is encrypted using our RSA Public Key!
							// Therefor we need to decode the AES key first if it even exists!
							AESEncJSON json = (new Gson()).fromJson(decodedString, AESEncJSON.class);

							if(!json.hasAesKey() || !json.hasAesEnc()) {
								p.getLogger().severe("[GSWAPI] Invalid json data received from app " + appName + "!");
								p.getLogger().severe("[GSWAPI] It might not be a trustable source!");
								GSWAPI.getInstance().removeTrustedApp(appName);
							}else{
								byte[] aesKey = Crypt.decrypt(json.getAesKeyBase64Decoded(), a.getKeyPair().getPrivate());
								//p.getLogger().severe(new String(aesKey));
								byte[] rsaKey = Crypt.decryptAES(json.getAesEncBase64Decoded(), aesKey);

								String safetyCheck = new String(rsaKey).split("\n")[0];
								if(safetyCheck.equalsIgnoreCase(appName)) {
									rsaKey = Arrays.copyOfRange(rsaKey, safetyCheck.length() + "\n".length(), rsaKey.length);
									//p.getLogger().severe(new String(Base64.encodeBase64(rsaKey)));
									pk = RSAMan.getPublicKeyFromBytes(rsaKey);
									
									if(dbg) {
										dbgHandler.handle("Public key received");
									}

									//write("CONCHECK");
								}else{
									p.getLogger().severe("[GSWAPI] Failed to decrypt AES encrypted public key for app " + appName + "!");
									p.getLogger().severe("[GSWAPI] It might not be a trustable source!");
									GSWAPI.getInstance().removeTrustedApp(appName);
								}
							}
						}else{
							String keyString = new String(Crypt.decrypt(Base64.decodeBase64(decodedString), a.getKeyPair().getPrivate()));
							//this.p.getLogger().severe(keyString);
							pk = RSAMan.getPublicKeyFromString(keyString);
						}
					}catch(RSAKeyLoadException e) {
						p.getLogger().severe("[GSWAPI] Invalid public key received from app " + appName + "!");
						p.getLogger().severe("[GSWAPI] It might not be a trustable source!");
						//p.getLogger().severe(e.getMsg());
						GSWAPI.getInstance().removeTrustedApp(appName);
					}
				}else{
					String d;
					if(!decodedString.startsWith("00")) {
						d = new String(Crypt.decrypt(Base64.decodeBase64(decodedString), a.getKeyPair().getPrivate()));
					}else{
						d = decodedString;
					}
					if(dbg) {
						dbgHandler.handle(d);
					}

					if(null != this.callBack) {
						this.callBack.handle(d);
					}
				}

				finished = true;
			}catch(Exception e) {
				p.getLogger().severe("[GSWAPI] An unknown error occured whilst attempting to reach app " + appName + "!");
				p.getLogger().severe("[GSWAPI] It might not be a trustable source!");
				//p.getLogger().severe(e.getMsg());
				GSWAPI.getInstance().removeTrustedApp(appName);
				//e.printStackTrace();
			}

			this.cancel();
		}
	}
}
