package org.unibl.etf.mdp.gui.chat;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.DataModel;
import org.unibl.etf.mdp.util.Controller;
import org.unibl.etf.mdp.util.Util;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController extends Controller {
	@FXML
	private TextArea chatTA;
	@FXML
	private TextField messageTF;
	
	public void initModel(DataModel model) {
		try {
			System.setProperty("javax.net.ssl.trustStore", "src/org/unibl/etf/mdp/gui/chat/clientkey.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "sigurnost");
			SSLSocketFactory sslsf = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket csocket;
			try {
				csocket = (SSLSocket) sslsf.createSocket(Main.ipAddress, Main.PORT);
			} catch (UnknownHostException exception) {
				Util.showAlert("Nepoznata adresa", true);
				Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				return;
			} catch (IOException exception) {
				Util.showAlert("Chat server nije dostupan", false);
				Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				return;
			}
			new ReadMes(csocket, chatTA);
			new SendMes(csocket, messageTF);
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		chatTA.setEditable(false);
		messageTF.setOnAction(action -> {
			chatTA.appendText(messageTF.getText() + "\n");
			synchronized (SendMes.lock) {
				SendMes.lock.notify();
			}
		});
	}
}