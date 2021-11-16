package org.unibl.etf.mdp.model;

import javafx.beans.property.SimpleStringProperty;

public class Osoba {
	private SimpleStringProperty token;

	public Osoba(String token) {
		super();
		this.token = new SimpleStringProperty(token);
	}

	public String getToken() {
		return token.get();
	}

	public void setToken(SimpleStringProperty token) {
		this.token = token;
	}

}
