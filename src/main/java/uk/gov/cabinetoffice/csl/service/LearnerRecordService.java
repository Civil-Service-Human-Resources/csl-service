package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.CourseRecords;

@Slf4j
@Service
public class LearnerRecordService {

    private final RequestEntityFactory requestEntityFactory;

    private final String courseRecordsForLearnerUrl;

    private final RestTemplate restTemplate;

    public LearnerRecordService(RequestEntityFactory requestEntityFactory, RestTemplate restTemplate,
        @Value("${learnerRecord.courseRecordsForLearnerUrl}") String courseRecordsForLearnerUrl) {
        this.requestEntityFactory = requestEntityFactory;
        this.restTemplate = restTemplate;
        this.courseRecordsForLearnerUrl = courseRecordsForLearnerUrl;
    }

    public ResponseEntity<?> getCourseRecordForLearner(String learnerId, String courseId) {
        RequestEntity<?> getRequestWithBearerAuth = requestEntityFactory.createGetRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId, null);
        ResponseEntity<CourseRecords> response = restTemplate.exchange(getRequestWithBearerAuth, CourseRecords.class);
        if(response.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords = response.getBody();
            assert courseRecords != null;
            courseRecords.getCourseRecords().forEach(c -> log.debug("Course Title: {}", c.getCourseTitle()));
        }
        return response;
    }
}
