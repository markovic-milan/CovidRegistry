package org.unibl.etf.mdp.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.server.Protocol;
import org.unibl.etf.mdp.server.SSLServer;

public class UserChatThread extends ChatThread {
	private boolean work;

	public UserChatThread(SSLSocket socket) {
		super(socket);
		work = true;
	}

	@Override
	public void run() {
		SSLSocket reciever = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message = "";
			while (work) {
				try {
					message = in.readLine();
				} catch (IOException exception) {
					SSLServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
					break;
				}
				if (message == null) {
					break;
				}
				if (SSLServer.jedanNaJedan.get(reciever) == null) {
					reciever = null;
				}
				if (message.startsWith(Protocol.QUIT.getMessage())) {
					if (reciever != null) {
						sendMessage(Protocol.INFO.getMessage(), reciever);
						SSLServer.jedanNaJedan.remove(reciever);
						SSLServer.freeSockets.add(reciever);
					}
					sendMessage(message, socket);
					break;
				}
				if (reciever == null) {
					reciever = SSLServer.getFirst();
				}
				if (reciever == null) {
					sendMessage(Protocol.INFO.getMessage() + "Nema prisutnih medicinskih osoba za chat", socket);
				} else {
					SSLServer.jedanNaJedan.put(reciever, socket);
					sendMessage(message, reciever);
				}
			}
			in.close();
		} catch (IOException exception) {
			SSLServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}