package org.unibl.etf.mdp.gui.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.util.Util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ReadMes extends Thread {
	private SSLSocket socket;
	private TextArea ta;

	public ReadMes(SSLSocket socket, TextArea ta) {
		this.socket = socket;
		this.ta = ta;
		start();
	}

	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String message = "";
			while (!Main.closeApp) {
				message = reader.readLine();
				if (message == null || message.startsWith(Protocol.QUIT.getMessage())) {
					break;
				} else if (message.startsWith(Protocol.INFO.getMessage())) {
					String poruka = message.split(Protocol.INFO.getMessage())[1];
					Platform.runLater(() -> Util.showAlert(poruka, false));
				} else {
					ta.appendText(message.split(Protocol.USR.getMessage())[1] + "\n");
				}
			}
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}