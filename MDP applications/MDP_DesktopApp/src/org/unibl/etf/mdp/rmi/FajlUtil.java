package org.unibl.etf.mdp.rmi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.unibl.etf.mdp.gui.main.Main;

public class FajlUtil {
	public static byte[] fileToByte(String filename) {
		byte[] b = null;
		BufferedInputStream is = null;
		try {
			File file = new File(filename);
			b = new byte[(int) file.length()];
			is = new BufferedInputStream(new FileInputStream(file));
			is.read(b);
		} catch (FileNotFoundException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException exception) {
					Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				}
			}
		}
		return b;
	}

	public static void zipFiles(String... fileNames) throws IOException {
		List<String> srcFiles = Arrays.asList(fileNames);
		FileOutputStream fos = new FileOutputStream("medicinskiFajlovi/compressed.zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		for (String srcFile : srcFiles) {
			File fileToZip = new File(srcFile);
			FileInputStream fis = new FileInputStream(fileToZip);
			ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
			zipOut.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
			fis.close();
		}
		zipOut.close();
		fos.close();
	}
}