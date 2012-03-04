package com.force.api.oauth2.client;

import com.force.api.oauth2.model.OAuthResponse;

/**
 * 
 * @author gwester
 */
public interface SessionInitializer {
	public OAuthResponse getSession(String username, String password) throws Exception;
}
