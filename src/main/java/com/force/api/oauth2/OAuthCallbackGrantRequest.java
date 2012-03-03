package com.force.api.oauth2;


/**
 * This class that captures the fields needed for the callback OAuth flow.
 * 
 * See: http://wiki.developerforce.com/page/Digging_Deeper_into_OAuth_2.0_on_Force.com
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
