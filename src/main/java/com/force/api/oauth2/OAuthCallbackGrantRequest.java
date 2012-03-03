package com.force.api.oauth2;

import org.apache.commons.httpclient.NameValuePair;

/**
 * 
 * @author gwester
 */
public class OAuthCallbackGrantRequest extends OAuthRequest {
	
	private final String grant_type = "authorization_code";

	@Override
	public String getGrantType() {
		return grant_type;
	}

}
