package com.force.api.oauth2;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * This class contains an embedded web container that
 * allows a web app to easily run on Heroku without
 * being packaged as a war and then deployed on top 
 * of Tomcat, JBoss, etc.  This is a great architecture
 * that unifies the development, testing and production
 * environments.
 * 
 * See also: http://www.12factor.net/
 * See also: http://devcenter.heroku.com/articles/java
 * 
 * @author gwester
 */
public class HerokuApp {
	
	private static final Logger logger = Logger.getLogger(HerokuApp.class.getName());
	
	public static void main(String[] args) throws Exception {
		//set port
		String port = System.getenv("PORT");
		if(port == null || port.isEmpty()) {
			port = "8081";
		}
		logger.log(Level.INFO, "Will bind to port " + port);
		
		//this app requires an OAuth client id
		String clientId = System.getenv("CLIENT_ID");
		if(clientId == null || clientId.isEmpty()) {
			logger.log(Level.SEVERE, "Environment variable CLIENT_ID not set!");
			System.exit(0);
		}
		
		//this app requires an OAuth client secret
		String clientSecret = System.getenv("CLIENT_SECRET");
		if(clientSecret == null || clientSecret.isEmpty()) {
			logger.log(Level.SEVERE, "Environment variable CLIENT_SECRET not set!");
			System.exit(0);
		}

		Server server = new Server(Integer.valueOf(port));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        //define our servlet endpoint
        server.setHandler(context);
        context.addServlet(new ServletHolder(new CallbackServlet()),"/*");
        
        //start the server
        server.start();
        server.join();  
	}
}
