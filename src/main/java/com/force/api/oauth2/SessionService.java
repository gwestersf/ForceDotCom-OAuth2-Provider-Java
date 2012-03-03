package com.force.api.oauth2;

import java.util.Queue;

import com.google.common.collect.Queues;

/**
 * This class holds recent sessions.
 * 
 * @author gwester
 */
public class SessionService {

	private Queue<OAuthResponse> sessions;
	
	private static SessionService SINGLETON;
	
	private SessionService() {
		sessions = Queues.<OAuthResponse>newConcurrentLinkedQueue();
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
	
	public void addSession(OAuthResponse sessionInfo) {
		sessions.add(sessionInfo);
	}
	
	public OAuthResponse getLastAddedSession() {
		return sessions.peek();
	}
}
