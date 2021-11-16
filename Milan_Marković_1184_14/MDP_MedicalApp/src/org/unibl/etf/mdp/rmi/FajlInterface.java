package org.unibl.etf.mdp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface FajlInterface extends Remote {
	String prikazInformacija() throws RemoteException;

	byte[] download(String nazivFajla) throws RemoteException;
}