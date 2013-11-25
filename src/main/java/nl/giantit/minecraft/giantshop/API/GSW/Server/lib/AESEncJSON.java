package nl.giantit.minecraft.giantshop.API.GSW.Server.lib;

import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Giant
 */
// JSON containing AES encoded data
public class AESEncJSON {
	
	private String aesKey; // Base64 encoded RSA encrypted AES key
	private String aesEnc; // Base64 encoded AES encrypted data
	
	public AESEncJSON() {
		// Empty constructor
	}
	
	public AESEncJSON(String aesKey, String aesEnc) {
		this.aesKey = aesKey;
		this.aesEnc = aesEnc;
	}
	
	public boolean hasAesKey() {
		return null != this.aesKey && this.aesKey.length() > 0;
	}
	
	public boolean hasAesEnc() {
		return null != this.aesEnc && this.aesEnc.length() > 0;
	}
	
	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}
	
	public void setAesEnc(String aesEnc) {
		this.aesEnc = aesEnc;
	}
	
	public String getAesKey() {
		return this.aesKey;
	}
	
	public byte[] getAesKeyBase64Decoded() {
		return Base64.decodeBase64(this.aesKey);
	}
	
	public String getAesEnc() {
		return this.aesEnc;
	}
	
	public byte[] getAesEncBase64Decoded() {
		return Base64.decodeBase64(this.aesEnc);
	}
	
}
