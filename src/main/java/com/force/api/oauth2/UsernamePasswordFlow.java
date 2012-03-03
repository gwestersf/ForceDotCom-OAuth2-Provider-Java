package com.force.api.oauth2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

/**
 * This class executes the OAuth2 username and password flow for Salesforce.
 * 
 * Please see: https://login.salesforce.com/help/doc/en/remoteaccess_oauth_username_password_flow.htm
 * 
 * @author gwester
 */
public class UsernamePasswordFlow {
	
	private final Logger logger = Logger.getLogger(UsernamePasswordFlow.class.getName());
	
	private final String consumerKey;
	private final String consumerSecret;
	
	private final String path;
	
	private final HttpClient httpClient;
	private final Gson gson;
	
	/**
	 * 
	 * @param hostname A hostname like 'na12.salesforce.com' or 'ap1.salesforce.com'
	 * @param consumerKey this is sometimes called 'client id'
	 * @param consumerSecret this is sometimes called 'client secret'
	 */
	public UsernamePasswordFlow(String hostname, String consumerKey, String consumerSecret) {
		this.path = "https://" + hostname + "/services/oauth2/token";
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		
		//utilities
		this.httpClient = new HttpClient();
		this.gson = new Gson();
	}
	
	public OAuthResponse getSession(String username, String password) throws HttpException, IOException {
		//set up the request object
		final OAuthPasswordGrantRequest request = new OAuthPasswordGrantRequest();
		request.setConsumerKey(consumerKey);
		request.setConsumerSecret(consumerSecret);
		request.setUsername(username);
		request.setPassword(password);
		
		//serialize the request, deserialize the response
		String responseBody = sendOAuthRequest(request);
		return gson.fromJson(responseBody, OAuthResponse.class);
	}
	
	/**
	 * 
	 * @param requestBody
	 * @return responseBody
	 * @throws HttpException
	 * @throws IOException
	 */
	private String sendOAuthRequest(OAuthPasswordGrantRequest requestBody) throws HttpException, IOException {
		final PostMethod method = new PostMethod(path);
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		method.setParameter("grant_type", requestBody.getGrantType());
		method.setParameter("client_id", requestBody.getConsumerKey());
		method.setParameter("client_secret", requestBody.getConsumerSecret());
		method.setParameter("username", requestBody.getUsername());
		method.setParameter("password", requestBody.getPassword());
		
		//send the request
		int statusCode = httpClient.executeMethod(method);
		String responseBody = IOUtils.toString(method.getResponseBodyAsStream());

		//handle errors, log
		logger.log(Level.INFO, "HTTP " + Integer.valueOf(statusCode));
		logger.log(Level.INFO, responseBody);
		if(statusCode >= 400) {
			logger.log(Level.SEVERE, "OAuth request failed.");
			return "";
		}
		return responseBody;
	}
}
