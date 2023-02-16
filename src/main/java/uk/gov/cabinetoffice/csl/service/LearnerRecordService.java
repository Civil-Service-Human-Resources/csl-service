package uk.gov.cabinetoffice.csl.service;

import jakarta.json.Json;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonStructure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.domain.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

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
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createGetRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                null);
        ResponseEntity<?> response = null;
        try {
            response = restTemplate.exchange(requestWithBearerAuth, CourseRecords.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                CourseRecords courseRecords = (CourseRecords)response.getBody();
                assert courseRecords != null;
                courseRecords.getCourseRecords().forEach(c -> log.debug("Course Title: {}", c.getCourseTitle()));
            }
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, requestWithBearerAuth.getUrl().getPath());
        }
        return response;
    }

    public ResponseEntity<?> createCourseRecordForLearner(CourseRecordInput courseRecordInput) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                courseRecordsForLearnerUrl, courseRecordInput, null);
        return courseRecordForLearner(requestWithBearerAuth);
    }

    public ResponseEntity<?> updateCourseRecordForLearner(String learnerId, String courseId) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();
        //jsonPatchBuilder = jsonPatchBuilder.replace("/lastUpdated", LocalDateTime.now().toString());
        jsonPatchBuilder = jsonPatchBuilder.replace("/state", State.COMPLETED.toString());
        JsonPatch jsonPatch = jsonPatchBuilder.build();

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                jsonPatch, null);
        return courseRecordForLearner(requestWithBearerAuth);
    }

    private ResponseEntity<?> courseRecordForLearner(RequestEntity<?> requestWithBearerAuth) {
        ResponseEntity<?> response = null;
        try {
            //response = restTemplate.exchange(requestWithBearerAuth, CourseRecord.class);
            response = restTemplate.exchange(requestWithBearerAuth, JsonStructure.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                CourseRecord courseRecord = (CourseRecord)response.getBody();
                assert courseRecord != null;
                log.debug("Course Title: {}", courseRecord.getCourseTitle());
            }
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, requestWithBearerAuth.getUrl().getPath());
        }
        return response;
    }

    public ResponseEntity<?> createModuleRecordForLearner(ModuleRecordInput moduleRecordInput) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                moduleRecordsForLearnerUrl, moduleRecordInput, null);
        return moduleRecordForLearner(requestWithBearerAuth);
    }

    public ResponseEntity<?> updateModuleRecordForLearner(Long moduleRecordId, Map<String, String> updateValues) {
        JsonPatchBuilder jsonPatchBuilder = Json.createPatchBuilder();

        jsonPatchBuilder = jsonPatchBuilder.replace("/postalCode", "500072");
        jsonPatchBuilder = jsonPatchBuilder.replace("/postalCode", 500072);

        JsonPatch jsonPatch = jsonPatchBuilder.build();

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                moduleRecordsForLearnerUrl + "/" + moduleRecordId, jsonPatch, null);
        return moduleRecordForLearner(requestWithBearerAuth);
    }

//    public ResponseEntity<?> updateModuleRecordForLearner1(Long moduleRecordId, PatchModuleRecordInput patchModuleRecordInput) {
//        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
//                moduleRecordsForLearnerUrl + "/" + moduleRecordId, patchModuleRecordInput, null);
//        return moduleRecordForLearner(requestWithBearerAuth);
//    }

    private ResponseEntity<?> moduleRecordForLearner(RequestEntity<?> requestWithBearerAuth) {
        ResponseEntity<?> response = null;
        try {
            response = restTemplate.exchange(requestWithBearerAuth, ModuleRecord.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                ModuleRecord moduleRecord = (ModuleRecord)response.getBody();
                assert moduleRecord != null;
                log.debug("ModuleRecord Title: {}", moduleRecord.getModuleTitle());
            }
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, requestWithBearerAuth.getUrl().getPath());
        }
        return response;
    }
}
