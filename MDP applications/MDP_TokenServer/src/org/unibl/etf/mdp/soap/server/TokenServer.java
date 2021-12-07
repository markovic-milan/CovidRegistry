package org.unibl.etf.mdp.soap.server;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.crypt.CryptoUtil;
import org.unibl.etf.mdp.logger.ExceptionLogger;
import org.unibl.etf.mdp.soap.redis.RedisService;

public class TokenServer {
	private static PublicKey pubKey;
	private static String header = Base64.getEncoder().encodeToString("{\"alg\": \"RSA256\"}".getBytes());
	public static Logger loger = new ExceptionLogger().getLoger();

	public boolean exists(String subject) {
		return RedisService.exists(subject);
	}

	public Token[] getAktivne() {
		return RedisService.getAktivni();
	}

	public Token[] getNeaktivne() {
		return RedisService.getNeaktivni();
	}

	public void deactivateToken(String subject) {
		RedisService.deaktivirajToken(subject);
	}

	public void clearRedis() {
		RedisService.flushAll();
	}

	public void odjavaIzRegistra(String username) {
		RedisService.ukloniTokenIzRegistra(username);
	}

	public boolean verifyToken(Token token) {
		boolean verify = false;
		String payload = token.getPayload();
		byte[] payloadDec = Base64.getDecoder().decode(payload);
		Long time = Long.parseLong(new String(payloadDec).split(",")[0]);
		if (time < System.currentTimeMillis()) {
			System.out.println("Token vise nije vazeci!");
			return false;
		} else {
			byte[] signature = Base64.getDecoder().decode(token.getSign());
			String params = token.getHeader() + "." + token.getPayload();
			byte[] data = params.getBytes();
			KeyStore ks;
			try {
				ks = KeyStore.getInstance("pkcs12");
				ks.load(new FileInputStream(System.getProperty("catalina.base") + "/webapps/server.pkcs12"),
						"server123".toCharArray());
				verify = CryptoUtil.verify(data, signature, pubKey);
			} catch (Exception exception) {
				loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			}
		}
		return verify;
	}

	public Token getToken(String params) {
		Token token = null;
		Long vrijeme = System.currentTimeMillis() + 3600000;
		String username = params.split(":")[3];
		String creds = params.split(":")[0] + ":" + params.split(":")[1] + ":" + params.split(":")[2];
		String payload = vrijeme + "," + username;
		String encPayload = Base64.getEncoder().encodeToString(payload.getBytes());
		if (!RedisService.checkCredentials(creds)) {
			System.out.println("Wrong credentials");
		} else {
			KeyStore ks;
			try {
				ks = KeyStore.getInstance("pkcs12");
				ks.load(new FileInputStream(System.getProperty("catalina.base") + "/webapps/server.pkcs12"),
						"server123".toCharArray());
				PrivateKey privKey = (PrivateKey) ks.getKey("server", "server123".toCharArray());
				pubKey = ks.getCertificate("server").getPublicKey();
				String data = header + "." + encPayload;
				byte[] sign = CryptoUtil.sign(data.getBytes(), privKey);
				token = new Token(header, encPayload, Base64.getEncoder().encodeToString(sign));
				token.setSubject(username);
				token.setExpTime(vrijeme);
				RedisService.dodajTokenUAktivne(username);
				System.out.println("TOKEN= " + token);
			} catch (Exception exception) {
				loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			}
		}
		return token;
	}
}