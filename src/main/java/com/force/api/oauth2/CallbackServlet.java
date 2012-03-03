package com.force.api.oauth2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private final Gson gson = new Gson();
	
	private static final long serialVersionUID = 1L;
	

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			//we don't want just anyone POST-ing here; this endpoint is only for Salesforce to use
			if(!request.getRemoteHost().contains("salesforce.com")) {
				logger.log(Level.WARNING, request.getRemoteHost());
				response.sendError(HttpStatus.UNAUTHORIZED_401);
			}
			
			//read the request
			String requestBody = IOUtils.toString(request.getInputStream());
			logger.log(Level.INFO, requestBody);
			
			//parse and store the request in our session table
			OAuthResponse sessionInfo = gson.fromJson(requestBody, OAuthResponse.class);
			SessionService.getInstance().addSession(sessionInfo);

			//send response
			response.setStatus(HttpStatus.NO_CONTENT_204);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = SessionService.getInstance().getLastAddedSession().getAccess_token();
		try {
			response.getWriter().write(sessionId);
			response.setStatus(HttpStatus.OK_200);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}
}
