package com.force.api.oauth2;

import org.apache.commons.httpclient.NameValuePair;

/**
 * 
 * @author gwester
 */
public class OAuthPasswordGrantRequest extends OAuthRequest {
	
	private final String grant_type = "password";

	//NOTE: never distribute a client secret with client (mobile or desktop) software!
	private String client_secret;
	
	private String username;
	private String password;

	@Override
	public String getGrantType() {
		return grant_type;
	}

	/**
	 * NOTE: never distribute a client secret with client (mobile or desktop) software!
	 * @param client_secret also known as 'client secret'
	 */
	public void setConsumerSecret(String client_secret) {
		this.client_secret = client_secret;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	/*
	 * These getters should not be used as these are responses from the API.
	 * The JSON deserializer uses reflection to set these fields.
	 */
	
	/**
	 * NOTE: never distribute a client secret with client (mobile or desktop) software!
	 * @return
	 */
	protected String getConsumerSecret() {
		return client_secret;
	}
	protected String getUsername() {
		return username;
	}
	protected String getPassword() {
		return password;
	}
	
	
}
