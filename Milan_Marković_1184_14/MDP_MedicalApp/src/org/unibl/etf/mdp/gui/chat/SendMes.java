package org.unibl.etf.mdp.gui.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.gui.main.Main;

import javafx.scene.control.TextField;

public class SendMes extends Thread {
	public static Object lock = new Object();
	private SSLSocket socket;
	private static TextField field;
	private static String protocol;

	public SendMes(SSLSocket socket) {
		this.socket = socket;
	}

	public static void set(TextField f, String p) {
		protocol = p;
		field = f;
	}

	public void run() {
		try (PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			String message = "";
			writer.println(Protocol.MED.getMessage());
			writer.flush();
			while (!Main.quit) {
				synchronized (lock) {
					lock.wait();
				}
				if (Main.quit) {
					message = Protocol.QUIT.getMessage();
					writer.println(message);
				} else if (protocol.equals(Protocol.INFO.getMessage())) {
					message = "Prekid chata";
					writer.println(protocol + message);
				} else {
					message = field.getText();
					field.clear();
					writer.println(protocol + Main.username + ": " + message);
				}
				writer.flush();
				if (message.equals(Protocol.QUIT.getMessage())) {
					Main.quit = true;
				}
			}
		} catch (IOException | InterruptedException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}