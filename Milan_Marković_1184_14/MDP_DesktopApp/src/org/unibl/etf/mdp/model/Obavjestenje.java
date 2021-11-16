package org.unibl.etf.mdp.model;

import java.io.Serializable;

public class Obavjestenje implements Serializable {
	private String message;

	public Obavjestenje() {

	}

	public Obavjestenje(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Obavjestenje: " + message;
	}
}
