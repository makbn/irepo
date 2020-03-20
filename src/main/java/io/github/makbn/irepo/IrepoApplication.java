package io.github.makbn.irepo;

import io.github.makbn.irepo.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class IrepoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IrepoApplication.class, args);

	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}

}
