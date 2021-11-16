package org.unibl.etf.mdp.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ExceptionLogger {
	private static Logger loger = Logger.getLogger("Loger");

	public ExceptionLogger() {
		try {
			FileHandler handler = new FileHandler(
					"src" + File.separator + "evidentiraniIzuzeci" + File.separator + "izuzeci.log", true);
			handler.setFormatter(new SimpleFormatter());
			loger.addHandler(handler);
		} catch (IOException izuzetak) {

		}
	}

	public static Logger getLoger() {
		return loger;
	}

	public static void setLoger(Logger loger) {
		ExceptionLogger.loger = loger;
	}
}
