package com.force.api.oauth2.persistence;

import com.force.api.oauth2.model.OAuthResponse;

public interface SessionService {

	public String addSessionAndGetNewCookie(OAuthResponse sessionInfo) throws Exception;
	
	public OAuthResponse getAuthorizationInfo(String cookie) throws Exception;
}
