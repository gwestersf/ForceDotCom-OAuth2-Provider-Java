package com.force.api.oauth2;

/**
 * 
 * @author gwester
 */
public interface UsernamePasswordFlow {

	public OAuthResponse getSession(String username, String password) throws Exception;
}
