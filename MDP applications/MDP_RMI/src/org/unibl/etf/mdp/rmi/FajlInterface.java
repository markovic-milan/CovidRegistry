package org.unibl.etf.mdp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.unibl.etf.mdp.soap.server.Token;

public interface FajlInterface extends Remote {
	String prikazInformacija() throws RemoteException;

	boolean upload(Token token, String filename, byte[] file) throws RemoteException;

	byte[] download(String nazivFajla) throws RemoteException;
}