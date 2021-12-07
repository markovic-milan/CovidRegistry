package org.unibl.etf.mdp.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.unibl.etf.mdp.server.FajlServer;

public class FajlUtil {
	public static byte[] fileToByte(String filename) {
		byte[] b = null;
		File file = new File(filename);
		b = new byte[(int) file.length()];
		try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
			is.read(b);
		} catch (FileNotFoundException exception) {
			FajlServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			FajlServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		return b;
	}
}