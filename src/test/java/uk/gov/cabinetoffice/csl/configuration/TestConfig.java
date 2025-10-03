package uk.gov.cabinetoffice.csl.configuration;

import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.*;

@TestConfiguration
@Import({MockClockConfig.class, MockJmsConfig.class})
public class TestConfig {
    @Bean
    @Primary
    public RedissonClient redissonClient() {
        return mock(RedissonClient.class);
    }
}
