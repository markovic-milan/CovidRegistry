package org.unibl.etf.mdp.model;

import org.unibl.etf.mdp.soap.server.Token;

public class User {
	private String username;
	private String name;
	private String lastName;
	private String password;
	private String jmb;
	private Token token;

	public User(String name, String lastName, String jmb) {
		super();
		this.name = name;
		this.lastName = lastName;
		this.jmb = jmb;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJmb() {
		return jmb;
	}

	public void setJmb(String jmb) {
		this.jmb = jmb;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jmb == null) ? 0 : jmb.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (jmb == null) {
			if (other.jmb != null)
				return false;
		} else if (!jmb.equals(other.jmb))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", name=" + name + ", lastName=" + lastName + ", password=" + password
				+ ", jmb=" + jmb + ", registered=" + "]";
	}
}