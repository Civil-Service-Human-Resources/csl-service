package uk.gov.cabinetoffice.csl.config.jobs;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@ConditionalOnProperty(
        prefix = "spring.cache",
        name = {"type"},
        havingValue = "redis",
        matchIfMissing = true
)
public class ScheduledJobConfig {

    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory);
    }

}
