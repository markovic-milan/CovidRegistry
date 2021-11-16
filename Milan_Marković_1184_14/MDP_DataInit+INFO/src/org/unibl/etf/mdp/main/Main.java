package org.unibl.etf.mdp.main;

import java.util.logging.Logger;

import org.unibl.etf.mdp.logger.ExceptionLogger;
import org.unibl.etf.mdp.redis.RedisServis;

public class Main {
	public static Logger loger = new ExceptionLogger().getLoger();

	// Za inicijalizaciju testnih Redis podataka
	public static void main(String[] args) {
		String korisnik1 = "marko:markovic:1111111111111";// username = korisnik
		String korisnik2 = "dusan:peric:2222222222222";// username = user

		System.out.println("Prijavljeni korisnici sa poèetnim informacijama su:\n\n"
				+ "Ime: marko\nPrezime: markovic\nJmb: 1111111111111\nKorisnicko ime: korisnik\n\n"
				+ "Ime: dusan\nPrezime: peric\nJmb: 2222222222222\nKorisnicko ime: user"
				+ "\n\n>>Prvo pokrenuti ChatServer,RMI_Server i tomcat (redoslijed nije bitan). Nakon toga mogu korisnièka i aplikacija za med. osoblje nezavisno da se pokreæu!<<\n\nTokeni su implementirani kao JWT");

		RedisServis.dodajOsobu(korisnik1);
		RedisServis.dodajOsobu(korisnik2);

		RedisServis.dodajOsobuUAktivne("korisnik");
		RedisServis.dodajOsobuUAktivne("user");

		String[] times1 = new String[] { "1614478534254:1614478640648", "1614469484343:1614469497935",
				"1614477810249:1614478042822", "1614480851895:1614480895232", "1614477315157:1614477337790",
				"1614495222872:1614496088468", "1614480491716:1614480508045", "1614478152412:1614478201441",
				"1614469007128:1614469228777", "1614469925119:1614470030411", "1614469703936:1614469746240",
				"1614452496672:1614452547461", "1614467423024:1614467477073" };

		String[] times2 = new String[] { "1614469028976:1614469225035", "1614512023764:1614512140270" };

		String[] positions1 = new String[] { "1,2,2021-02-26*12:10,2021-02-26*12:20|",
				"2,1,2021-02-27*12:00,2021-02-27*13:00|", "5,3,2021-02-27*12:00,2021-02-27*13:00|",
				"7,3,2021-02-21*14:00,2021-02-21*15:30|", "3,1,2021-02-27*13:00,2021-02-27*14:00|" };

		String[] positions2 = new String[] { "6,7,2021-02-28*21:00,2021-02-28*21:30|",
				"0,0,2021-02-26*12:00,2021-02-26*12:30|" };

		for (String s : times1) {
			RedisServis.dodajVrijeme("korisnik", s);
		}
		for (String s : times2) {
			RedisServis.dodajVrijeme("user", s);
		}

		for (String s : positions1) {
			RedisServis.dodajPoziciju("korisnik", s);
		}

		for (String s : positions2) {
			RedisServis.dodajPoziciju("user", s);
		}
	}
}
