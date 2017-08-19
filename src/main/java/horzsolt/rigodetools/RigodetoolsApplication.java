package horzsolt.rigodetools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
public class RigodetoolsApplication {

	private static final Logger logger = LoggerFactory.getLogger(RigodetoolsApplication.class);

	public static void main(String[] args) {

		logger.info("Application is starting........");
		ApplicationContext context = SpringApplication.run(RigodetoolsApplication.class, args);
	}

	@Component
	public static class GMailAccount {

		@Value("${gmail.username}")
		private String username;
		@Value("${gmail.password}")
		private String password;

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
	}
}
