package com.force.api.oauth2.persistence;

import com.force.api.oauth2.model.OAuthResponse;

/**
 * TODO: persist up this service with mongo db
 * 
 * This class holds recent sessions.  If you scale this web application beyond a single node, you'll need 
 * to put these sessions in a data store.  A request can land on any node (or web worker, or JVM, or whatever).
 * 
 * I would recommend MongoDB as a Heroku Add-On.  You would key the document database on cookie value, 
 * and get back the session info as a document.  Mongo serializes the documents in BSON, a binary permutation 
 * of a JSON-like format.
 * 
 * @author gwester
 */
public class MongoSessionService implements SessionService {

	@Override
	public String addSessionAndGetNewCookie(OAuthResponse sessionInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OAuthResponse getAuthorizationInfo(String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
