package net.etfbl.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.rpc.ServiceException;

import org.unibl.etf.mdp.soap.server.Token;
import org.unibl.etf.mdp.soap.server.TokenServer;
import org.unibl.etf.mdp.soap.server.TokenServerServiceLocator;

public class TokenService {
	private static TokenService instance = null;
	private static TokenServerServiceLocator locator = new TokenServerServiceLocator();
	private static TokenServer server;

	private TokenService() {

	}

	public static TokenService getInstance() {
		if (instance == null) {
			instance = new TokenService();
		}
		return instance;
	}

	public ArrayList<Token> getAktivne() throws ServiceException, RemoteException {
		server = locator.getTokenServer();
		return new ArrayList<Token>(Arrays.asList(server.getAktivne()));
	}

	public boolean exists(String username) throws ServiceException, RemoteException {
		server = locator.getTokenServer();
		return server.exists(username);
	}

	public boolean verifyToken(Token token) throws RemoteException, ServiceException {
		server = locator.getTokenServer();
		return server.verifyToken(token);
	}
}