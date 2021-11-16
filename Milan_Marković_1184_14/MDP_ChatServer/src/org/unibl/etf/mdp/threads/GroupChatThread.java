package org.unibl.etf.mdp.threads;

import java.io.IOException;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.server.Protocol;
import org.unibl.etf.mdp.server.SSLServer;

public class GroupChatThread extends ChatThread {
	private boolean work;

	public GroupChatThread(SSLSocket socket) {
		super(socket);
		work = true;
	}

	@Override
	public void run() {
		try {
			String message = "";
			while (work) {
				try {
					message = in.readLine();
				} catch (IOException exception) {
					SSLServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
					break;
				}
				if (message.startsWith(Protocol.QUIT.getMessage())) {
					sendMessage(Protocol.QUIT.getMessage(), socket);
					SSLServer.removeSocket(socket);
					break;
				} else if (message.startsWith(Protocol.MED.getMessage())) {
					for (SSLSocket s : SSLServer.group) {
						if (s != socket) {
							sendMessage(message, s);
						}
					}
				} else if (message.startsWith(Protocol.USR.getMessage())) {
					SSLSocket reciever = SSLServer.jedanNaJedan.get(socket);
					sendMessage(message, reciever);
				} else if (message.startsWith(Protocol.INFO.getMessage())) {
					SSLSocket reciever = SSLServer.jedanNaJedan.get(socket);
					if (reciever != null) {
						sendMessage(message, reciever);
						SSLServer.jedanNaJedan.remove(socket);
						SSLServer.freeSockets.add(socket);
					}
				}
			}
			in.close();
			if (out != null)
				out.close();
			socket.close();
		} catch (IOException exception) {
			SSLServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}