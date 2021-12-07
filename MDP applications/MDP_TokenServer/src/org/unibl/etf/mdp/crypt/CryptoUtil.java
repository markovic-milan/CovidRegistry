package org.unibl.etf.mdp.crypt;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptoUtil {
	public static SecretKey generateSecretKey() throws CryptoException {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecretKey secKey = kgen.generateKey();
			return secKey;
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage(), ex);
		}
	}

	public static byte[] sign(byte plainBytes[], PrivateKey privateKey) throws CryptoException {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
			sig.initSign(privateKey);
			sig.update(plainBytes);
			byte[] digSig = sig.sign();
			return digSig;
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage(), ex);
		}
	}

	public static boolean verify(byte plainBytes[], byte[] digSig, PublicKey publicKey) throws CryptoException {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
			sig.initVerify(publicKey);
			sig.update(plainBytes);
			boolean verified = sig.verify(digSig);
			return verified;
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage(), ex);
		}
	}

	public static byte[] encryptAsym(byte plainBytes[], PublicKey publicKey) throws CryptoException {
		byte[] cipherBytes = cipher(plainBytes, publicKey, "RSA", Cipher.ENCRYPT_MODE);
		return cipherBytes;
	}

	public static byte[] decryptAsym(byte[] cipherBytes, PrivateKey privateKey) throws CryptoException {
		byte[] plainBytes = cipher(cipherBytes, privateKey, "RSA", Cipher.DECRYPT_MODE);
		return plainBytes;
	}

	public static byte[] encryptSym(byte plainBytes[], SecretKey sessionKey) throws CryptoException {
		byte[] cipherBytes = cipher(plainBytes, sessionKey, "AES", Cipher.ENCRYPT_MODE);
		return cipherBytes;
	}

	public static byte[] decryptSym(byte[] cipherBytes, SecretKey sessionKey) throws CryptoException {
		byte[] plainBytes = cipher(cipherBytes, sessionKey, "AES", Cipher.DECRYPT_MODE);
		return plainBytes;
	}

	private static byte[] cipher(byte[] bytes, Key key, String algo, int mode) throws CryptoException {
		try {
			Cipher aes = Cipher.getInstance(algo);
			aes.init(mode, key);
			byte[] plainBytes = aes.doFinal(bytes);
			return plainBytes;
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage(), ex);
		}
	}
}