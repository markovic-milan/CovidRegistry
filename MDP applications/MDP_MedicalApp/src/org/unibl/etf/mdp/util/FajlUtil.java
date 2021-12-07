package org.unibl.etf.mdp.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.unibl.etf.mdp.gui.main.Main;

public class FajlUtil {
	public static byte[] fileToByte(String filename) {
		byte[] b = null;
		try {
			File file = new File(filename);
			b = new byte[(int) file.length()];
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
			is.read(b);
		} catch (FileNotFoundException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		return b;
	}
}