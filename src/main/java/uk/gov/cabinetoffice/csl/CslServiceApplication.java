package uk.gov.cabinetoffice.csl;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

import static net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock.InterceptMode.PROXY_METHOD;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableSchedulerLock(interceptMode = PROXY_METHOD, defaultLockAtMostFor = "2h")
@ConfigurationPropertiesScan
@EnableJms
public class CslServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CslServiceApplication.class, args);
    }
}
