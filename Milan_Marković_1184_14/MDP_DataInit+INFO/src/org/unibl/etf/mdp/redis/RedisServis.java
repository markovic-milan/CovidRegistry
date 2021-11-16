package org.unibl.etf.mdp.redis;

import java.util.logging.Level;

import org.unibl.etf.mdp.main.Main;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisServis {
	private static final String aktivni = "TOKENI:AKTIVNI:";
	private static final String userSpace = "USER:";
	private static final String times = "USER:TIME:";
	private static final String map = "USER:map:";

	private static JedisPool jedisPool = null;

	public static void dodajOsobu(String creds) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(userSpace, creds);
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static JedisPool getPool() {
		if (jedisPool == null) {
			jedisPool = new JedisPool("localhost");
		}
		return jedisPool;
	}

	public static void flushAll() {
		try (Jedis jedis = getPool().getResource()) {
			jedis.flushAll();
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void destroyPool() {
		jedisPool = null;
	}

	public static void dodajOsobuUAktivne(String username) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(aktivni, username);
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void dodajVrijeme(String username, String time) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(times + username, time);
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void dodajPoziciju(String username, String position) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(map + username, position);
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}