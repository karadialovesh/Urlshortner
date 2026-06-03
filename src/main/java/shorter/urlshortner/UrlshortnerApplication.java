package shorter.urlshortner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import shorter.urlshortner.config.SecretsManagerConfig;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class UrlshortnerApplication {

	public static void main(String[] args) {
		//SpringApplication.run(UrlshortnerApplication.class, args);
		SpringApplication app =
				new SpringApplication(UrlshortnerApplication.class);
		app.addInitializers(new SecretsManagerConfig());
		app.run(args);
	}

}
