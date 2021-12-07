package org.unibl.etf.mdp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.unibl.etf.mdp.soap.server.Token;

public interface FajlInterface extends Remote {
	public boolean upload(Token token, String filename, byte[] file) throws RemoteException;
}