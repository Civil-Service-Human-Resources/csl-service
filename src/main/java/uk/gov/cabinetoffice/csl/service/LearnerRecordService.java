package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.*;

import static uk.gov.cabinetoffice.csl.CslServiceUtil.returnError;

@Slf4j
@Service
public class LearnerRecordService {

    private final RequestEntityFactory requestEntityFactory;

    private final String courseRecordsForLearnerUrl;

    private final String moduleRecordsForLearnerUrl;

    private final RestTemplate restTemplate;

    public LearnerRecordService(RequestEntityFactory requestEntityFactory, RestTemplate restTemplate,
        @Value("${learnerRecord.courseRecordsForLearnerUrl}") String courseRecordsForLearnerUrl,
        @Value("${learnerRecord.moduleRecordsForLearnerUrl}") String moduleRecordsForLearnerUrl) {
        this.requestEntityFactory = requestEntityFactory;
        this.restTemplate = restTemplate;
        this.courseRecordsForLearnerUrl = courseRecordsForLearnerUrl;
        this.moduleRecordsForLearnerUrl = moduleRecordsForLearnerUrl;
    }

    public ResponseEntity<?> getCourseRecordForLearner(String learnerId, String courseId) {
        RequestEntity<?> getRequestWithBearerAuth = requestEntityFactory.createGetRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                null);
        ResponseEntity<?> response = null;
        try {
            response = restTemplate.exchange(getRequestWithBearerAuth, CourseRecords.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                CourseRecords courseRecords = (CourseRecords)response.getBody();
                assert courseRecords != null;
                courseRecords.getCourseRecords().forEach(c -> log.debug("Course Title: {}", c.getCourseTitle()));
            }
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, getRequestWithBearerAuth.getUrl().getPath());
        }
        return response;
    }

    public ResponseEntity<?> createCourseRecordForLearner(CourseRecordInput courseRecordInput) {
        RequestEntity<?> postRequestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                courseRecordsForLearnerUrl, courseRecordInput, null);

        ResponseEntity<?> response = null;
        try {
            response = restTemplate.exchange(postRequestWithBearerAuth, CourseRecord.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                CourseRecord courseRecord = (CourseRecord)response.getBody();
                assert courseRecord != null;
                log.debug("Course Title: {}", courseRecord.getCourseTitle());
            }
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, postRequestWithBearerAuth.getUrl().getPath());
        }
        return response;
    }

    public ResponseEntity<?> createModuleRecordForLearner(ModuleRecordInput moduleRecordInput) {
        RequestEntity<?> postRequestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                moduleRecordsForLearnerUrl, moduleRecordInput, null);

        ResponseEntity<?> response = null;
        try {
            response = restTemplate.exchange(postRequestWithBearerAuth, ModuleRecord.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                ModuleRecord moduleRecord = (ModuleRecord)response.getBody();
                assert moduleRecord != null;
                log.debug("ModuleRecord Title: {}", moduleRecord.getModuleTitle());
            }
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, postRequestWithBearerAuth.getUrl().getPath());
        }
        return response;
    }
}
