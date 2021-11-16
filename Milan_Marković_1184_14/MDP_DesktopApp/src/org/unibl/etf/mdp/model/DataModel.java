package org.unibl.etf.mdp.model;

public class DataModel {
	private User user;

	public DataModel() {

	}

	public DataModel(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
