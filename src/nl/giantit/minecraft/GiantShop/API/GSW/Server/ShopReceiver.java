package nl.giantit.minecraft.GiantShop.API.GSW.Server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import nl.giantit.minecraft.GiantShop.API.GSW.Crypt.Crypt;
import nl.giantit.minecraft.GiantShop.API.GSW.GSWAPI;
import nl.giantit.minecraft.GiantShop.API.GSW.Server.lib.AESEncJSON;
import nl.giantit.minecraft.GiantShop.GiantShop;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

/**
 *
 * @author Giant
 */
public class ShopReceiver extends Thread {
	
	private final GiantShop p;
	
	private String host;
	private int port;
	private ServerSocket socket;
	
	private boolean running = false;
	
	public ShopReceiver(final GiantShop p, String host, int port) {
		this.p = p;
		this.host = host;
		this.port = port;
		
		try {
			socket = new ServerSocket();
			socket.bind(new InetSocketAddress(host, port));
			this.running = true;
		}catch(IOException e) {
			p.getLogger().severe("[GSWAPI] Failed to bind listener socket.");
			p.getLogger().severe("[GSWAPI] Please check if the host and port are not already in use!");
		}
	}
	
	public void disable() {
		if(this.running) {
			running = false;
			if(null != this.socket) {
				try {
					this.socket.close();
				}catch(IOException e) {
					p.getLogger().warning("[GSWAPI] Listener socket not properly closed!");
				}
			}
		}
	}
	
	@Override
	public void run() {
		while(this.running) {
			try {
				Socket sock = this.socket.accept();
				sock.setSoTimeout(2000); // 2 seconds should be plenty for a connection
				
				BufferedWriter bW = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				InputStream iS = sock.getInputStream();
				
				String rawInput = "";
				byte[] buffer = new byte[256];
				
				int curRead;
				while((curRead = iS.read(buffer)) != -1) {
					rawInput += new String(buffer);
				}
				
				String decrypted;
				
				if(rawInput.toString().startsWith("{")) {
					// Data encrypted had to probably be longer then 256 bytes!
					AESEncJSON json = (new Gson()).fromJson(rawInput, AESEncJSON.class);
					
					if(!json.hasAesKey() || !json.hasAesEnc()) {
						bW.write("Invalid JSON data passed!");
						bW.close();
						iS.close();
						sock.close();
						continue;
					}
					
					byte[] aesKey = Crypt.decrypt(json.getAesKeyBase64Decoded(), GSWAPI.getInstance().getKeyPair().getPrivate());
					decrypted = new String(Crypt.decryptAES(json.getAesEncBase64Decoded(), aesKey));
				}else{
					byte[] rawBytes = rawInput.getBytes();
					decrypted = new String(Crypt.decrypt(Base64.decodeBase64(rawBytes), GSWAPI.getInstance().getKeyPair().getPrivate()));
				}
				
				String[] data = decrypted.split("\n");

				if(data.length < 2) {
					// Most likely the data got malformed somewhere...
					throw new Exception("Invalid data received!");
				}

				if(!GSWAPI.getInstance().isTrustedApp(data[0])) {
					// We don't even know this guy!
					throw new Exception("Received signal from untrusted app!");
				}

				if(data[1].equals("GETSTOCK")) {
					// Need to implement!
					bW.write("GETSTOCK not yet implemented!");
					bW.flush();
				}else if(data[1].equals("GETSHOPS")) {
					// Need to implement!
					bW.write("GETSHOPS not yet implemented!");
					bW.flush();
				}else if(data[1].equals("GETBALANCE")) {
					// Need to implement!
					bW.write("GETBALANCE not yet implemented!");
					bW.flush();
				}else if(data[1].equals("SHOP")) {
					// App sends online purchase signal

					boolean found = false;
					OfflinePlayer[] oPI = Bukkit.getOfflinePlayers();
					for(OfflinePlayer oP : oPI) {
						if(oP.getName().equals(data[2])) {
							found = true;
							break;
						}
					}

					if(!found) {
						// Requested player not findable on out server
						bW.write("Player not found!");
						bW.flush();
					}else{
						// Server should be notified that purchase is ok.
						// However it might have actually not passed! As we need to check balance of player and such from a sync task!
						// Therefore we will later on send the server a signal if actuall purchase went OK!
						bW.write("Success!");
						bW.flush();
						
						ShopWorker sW = new ShopWorker(p, data);
						sW.runTask(p);
					}
				}else{
					// Unknown command
					bW.write("Unknown action requested!");
					bW.flush();
				}
				
				bW.close();
				iS.close();
				sock.close();
			}catch(SocketException e) {
				//e.printStackTrace();
			}catch(IOException e) {
				//e.printStackTrace();
			}catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}
}
