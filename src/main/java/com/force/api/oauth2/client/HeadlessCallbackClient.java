package com.force.api.oauth2.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.force.api.oauth2.model.OAuthResponse;
import com.google.gson.Gson;

/**
 * This class connects to a callback servlet the way that a browser would,
 * following redirects.  Ultimately you'll get a session ID for supplying
 * a username and password.  Unlike the username and password flow, you
 * don't need the consumer key (client id) or consumer secret.  They are
 * safely stored on the server.
 * 
 * Optionally, you could supply your own consumer key in which case you'd be 
 * able to connect to any organization.  You'd have to set up the servlet to
 * accept and allow that.
 * 
 * @author gwester
 */
public class HeadlessCallbackClient implements SessionInitializer {

	private final Logger logger = Logger.getLogger(HeadlessCallbackClient.class.getName());
	
	private final String applicationUrl;
	
	private final HttpClient httpClient;
	private final Gson gson;
	
	/**
	 * 
	 * @param applicationUrl Something like https://your-oauth-app.herokuapp.com/myServletMapping/
	 * @param consumerKey this is sometimes called 'client id'
	 */
	public HeadlessCallbackClient(String applicationUrl) {
		this.applicationUrl = applicationUrl;
		//this.consumerKey = consumerKey;
		
		//utilities
		this.httpClient = new HttpClient();
		this.gson = new Gson();
	}

	@Override
	public OAuthResponse getSession(String username, String password) throws Exception {
		GetMethod methodGetCallbackEndpoint = new GetMethod(applicationUrl);
		methodGetCallbackEndpoint.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		methodGetCallbackEndpoint.setFollowRedirects(true);		//we will get redirected to SFDC
		
		int getReqStatusCode = httpClient.executeMethod(methodGetCallbackEndpoint);
		if(getReqStatusCode >= HttpStatus.BAD_REQUEST_400) {
			logger.log(Level.SEVERE, "GET request to you app server did not work: " + 
					IOUtils.toString(methodGetCallbackEndpoint.getResponseBodyAsStream()));
		}
		
		//get the redirect URI.  log it so we can debug
		String loginUri = methodGetCallbackEndpoint.getURI().toString();
		logger.log(Level.FINE, loginUri);
		
		//post username and password to salesforce
		PostMethod methodPostCredentials = new PostMethod(loginUri);
		methodPostCredentials.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		methodPostCredentials.setFollowRedirects(true);		//we should get redirected back to our app
		
		//set body
		NameValuePair[] postBody = new NameValuePair[] {
				new NameValuePair("username", username), 
				new NameValuePair("password", password) 	//this might be 'pw' instead of 'password'
				};
		methodPostCredentials.addParameters(postBody);
		
		httpClient.executeMethod(methodPostCredentials);
		int postReqStatusCode = httpClient.executeMethod(methodGetCallbackEndpoint);
		if(postReqStatusCode >= HttpStatus.BAD_REQUEST_400) {
			logger.log(Level.SEVERE, "POST request to Salesforce login did not work: " + 
					IOUtils.toString(methodGetCallbackEndpoint.getResponseBodyAsStream()));
		}
		
		//get the new redirect URI. this should have a parameter on it called "code"
		String appUri = methodPostCredentials.getURI().toString();
		logger.log(Level.FINE, appUri);
		
		
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length != 3) {
			throw new IllegalArgumentException("Pass in: application URL, username, password");
		}
		
		HeadlessCallbackClient client = new HeadlessCallbackClient(args[0]);
		client.getSession(args[1], args[2]);
	}

}
