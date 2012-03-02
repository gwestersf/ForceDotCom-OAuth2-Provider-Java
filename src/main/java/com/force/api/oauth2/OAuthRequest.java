package com.force.api.oauth2;

/**
 * 
 * @author gwester
 */
public abstract class OAuthRequest {
	
	//also known as 'consumer key' in Salesforce documentation
	private String client_id;		
	
	/**
	 * @return Also known as 'client id' in OAuth2
	 */
	public String getConsumerKey() {
		return client_id;
	}
	
	/**
	 * @param consumerKey Also known as 'client id' in OAuth2
	 */
	protected void setConsumerKey(String client_id) {
		this.client_id = client_id;
	}
	
	/**
	 * All child classes must define a grant type, like:
	 * 
	 * authorization_code   //callbacks
	 * password             //username+password flow
	 * refresh_token        //trade in an expired session for another session
	 * client_credentials   //not supported at Salesforce
	 * 
	 * Please see the OAuth2 specification for complete details:
     * http://tools.ietf.org/html/draft-ietf-oauth-v2
	 * 
	 * @return
	 */
	public abstract String getGrantType();
	
}
