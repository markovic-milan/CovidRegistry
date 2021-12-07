package org.unibl.etf.mdp.soap.server;

public class Token {
	private String header;
	private String payload;
	private String sign;
	private String subject;
	private Long expTime;

	public Token() {
	}

	public Token(String subject) {
		this.subject = subject;
	}

	public Token(String header, String payload, String sign) {
		this.header = header;
		this.payload = payload;
		this.sign = sign;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Long getExpTime() {
		return expTime;
	}

	public void setExpTime(Long expTime) {
		this.expTime = expTime;
	}

	@Override
	public String toString() {
		return header + "." + payload + "." + sign;
	}

}