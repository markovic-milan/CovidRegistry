package org.unibl.etf.mdp.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.logger.ExceptionLogger;
import org.unibl.etf.mdp.threads.GroupChatThread;
import org.unibl.etf.mdp.threads.UserChatThread;

public class SSLServer {
	private static int numOfConnections = 0;
	public static LinkedList<SSLSocket> freeSockets = new LinkedList<>();
	public static List<SSLSocket> group = new ArrayList<SSLSocket>();
	public static HashMap<SSLSocket, SSLSocket> jedanNaJedan = new HashMap<>();
	private static final String keyFile = "src/org/unibl/etf/mdp/server/serverkey.jks";
	private static boolean work = true;
	public static Logger loger = new ExceptionLogger().getLoger();

	public static void removeSocket(SSLSocket socket) {
		if (freeSockets.contains(socket)) {
			freeSockets.remove(socket);
		}
		if (group.contains(socket)) {
			group.remove(socket);
		}
	}

	public static SSLSocket getFirst() {
		return freeSockets.pollFirst();
	}

	public static void startSSLServer() {
		int port;
		String ip;
		String keyPass;
		SSLServerSocket sslsocket = null;
		KeyStore ks;
		KeyManagerFactory kmf;
		SSLContext sslc = null;
		try {
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream("src/org/unibl/etf/mdp/server/server.config"));
				ip = prop.getProperty("IP");
				keyPass = prop.getProperty("filePass");
				port = Integer.parseInt(prop.getProperty("PORT"));

				ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream(keyFile), keyPass.toCharArray());
				kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, keyPass.toCharArray());
				sslc = SSLContext.getInstance("SSLv3");
				sslc.init(kmf.getKeyManagers(), null, null);

				SSLServerSocketFactory sslssf = sslc.getServerSocketFactory();
				sslsocket = (SSLServerSocket) sslssf.createServerSocket();
				SocketAddress sa = new InetSocketAddress(ip, port);
				sslsocket.bind(sa);
			} catch (FileNotFoundException exception) {
				loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			} catch (IOException exception) {
				loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			}
		} catch (Exception exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		System.out.println("SSLServer running...");

		String s;
		BufferedReader socketIn = null;
		SSLSocket ssocket = null;
		try {
			while (work) {
				ssocket = (SSLSocket) sslsocket.accept();
				numOfConnections++;
				System.out.println("New connection [id= " + numOfConnections + "]");
				socketIn = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
				s = socketIn.readLine();
				if (s.equals(Protocol.MED.getMessage())) {
					freeSockets.add(ssocket);
					group.add(ssocket);
					new GroupChatThread(ssocket).start();
				} else if (s.equals(Protocol.USR.getMessage())) {
					new UserChatThread(ssocket).start();
				}
			}
			socketIn.close();
			sslsocket.close();
		} catch (IOException exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void main(String[] args) {
		startSSLServer();
	}
}