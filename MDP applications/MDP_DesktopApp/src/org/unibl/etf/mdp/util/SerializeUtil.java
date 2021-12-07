package org.unibl.etf.mdp.util;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.Obavjestenje;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;

public class SerializeUtil {
	private static String OBAVJESTENJA_PATH = "./obavjestenja/";
	private static int brojac = 0;
	private static int serializationType = 0;

	public static void serialize(Obavjestenje obavjestenje) {
		// nova nit za serijalizaciju
		new Thread(() -> {
			File folder = new File(OBAVJESTENJA_PATH + Main.model.getUser().getUsername());
			if (!folder.exists()) {
				folder.mkdirs();
			}
			if (serializationType == 4) {
				serializationType = 0;
			}
			switch (serializationType) {
			case 0: {
				serializeWithGson(obavjestenje);
				break;
			}
			case 1: {
				serializeWithKryo(obavjestenje);
				break;
			}
			case 2: {
				serializeWithJava(obavjestenje);
				break;
			}
			case 3: {
				serializeWithXML(obavjestenje);
				break;
			}
			}
			brojac++;
			serializationType++;
		}).start();
	}

	// GSON
	private static void serializeWithGson(Obavjestenje obavjestenje) {
		Gson gson = new Gson();
		try {
			FileWriter out = new FileWriter(
					new File(OBAVJESTENJA_PATH + Main.model.getUser().getUsername() + "/gson" + brojac + ".out"));
			out.write(gson.toJson(obavjestenje));
			out.close();
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	// Kryo
	private static void serializeWithKryo(Obavjestenje obavjestenje) {
		Kryo kryo = new Kryo();
		kryo.register(Obavjestenje.class);
		try {
			Output out = new Output(new FileOutputStream(
					new File(OBAVJESTENJA_PATH + Main.model.getUser().getUsername() + "/kryo" + brojac + ".out")));
			kryo.writeClassAndObject(out, obavjestenje);
			out.close();
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	// Java
	private static void serializeWithJava(Obavjestenje obavjestenje) {
		try {
			FileOutputStream fileOut = new FileOutputStream(
					OBAVJESTENJA_PATH + Main.model.getUser().getUsername() + "/java" + brojac + ".out");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(obavjestenje);
			out.close();
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	// XML
	private static void serializeWithXML(Obavjestenje obavjestenje) {
		try {
			XMLEncoder encoder = new XMLEncoder(new FileOutputStream(
					new File(OBAVJESTENJA_PATH + Main.model.getUser().getUsername() + "/xml" + brojac + ".out")));
			encoder.writeObject(obavjestenje);
			encoder.close();
		} catch (Exception exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}