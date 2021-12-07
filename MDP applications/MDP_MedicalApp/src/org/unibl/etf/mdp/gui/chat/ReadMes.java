package org.unibl.etf.mdp.gui.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.util.Util;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ReadMes extends Thread {
	private SSLSocket socket;
	private static TextArea group, user;

	public static void setGroupArea(TextArea gr) {
		group = gr;
	}

	public ReadMes(SSLSocket socket) {
		this.socket = socket;
	}

	public static void setUserArea(TextArea ar) {
		user = ar;
	}

	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String message = null;
			while (!Main.quit) {
				message = reader.readLine();
				if (message.equals(Protocol.QUIT.getMessage())) {
					System.out.println("Closing");
					break;
				} else if (message.startsWith(Protocol.MED.getMessage())) {
					group.appendText(message.split(Protocol.MED.getMessage())[1] + "\n");
				} else if (message.startsWith(Protocol.USR.getMessage())) {
					if (!Main.isOpen) {
						String m = message.split(Protocol.USR.getMessage())[1];
						Platform.runLater(() -> {
							try {
								Event.fireEvent(Main.label,
										new MouseEvent(MouseEvent.MOUSE_CLICKED, 10, 10, 10, 10, MouseButton.PRIMARY, 1,
												true, true, true, true, true, true, true, true, true, true, null));
								while (!Main.isOpen)
									Thread.sleep(400);
								user.appendText(m + "\n");
							} catch (InterruptedException exception) {
								Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
							}
						});
					} else {
						user.appendText(message.split(Protocol.USR.getMessage())[1] + "\n");
					}
				} else if (message.startsWith(Protocol.INFO.getMessage())) {
					Platform.runLater(() -> {
						Util.showAlert("Korisnik je prekinuo chat", false);
						user.getScene().getWindow().hide();
						Main.isOpen = false;
					});
				}
			}
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}