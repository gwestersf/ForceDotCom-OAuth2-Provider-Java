package com.force.api.oauth2;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

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

	public static void main(String[] args) throws Exception {
		String webPort = System.getenv("PORT");
		String webappDirLocation = "src/main/webapp/";

		if(webPort == null || webPort.isEmpty()) {
			webPort = "8081";
		}

		Server server = new Server(Integer.valueOf(webPort));
		WebAppContext root = new WebAppContext();
		root.setContextPath("/");
		root.setDescriptor(webappDirLocation+"/WEB-INF/web.xml");
		root.setResourceBase(webappDirLocation);
		root.setParentLoaderPriority(true);

		server.setHandler(root);
		server.start();
		server.join(); 
	}
}
