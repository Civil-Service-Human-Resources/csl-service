package uk.gov.cabinetoffice.csl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
public class CslServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CslServiceApplication.class, args);
	}

}
