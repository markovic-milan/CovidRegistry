package net.etfbl.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisService {
	private static final String userSpace = "USER:";
	private static final String aktivni = "TOKENI:AKTIVNI:";
	private static final String zarazeni = "TOKENI:ZARAZENI:";
	private static final String potZarazeni = "TOKENI:POT_ZARAZENI:";
	private static final String userTimes = "USER:TIME:";
	private static final String mapa = "USER:map:";
	private static JedisPool jedisPool = null;
	private static boolean work = true;

	public static void startStatusCheckThread() {
		new Thread(() -> {
			// pocetne vrijednosti
			int n_Sekundi = 4;
			int p_Minuta = 20;
			int k_Metara = 2;
			Properties prop = new Properties();
			try {
				prop.load(new BufferedReader(
						new FileReader(System.getProperty("catalina.base") + "/webapps/server.config")));
				n_Sekundi = Integer.parseInt(prop.getProperty("n"));
				p_Minuta = Integer.parseInt(prop.getProperty("p"));
				k_Metara = Integer.parseInt(prop.getProperty("k"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("n= " + n_Sekundi + " s\np= " + p_Minuta + " min\nk= " + k_Metara + " m");
			while (work) {
				try {
					RedisService.checkPotencijalnoZarazeni();
					Thread.sleep(n_Sekundi * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void zarazenToken(String info) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(zarazeni, info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean checkTime(String datumOd, String vrijemeOd, String datumOdPotencijalnog,
			String vrijemeOdPotencijalnog, int period) {
		if (datumOdPotencijalnog.equals(datumOd)) {
			if (LocalTime.parse(vrijemeOdPotencijalnog).plusMinutes(period).isAfter(LocalTime.parse(vrijemeOd))
					&& LocalTime.parse(vrijemeOdPotencijalnog).minusMinutes(period)
							.isBefore(LocalTime.parse(vrijemeOd))) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public static void checkPotencijalnoZarazeni() {
		String datumZarazeni, vrijemeZarazeni, datumOdPotencijalni, vrijemeOdPotencijalni;
		String username = "";
		int xPosition, yPosition, xZarazeni, yZarazeni, len = 2, period = 30;
		try (Jedis jedis = getPool().getResource()) {
			if (jedis.scard(potZarazeni) > 0) {
				username = jedis.spop(potZarazeni);
			} else if (jedis.scard(zarazeni) > 0) {
				username = jedis.spop(zarazeni);
			}
			if (!"".equals(username))
				// prolazak kroz mapu potencijalno zarazenog
				for (String zarazenaPozicija : jedis.smembers(mapa + username)) {
					xZarazeni = Integer.parseInt(zarazenaPozicija.split(",")[0]);
					yZarazeni = Integer.parseInt(zarazenaPozicija.split(",")[1]);
					datumZarazeni = zarazenaPozicija.split(",")[2].split("\\*")[0];
					vrijemeZarazeni = zarazenaPozicija.split(",")[2].split("\\*")[1];
					for (String ime : jedis.smembers(aktivni))
						if (!ime.equals(username))
							for (String line : jedis.smembers(mapa + ime)) {
								xPosition = Integer.parseInt(line.split(",")[0]);
								yPosition = Integer.parseInt(line.split(",")[1]);
								datumOdPotencijalni = line.split(",")[2].split("\\*")[0];
								vrijemeOdPotencijalni = line.split(",")[2].split("\\*")[1];
								if (Math.abs(xZarazeni - xPosition) <= len && Math.abs(yPosition - yZarazeni) <= len
										&& checkTime(datumOdPotencijalni, vrijemeOdPotencijalni, datumZarazeni,
												vrijemeZarazeni, period)) {
									APIServis.potencijalnoZarazeni.put(ime, line);
								}
							}
				}
		}
	}

	public static String sendPotZarazeniPicture(String user) {
		return APIServis.potencijalnoZarazeni.get(user);
	}

	public static int check(String username) {
		if (APIServis.zarazeni.contains(username)) {
			return 1;
		} else if (APIServis.potencijalnoZarazeni.containsKey(username)) {
			return 2;
		} else
			return 0;
	}

	public static boolean setTime(String username, String time) {
		boolean result = false;
		try (Jedis jedis = getPool().getResource()) {
			result = jedis.sadd(userTimes + username, time) > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static void potencijalnoZarazen(String user) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(potZarazeni, user);
			APIServis.potencijalnoZarazeni.put(user, "-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void zarazen(String user) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(zarazeni, user);
			APIServis.zarazeni.add(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void nijeZarazen(String username) {
		try (Jedis jedis = getPool().getResource()) {
			if (jedis.sismember(zarazeni, username)) {
				jedis.srem(zarazeni, username);
			} else if (jedis.sismember(potZarazeni, username)) {
				jedis.srem(potZarazeni, username);
			}
			APIServis.potencijalnoZarazeni.remove(username);
			APIServis.zarazeni.remove(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setMap(String user, String position) {
		String dec = new String(Base64.getDecoder().decode(position));
		try (Jedis jedis = getPool().getResource()) {
			jedis.sadd(mapa + user, dec);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void odjava(String username) {
		try (Jedis jedis = getPool().getResource()) {
			jedis.del(userSpace + username);
			jedis.del(mapa + username);
			jedis.del(userTimes + username);
			jedis.srem(aktivni, username);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getMap(String username, int brojDana) {
		String positions = "";
		String date = LocalDate.now().minusDays(brojDana).toString();
		String lastDate = "";
		try (Jedis jedis = getPool().getResource()) {
			for (String line : jedis.smembers(mapa + username)) {
				lastDate = line.split(",")[3].split("\\*")[0];
				if (brojDana == 0 || date.compareTo(lastDate) <= 0) {
					positions += line;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return positions;
	}

	public static ArrayList<String> times(String username) {
		ArrayList<String> t = new ArrayList<>();
		try (Jedis jedis = getPool().getResource()) {
			for (String time : jedis.smembers(userTimes + username)) {
				t.add(time);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return t;
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void destroyPool() {
		jedisPool = null;
	}
}