package sk.stuba.sdg.isbe;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class IsBeApplication {


	public static void main(String[] args) {
		SpringApplication.run(IsBeApplication.class, args);
	}

	// 	Let's configure additional connector to enable support for both HTTP and HTTPS
	// 	HTTP port
	//	@Value("${http.port}")
	//	private int httpPort;
	//	@Bean
	//	public ServletWebServerFactory servletContainer() {
	//		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
	//		tomcat.addAdditionalTomcatConnectors(createStandardConnector());
	//		return tomcat;
	//	}
	//
	//	private Connector createStandardConnector() {
	//		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
	//		connector.setPort(httpPort);
	//		return connector;
	//	}
}
