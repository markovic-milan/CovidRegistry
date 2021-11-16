package org.unibl.etf.mdp.gui.chat;

public enum Protocol {
	MED("MED:"), USR("USR:"), QUIT("QUIT:"),INFO("INFO:");

	private String message;

	public String getMessage() {
		return message;
	}

	Protocol(String message) {
		this.message = message;
	}
}
