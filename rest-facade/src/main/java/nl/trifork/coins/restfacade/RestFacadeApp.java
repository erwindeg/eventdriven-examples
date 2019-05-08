package nl.trifork.coins.restfacade;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RestFacadeApp {
    public static void main(String[] args) {
        SpringApplication.run(RestFacadeApp.class, args);
    }
}