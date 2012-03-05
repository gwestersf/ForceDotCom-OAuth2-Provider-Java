package com.force.api.oauth2.persistence;

import java.util.Map;
import java.util.UUID;

import com.force.api.oauth2.model.OAuthResponse;
import com.google.common.collect.Maps;

/**
 * This class holds recent sessions.  If you scale this web application beyond a single node, you'll need 
 * to put these sessions in a data store.  A request can land on any node (or web worker, or JVM, or whatever).
 * 
 * @author gwester
 */
public class SingleNodeSessionService implements SessionService {

	private Map<String, OAuthResponse> cookieToAuthorizationInfo;
	
	private static SingleNodeSessionService SINGLETON;
	
	private SingleNodeSessionService() {
		cookieToAuthorizationInfo = Maps.<String, OAuthResponse>newHashMap();
	}
	
	public static SingleNodeSessionService getInstance() {
		if(SINGLETON == null) {
			//double check locking
			synchronized(SingleNodeSessionService.class) {
				if(SINGLETON == null) {
					SINGLETON = new SingleNodeSessionService();
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
