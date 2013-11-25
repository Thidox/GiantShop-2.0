package nl.giantit.minecraft.giantshop.API.GSW.Crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import javax.xml.bind.DatatypeConverter;

import nl.giantit.minecraft.giantshop.API.GSW.Exceptions.RSAKeyGenException;
import nl.giantit.minecraft.giantshop.API.GSW.Exceptions.RSAKeyLoadException;
import nl.giantit.minecraft.giantshop.API.GSW.Exceptions.RSAKeySaveException;

/**
 *
 * @author Giant
 */
public class RSAMan {
	
	public static void save(File dir, KeyPair kp) throws RSAKeySaveException {
		PrivateKey privKey = kp.getPrivate();
		PublicKey pubKey = kp.getPublic();
		
		try {
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKey.getEncoded());
			FileOutputStream pubKeyOS = new FileOutputStream(dir + "/pub.key");
			pubKeyOS.write(DatatypeConverter.printBase64Binary(pubKeySpec.getEncoded()).getBytes());
			pubKeyOS.close();
		}catch(FileNotFoundException e) {
			throw new RSAKeySaveException("Failed to save public key file!", RSAKeySaveException.ErrType.PublicKeyFileNotFoundError);
		}catch(IOException e) {
			throw new RSAKeySaveException("Failed to close public key file stream!", RSAKeySaveException.ErrType.PublicKeyFileCloseError);
		}
		
		try {
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privKey.getEncoded());
			FileOutputStream privKeyOS = new FileOutputStream(dir + "/priv.key");
			privKeyOS.write(DatatypeConverter.printBase64Binary(privKeySpec.getEncoded()).getBytes());
			privKeyOS.close();
		}catch(FileNotFoundException e) {
			throw new RSAKeySaveException("Failed to save private key file!", RSAKeySaveException.ErrType.PrivateKeyFileNotFoundError);
		}catch(IOException e) {
			throw new RSAKeySaveException("Failed to close private key file stream!", RSAKeySaveException.ErrType.PrivateKeyFileCloseError);
		}
	}
	
	public static KeyPair load(File dir) throws RSAKeyLoadException {
		File pubKeyFile = new File(dir + "/pub.key");
		byte[] encPubKey = new byte[(int) pubKeyFile.length()];
		
		File privKeyFile = new File(dir + "/priv.key");
		byte[] encPrivKey = new byte[(int) privKeyFile.length()];
		
		try {
			FileInputStream pubKeyIS = new FileInputStream(dir + "/pub.key");

			pubKeyIS.read(encPubKey);
			encPubKey = DatatypeConverter.parseBase64Binary(new String(encPubKey));
			pubKeyIS.close();
		}catch(FileNotFoundException e) {
			throw new RSAKeyLoadException("Failed to load public key file!", RSAKeyLoadException.ErrType.PublicKeyFileNotFoundError);
		}catch(IOException e) {
			throw new RSAKeyLoadException("Failed to close public key file stream!", RSAKeyLoadException.ErrType.PublicKeyFileCloseError);
		}

		try {
			FileInputStream privKeyIS = new FileInputStream(dir + "/priv.key");

			privKeyIS.read(encPrivKey);
			encPrivKey = DatatypeConverter.parseBase64Binary(new String(encPrivKey));
			privKeyIS.close();
		}catch(FileNotFoundException e) {
			throw new RSAKeyLoadException("Failed to load private key file!", RSAKeyLoadException.ErrType.PrivateKeyFileNotFoundError);
		}catch(IOException e) {
			throw new RSAKeyLoadException("Failed to close private key file stream!", RSAKeyLoadException.ErrType.PrivateKeyFileCloseError);
		}
		
		PublicKey pubKey;
		PrivateKey privKey;
		KeyFactory kf;
		
		try {
			kf = KeyFactory.getInstance("RSA");
		}catch(NoSuchAlgorithmException e) {
			throw new RSAKeyLoadException("Error loading RSA algorithm!", RSAKeyLoadException.ErrType.RSAAlgorithmNotFoundError);
		}
		
		try {
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encPubKey);
			pubKey = kf.generatePublic(pubKeySpec);
		}catch(InvalidKeySpecException e) {
			throw new RSAKeyLoadException("Failed to decrypt public key!", RSAKeyLoadException.ErrType.PublicKeyDecryptError);
		}
		
		try {
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encPrivKey);
			privKey = kf.generatePrivate(privKeySpec);
		}catch(InvalidKeySpecException e) {
			throw new RSAKeyLoadException("Failed to decrypt private key!", RSAKeyLoadException.ErrType.PrivateKeyDecryptError);
		}
		
		return new KeyPair(pubKey, privKey);
	}
	
	public static PublicKey getPublicKeyFromString(String keyString) throws RSAKeyLoadException {
		byte[] encPubKey = keyString.getBytes();
		
		return RSAMan.getPublicKeyFromBytes(encPubKey);
	}
	
	public static PublicKey getPublicKeyFromBytes(byte[] encPubKey) throws RSAKeyLoadException {
		PublicKey pubKey;
		KeyFactory kf;
		
		try {
			kf = KeyFactory.getInstance("RSA");
		}catch(NoSuchAlgorithmException e) {
			throw new RSAKeyLoadException("Error loading RSA algorithm!", RSAKeyLoadException.ErrType.RSAAlgorithmNotFoundError);
		}
		
		try {
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encPubKey);
			pubKey = kf.generatePublic(pubKeySpec);
		}catch(InvalidKeySpecException e) {
			e.printStackTrace();
			throw new RSAKeyLoadException("Failed to encode public key!", RSAKeyLoadException.ErrType.PublicKeyDecryptError);
		}
		
		return pubKey;
	}
	
	public static KeyPair genKey(int bits) throws RSAKeyGenException {
		try {
			KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
			RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(bits, RSAKeyGenParameterSpec.F4);
			kg.initialize(spec);
			return kg.genKeyPair();
		}catch(NoSuchAlgorithmException ex) {
			throw new RSAKeyGenException("Error loading RSA algorithm!", RSAKeyGenException.ErrType.RSAAlgorithmNotFoundError);
		}catch(InvalidAlgorithmParameterException ex) {
			throw new RSAKeyGenException("Invalid algorithm supplied!", RSAKeyGenException.ErrType.InvalidAlgorithmError);
		}
	}
}
