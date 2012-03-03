package com.force.api.oauth2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;


/**
 * 
 * See: http://wiki.developerforce.com/page/Digging_Deeper_into_OAuth_2.0_on_Force.com
 * 
 * @author gwester
 */
public class CallbackServlet extends HttpServlet {
	
	private final Logger logger = Logger.getLogger(CallbackServlet.class.getName());
	
	private final HttpClient httpClient = new HttpClient();
	private final Gson gson = new Gson();
	
	private final String URL_PARAM_CODE = "code";
	private final String URL_PARAM_ACTION = "action";
	
	private final String HOSTNAME = System.getenv("HOSTNAME"); 
	private final String CLIENT_ID = System.getenv("CLIENT_ID");
	private final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
	
	private final String HOSTNAME_URL = "https://" + HOSTNAME + "/";
	
	private final String AUTHORIZATION_ENDPOINT = 
			"https://login.salesforce.com/services/oauth2/authorize?" +
			//This is the parameter Salesforce will tack on the next GET request with the access code
		    "response_type=" + URL_PARAM_CODE + 
			//Your application's client identifier (consumer key in Remote Access Detail).
			"&client_id=" + CLIENT_ID +
			//This must match your application's configured callback URL in Salesforce > Setup > Remote Access
			"&redirect_uri=" + HOSTNAME_URL;
	
	private final String TOKEN_ENDPOINT = "https://login.salesforce.com/services/oauth2/token";
	

	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			//if there are no parameters on the URL, we need to send them to Salesforce for authorization
			if(!request.getParameterNames().hasMoreElements()) {
				response.sendRedirect(AUTHORIZATION_ENDPOINT);
			}
			
			//get the parameters
			String code = request.getParameter(URL_PARAM_CODE);
			String action = request.getParameter(URL_PARAM_ACTION);
			
			if(code != null && (!code.isEmpty())) {
				OAuthCallbackGrantRequest requestInfo = new OAuthCallbackGrantRequest(CLIENT_ID, CLIENT_SECRET, code, HOSTNAME_URL);
				
				PostMethod methodExcecutedOnSalesforce = sendOAuthTokenRequest(requestInfo);
				int statusCode = methodExcecutedOnSalesforce.getStatusCode();
				String responseBody = IOUtils.toString(methodExcecutedOnSalesforce.getResponseBodyAsStream());

				//handle errors, log
				logger.log(Level.INFO, "HTTP " + Integer.valueOf(statusCode));
				logger.log(Level.INFO, responseBody);
				if(statusCode >= 400) {
					logger.log(Level.SEVERE, "OAuth request failed.");
					response.sendError(HttpStatus.UNAUTHORIZED_401);
				}
				
				//parse and store the request in our session table
				OAuthResponse authInfo = gson.fromJson(responseBody, OAuthResponse.class);
				
				//set a cookie so we can lookup the authorization code the next time this user comes back
				String cookieValue = SessionService.getInstance().addSessionAndGetNewCookie(authInfo);
				response.addCookie(new Cookie("sid", cookieValue));
				
				response.getWriter().write(getUserInformation(authInfo).toString());
			}
			else if(action != null && (!action.isEmpty())) {
				//code whatever your webapp is supposed to do here
				if(action.equals("showUserInfo")) {
					String cookieValue = null;
					for(Cookie cookie : request.getCookies()) {
						if(cookie.getName().equals("sid")) {
							cookieValue = cookie.getValue();
						}
					}
					if(cookieValue == null || cookieValue.isEmpty()) {
						logger.log(Level.SEVERE, "No cookie for session ID found.");
						response.sendError(HttpStatus.UNAUTHORIZED_401);
					} 
					else { //authorized
						OAuthResponse authInfo = SessionService.getInstance().getAuthorizationInfo(cookieValue);
						response.getWriter().write(getUserInformation(authInfo).toString());
					}
				}
			}
			
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
		
		/*
		String sessionId = SessionService.getInstance().getLastAddedSession().getAccess_token();
		try {
			response.getWriter().write(sessionId);
			response.setStatus(HttpStatus.OK_200);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
		*/
	}
	
	
	/**
	 * This makes an outbound HTTP request to Salesforce's auth endpoint to get a real session ID.
	 * @param requestInfo
	 * @return responseBody
	 * @throws HttpException
	 * @throws IOException
	 */
	private PostMethod sendOAuthTokenRequest(OAuthCallbackGrantRequest requestInfo) throws HttpException, IOException {
		final PostMethod method = new PostMethod(TOKEN_ENDPOINT);
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		method.setParameter("grant_type", requestInfo.getGrantType());
		method.setParameter("client_id", requestInfo.getConsumerKey());
		method.setParameter("client_secret", requestInfo.getConsumerSecret());
		method.setParameter("code", requestInfo.getCode());
		method.setParameter("redirect_uri", requestInfo.getRedirectUri());
		
		//send the request
		httpClient.executeMethod(method);
		return method;
	}
	
	/**
	 * This makes an outbound HTTP request to Salesforce's oauth servlet, 
	 * with a valid user session ID, to get in depth info about a user.
	 * 
	 * @param authInfo
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	private UserInformation getUserInformation(OAuthResponse authInfo) throws HttpException, IOException {
		final GetMethod method = new GetMethod(authInfo.getId());
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		method.setRequestHeader("Authorization", "OAuth " + authInfo.getAccessToken());

		int statusCode = httpClient.executeMethod(method);
		String responseBody = IOUtils.toString(method.getResponseBodyAsStream());
		if(statusCode >= HttpStatus.BAD_REQUEST_400) {
			logger.log(Level.SEVERE, "Could not get user information: " + responseBody);
		}
		return gson.fromJson(responseBody, UserInformation.class);
	}
}
