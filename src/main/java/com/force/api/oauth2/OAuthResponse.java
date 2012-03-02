package com.force.api.oauth2;

import java.net.MalformedURLException;

/**
 * These are the fields Salesforce returns with an OAuth2 response.
 * 
 * Please see the OAuth2 specification for complete details:
 * http://tools.ietf.org/html/draft-ietf-oauth-v2
 * 
 * @author gwester
 */
public class OAuthResponse {
	private String id;
	private String access_token;
	private String instance_url;
	private String issued_at;
	private String signature;
	
    /**
     * The 'id' field actually contains a URL that contains data about the user in this org.
     * Follow this URL to learn about the user.
     * @return
     * @throws MalformedURLException
     */
	public java.net.URL getUserInfo() throws MalformedURLException {
		return new java.net.URL(id);
	}
	
	public String getId() {
		return id;
	}
	public String getAccess_token() {
		return access_token;
	}
	public String getInstance_url() {
		return instance_url;
	}
	public String getIssued_at() {
		return issued_at;
	}
	public String getSignature() {
		return signature;
	}
	
	/*
	 * These setters should not be used as these are responses from the API.
	 * The JSON deserializer uses reflection to set these fields.
	 */
	
	protected void setId(String id) {
		this.id = id;
	}
	protected void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	protected void setInstance_url(String instance_url) {
		this.instance_url = instance_url;
	}
	protected void setIssued_at(String issued_at) {
		this.issued_at = issued_at;
	}
	protected void setSignature(String signature) {
		this.signature = signature;
	}
}
