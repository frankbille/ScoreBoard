package dk.frankbille.scoreboard;

import org.apache.wicket.RuntimeConfigurationType;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.xml.XmlConfiguration;

public class Start {
    public static void main(String[] args) throws Exception {
    	System.setProperty("wicket.configuration", RuntimeConfigurationType.DEVELOPMENT.name());

		Server server = new Server(8080);

		WebAppContext webAppContext = new WebAppContext("src/main/webapp", "/");
		webAppContext.setConfigurationClasses(new String[]{
				WebInfConfiguration.class.getName(),
				WebXmlConfiguration.class.getName(),
				MetaInfConfiguration.class.getName(),
				FragmentConfiguration.class.getName(),
				EnvConfiguration.class.getName(),
				PlusConfiguration.class.getName(),
				JettyWebXmlConfiguration.class.getName()
		});
		XmlConfiguration configuration = new XmlConfiguration(Start.class.getResourceAsStream("/jetty-env.xml"));
		configuration.configure(webAppContext);

		server.setHandler(webAppContext);

		server.start();
		server.join();
    }
}
