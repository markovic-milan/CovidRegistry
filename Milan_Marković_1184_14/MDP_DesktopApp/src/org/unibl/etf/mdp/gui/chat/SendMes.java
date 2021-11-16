package org.unibl.etf.mdp.gui.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.gui.main.Main;

import javafx.scene.control.TextField;

public class SendMes extends Thread {
	public static final Object lock = new Object();
	private TextField tf;
	private PrintWriter writer;

	public SendMes(SSLSocket socket, TextField tf) throws IOException {
		this.tf = tf;
		writer = new PrintWriter(socket.getOutputStream());
		writer.println(Protocol.USR.getMessage());
		writer.flush();
		start();
	}

	public void run() {
		try {
			String message = "";
			while (!Main.closeApp) {
				synchronized (lock) {
					lock.wait();
				}
				if (Main.closeApp) {
					writer.println(Protocol.QUIT.getMessage());
					writer.flush();
					break;
				} else {
					message = tf.getText();
					tf.clear();
					writer.println(Protocol.USR.getMessage() + Main.model.getUser().getUsername() + ": " + message);
					writer.flush();
				}
			}
		} catch (InterruptedException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}