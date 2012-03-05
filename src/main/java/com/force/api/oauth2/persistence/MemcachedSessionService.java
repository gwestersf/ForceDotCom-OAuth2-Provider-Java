package com.force.api.oauth2.persistence;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import com.force.api.oauth2.model.OAuthResponse;
import com.google.common.collect.Lists;

/**
 * TODO: persist up this service with memcached
 * 
 * This class holds recent sessions.  If you scale this web application beyond a single node, you'll need 
 * to put these sessions in a data store.  A request can land on any node (or web worker, or JVM, or whatever).
 * 
 * @author gwester
 */
public class MemcachedSessionService implements SessionService {
	
	private MemcachedClient client;
	
	private static MemcachedSessionService SINGLETON;
	
	private MemcachedSessionService() throws IOException {
		client = new MemcachedClient(new BinaryConnectionFactory(), 
				Lists.<InetSocketAddress>newArrayList(new InetSocketAddress("0.0.0.0", 11211)));
	}
	
	public static MemcachedSessionService getInstance() throws IOException {
		if(SINGLETON == null) {
			//double check locking
			synchronized(MemcachedSessionService.class) {
				if(SINGLETON == null) {
					SINGLETON = new MemcachedSessionService();
				}
			}
		}
		return SINGLETON;
	}

	@Override
	public String addSessionAndGetNewCookie(OAuthResponse sessionInfo) {
		String cookieValue = UUID.randomUUID().toString();
		client.add(cookieValue, 60 * 60 * 2, sessionInfo);
		return cookieValue;
	}

	@Override
	public OAuthResponse getAuthorizationInfo(String cookie) {
		return (OAuthResponse)client.get(cookie);
	}

	
}
