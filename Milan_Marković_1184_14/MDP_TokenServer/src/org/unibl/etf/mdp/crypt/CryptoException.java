package org.unibl.etf.mdp.crypt;

public class CryptoException extends Exception {
	private static final long serialVersionUID = 1L;

	public CryptoException(String message, Throwable t) {
		super(message, t);
	}

	public CryptoException(String message) {
		super(message);
	}
}