package org.unibl.etf.mdp.soap.redis;

import java.util.ArrayList;
import java.util.logging.Level;

import org.unibl.etf.mdp.soap.server.Token;
import org.unibl.etf.mdp.soap.server.TokenServer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisService {
	private static final String aktivni = "TOKENI:AKTIVNI:";
	private static final String neaktivni = "TOKENI:NEAKTIVNI:";
	private static final String zarazeni = "TOKENI:ZARAZENI:";
	private static final String potZarazeni = "TOKENI:POT_ZARAZENI:";
	private static JedisPool jedisPool = null;

	public static boolean exists(String username) {
		try (Jedis jedis = getPool().getResource()) {
			if (jedis.sismember(aktivni, username)) {
				return true;
			} else if (jedis.sismember(neaktivni, username)) {
				return true;
			}
		} catch (Exception exception) {
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		return false;
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
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void destroyPool() {
		jedisPool = null;
	}

	public static boolean checkCredentials(String creds) {
		boolean result = false;
		try (Jedis jedis = getPool().getResource()) {
			for (String s : jedis.smembers("USER:")) {
				if (s.equals(creds)) {
					result = true;
					break;
				}
			}
		} catch (Exception exception) {
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		return result;
	}

	public static Token[] getNeaktivni() {
		Token[] tokeni = null;
		ArrayList<Token> al = new ArrayList<>();
		try (Jedis jedis = getPool().getResource()) {
			for (String username : jedis.smembers(neaktivni)) {
				al.add(new Token(username));
			}
		} catch (Exception exception) {
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		tokeni = al.toArray(new Token[0]);
		return tokeni;
	}

	public static Token[] getAktivni() {
		Token[] tokeni = null;
		ArrayList<Token> al = new ArrayList<>();
		try (Jedis jedis = getPool().getResource()) {
			for (String username : jedis.smembers(aktivni)) {
				al.add(new Token(username));
			}
		} catch (Exception exception) {
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		tokeni = al.toArray(new Token[0]);
		return tokeni;
	}

	public static void dodajTokenUAktivne(String token) {
		try (Jedis jedis = getPool().getResource()) {
			ukloniTokenIzRegistra(token);
			jedis.sadd(aktivni, token);
		} catch (Exception exception) {
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void deaktivirajToken(String username) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.smove(aktivni, neaktivni, username);
		} catch (Exception exception) {
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void ukloniTokenIzRegistra(String token) {
		try (Jedis jedis = getPool().getResource()) {
			if (jedis.sismember(aktivni, token)) {
				jedis.srem(aktivni, token);
			} else if (jedis.sismember(neaktivni, token)) {
				jedis.srem(neaktivni, token);
			} else if (jedis.sismember(potZarazeni, token)) {
				jedis.srem(neaktivni, token);
			} else if (jedis.sismember(zarazeni, token)) {
				jedis.srem(neaktivni, token);
			}
		} catch (Exception exception) {
			TokenServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}