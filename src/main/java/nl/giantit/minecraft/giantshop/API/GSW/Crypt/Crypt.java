package nl.giantit.minecraft.giantshop.API.GSW.Crypt;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author Giant
 */
public class Crypt {
	
	public static byte[] encrypt(byte[] data, PublicKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}
	
	public static byte[] decrypt(byte[] data, PrivateKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}
	
	public static byte[] encryptAES(byte[] data, byte[] key) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(key);
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/noPadding");
		cipher.init(Cipher.ENCRYPT_MODE, k, iv);
		return cipher.doFinal(data);
	}
	
	public static byte[] decryptAES(byte[] data, byte[] key) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(key);
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/noPadding");
		cipher.init(Cipher.DECRYPT_MODE, k, iv);
		return cipher.doFinal(data);
	}
}
