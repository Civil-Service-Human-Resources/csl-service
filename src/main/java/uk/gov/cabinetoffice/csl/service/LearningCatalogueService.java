package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.factory.RequestEntityWithBearerAuthFactory;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.invokeService;

@Slf4j
@Service
public class LearningCatalogueService {

    private final RequestEntityWithBearerAuthFactory requestEntityFactory;

    @Value("${learningCatalogue.courseUrl}")
    private String courseUrl;

    public LearningCatalogueService(RequestEntityWithBearerAuthFactory requestEntityFactory) {
        this.requestEntityFactory = requestEntityFactory;
    }

    public ResponseEntity<?> getCourse(String courseId) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createGetRequestWithBearerAuth(
                String.format(courseUrl, courseId), null);
        return invokeService(requestWithBearerAuth);
    }
}
