package moon.odyssey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class EnumTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnumTemplateApplication.class, args);
    }
}
