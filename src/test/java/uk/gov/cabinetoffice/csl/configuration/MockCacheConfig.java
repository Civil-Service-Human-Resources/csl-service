package uk.gov.cabinetoffice.csl.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.gov.cabinetoffice.csl.config.redis.ITtlCache;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TtlObjectCache;

import static org.mockito.Mockito.mock;

@Configuration
public class MockCacheConfig {

    @Bean
    @Primary
    public TtlObjectCache<Course> mockCourseCatalogueCache() {
        ITtlCache cache = mock(ITtlCache.class);
        return new TtlObjectCache<>(cache, Course.class);
    }

}
