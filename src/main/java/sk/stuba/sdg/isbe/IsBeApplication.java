package sk.stuba.sdg.isbe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sk.stuba.sdg.isbe.controllers.User;
import sk.stuba.sdg.isbe.controllers.UserRepository;

@SpringBootApplication
public class IsBeApplication {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(IsBeApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
			userRepository.save(new User("pqowie"));
		};
	}

}
