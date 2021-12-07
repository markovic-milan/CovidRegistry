package org.unibl.etf.mdp.model;

import java.util.Date;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class LoginTimes {
	private SimpleStringProperty prijava;
	private SimpleStringProperty odjava;
	private SimpleLongProperty razlika;

	public LoginTimes(String prijava, String odjava) {
		this.prijava = new SimpleStringProperty(new Date(Long.parseLong(prijava)).toString());
		this.odjava = new SimpleStringProperty(new Date(Long.parseLong(odjava)).toString());
		this.razlika = new SimpleLongProperty(Long.parseLong(odjava) - Long.parseLong(prijava));
	}

	public Long getRazlika() {
		return razlika.get() / 1000;
	}

	public String getPrijava() {
		return prijava.get();
	}

	public void setPrijava(SimpleStringProperty prijava) {
		this.prijava = prijava;
	}

	public String getOdjava() {
		return odjava.get();
	}

	public void setOdjava(SimpleStringProperty odjava) {
		this.odjava = odjava;
	}
}