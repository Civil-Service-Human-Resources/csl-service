package uk.gov.cabinetoffice.csl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CslServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CslServiceApplication.class, args);
	}
}
