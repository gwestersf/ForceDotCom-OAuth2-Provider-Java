package com.force.api.oauth2;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

/**
 * This class holds recent sessions.
 * 
 * @author gwester
 */
public class SessionService {

	private Map<String, OAuthResponse> cookieToAuthorizationInfo;
	
	private static SessionService SINGLETON;
	
	private SessionService() {
		cookieToAuthorizationInfo = Maps.<String, OAuthResponse>newHashMap();
	}
	
	public static SessionService getInstance() {
		if(SINGLETON == null) {
			//double check locking
			synchronized(SessionService.class) {
				if(SINGLETON == null) {
					SINGLETON = new SessionService();
				}
			}
		}
		return SINGLETON;
	}
	
	/**
	 * 
	 * @param sessionInfo
	 * @return a UUID
	 */
	public String addSessionAndGetNewCookie(OAuthResponse sessionInfo) {
		String cookieValue = UUID.randomUUID().toString();
		cookieToAuthorizationInfo.put(cookieValue, sessionInfo);
		return cookieValue;
	}
	
	public OAuthResponse getAuthorizationInfo(String cookie) {
		return cookieToAuthorizationInfo.get(cookie);
	}
}
