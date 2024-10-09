package uk.gov.cabinetoffice.csl.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MockClockConfig.class, MockJmsConfig.class})
public class TestConfig {

}
