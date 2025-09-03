package uk.gov.cabinetoffice.csl.client.frontend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;

import static java.lang.String.format;

@Component
@Slf4j
public class FrontendClient implements IFrontendClient {

    private final IHttpClient client;

    private final String clearLearningCacheEndpoint;

    public FrontendClient(@Qualifier("lpgUiClient") IHttpClient client, @Value("${ui.lpg.clearLearningEndpoint}") String clearLearningCacheEndpoint) {
        this.client = client;
        this.clearLearningCacheEndpoint = clearLearningCacheEndpoint;
    }

    @Override
    public void clearLearningCaches(String uid, String courseId) {
        log.info("Clearing learning caches for user {} and course {}", uid, courseId);
        String url = format(clearLearningCacheEndpoint, uid, courseId);
        RequestEntity<Void> request = RequestEntity.post(url).build();
        client.executeRequest(request, Void.class);
    }
}
