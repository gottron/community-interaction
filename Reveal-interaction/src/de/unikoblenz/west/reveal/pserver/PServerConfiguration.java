package de.unikoblenz.west.reveal.pserver;

public class PServerConfiguration {

	/**
	 * URI (including host name, port, protocol and path prefix) which provides the PServer REST API
	 */
	private String host = null;

	/**
	 * PServer mode to operate in
	 */
	private String mode = "pers";

	/**
	 * Name of client at PServer 
	 */
	private String clientName = null;

	/**
	 * Password of client at PServer
	 */
	private String clientPass = null;

	public PServerConfiguration() {

	}

	public String getHost() {
		return host;
	}

	public String getClientName() {
		return clientName;
	}

	public String getClientPass() {
		return clientPass;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setClientPass(String clientPass) {
		this.clientPass = clientPass;
	}

}
