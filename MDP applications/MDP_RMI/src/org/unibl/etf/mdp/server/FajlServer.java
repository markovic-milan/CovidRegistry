package org.unibl.etf.mdp.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.unibl.etf.mdp.logger.ExceptionLogger;
import org.unibl.etf.mdp.rmi.FajlInterface;
import org.unibl.etf.mdp.soap.server.Token;
import org.unibl.etf.mdp.soap.server.TokenServer;
import org.unibl.etf.mdp.soap.server.TokenServerServiceLocator;
import org.unibl.etf.mdp.util.FajlUtil;

public class FajlServer implements FajlInterface {
	public static final String PATH = "resources";
	private static final String FILE_SYSTEM_PATH = "FILE_SYSTEM";
	private TokenServerServiceLocator locator = new TokenServerServiceLocator();
	public static Logger loger = new ExceptionLogger().getLoger();

	public FajlServer() throws RemoteException {

	}

	public static void main(String args[]) {
		System.setProperty("java.security.policy", PATH + File.separator + "server_policyfile.txt");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			// ocistimo fajl sistem
			deleteDirectory(new File(FILE_SYSTEM_PATH));
			File fs = new File(FILE_SYSTEM_PATH);
			if (!fs.exists()) {
				fs.mkdirs();
			}

			FajlServer server = new FajlServer();
			FajlInterface stub = (FajlInterface) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind("FileServer", stub);
			System.out.println("FileServer started.");
		} catch (Exception exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	@Override
	public byte[] download(String nazivFajla) {
		return FajlUtil.fileToByte(FILE_SYSTEM_PATH + File.separator + nazivFajla);
	}

	@Override
	public boolean upload(Token token, String filename, byte[] fileContent) throws RemoteException {
		try {
			TokenServer ser = locator.getTokenServer();
			if (!ser.verifyToken(token)) {
				return false;
			} else {
				File file = new File(FILE_SYSTEM_PATH + File.separator + filename);
				try {
					if (!file.exists())
						file.createNewFile();
					BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
					os.write(fileContent);
					os.flush();
					os.close();
				} catch (FileNotFoundException exception) {
					loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				} catch (IOException exception) {
					loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				}
			}
		} catch (ServiceException exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (RemoteException exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		return true;
	}

	@Override
	public String prikazInformacija() throws RemoteException {
		String info = "";
		File fs = new File(FILE_SYSTEM_PATH);
		if (fs.list().length > 0) {
			for (String f : fs.list()) {
				info += f + ",";
			}
			return info;
		} else {
			return "INFO:Na sistemu nema medicinskih fajlova";
		}
	}

	private static boolean deleteDirectory(File directory) {
		File[] allContents = directory.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directory.delete();
	}
}