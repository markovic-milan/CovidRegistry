package org.unibl.etf.mdp.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.server.SSLServer;

public abstract class ChatThread extends Thread {
	protected SSLSocket socket;
	protected BufferedReader in;
	protected PrintWriter out;

	public ChatThread(SSLSocket socket) {
		this.socket = socket;
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException exception) {
			SSLServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public void sendMessage(String message, SSLSocket reciever) {
		try {
			out = new PrintWriter(reciever.getOutputStream());
			out.println(message);
			out.flush();
		} catch (Exception exception) {
			SSLServer.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}
}