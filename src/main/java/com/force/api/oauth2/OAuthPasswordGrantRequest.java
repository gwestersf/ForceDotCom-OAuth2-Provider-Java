package com.force.api.oauth2;


/**
 * This is a class with the fields for the username and password flow.
 * 
 * @author gwester
 */
public class OAuthPasswordGrantRequest extends OAuthRequest {
	
	private final String grant_type = "password";
	
	private final String username;
	private final String password;

	public OAuthPasswordGrantRequest(String client_id, String client_secret, String username, String password) {
		super();
		super.setConsumerKey(client_id);
		super.setConsumerSecret(client_secret);
		this.username = username;
		this.password = password;
	}

	@Override
	public String getGrantType() {
		return grant_type;
	}
	protected String getUsername() {
		return username;
	}
	protected String getPassword() {
		return password;
	}
	
	
}
