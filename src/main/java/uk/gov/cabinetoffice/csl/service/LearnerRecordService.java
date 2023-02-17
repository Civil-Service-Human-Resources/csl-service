package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

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
        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(requestWithBearerAuth, CourseRecords.class);
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

    public ResponseEntity<?> updateCourseRecordForLearner(String learnerId, String courseId,
                                                          Map<String, String> updateFields) {
        List<PatchOp> jsonPatch = new ArrayList<>();
        jsonPatch.add(new PatchOp("replace", "/lastUpdated", LocalDateTime.now().toString()));
        updateFields.forEach((key, value) ->
                jsonPatch.add(new PatchOp("replace", "/" + key, value)));

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                jsonPatch, null);
        return courseRecordForLearner(requestWithBearerAuth);
    }

    private ResponseEntity<?> courseRecordForLearner(RequestEntity<?> requestWithBearerAuth) {
        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(requestWithBearerAuth, CourseRecord.class);
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

    public ResponseEntity<?> updateModuleRecordForLearner(Long moduleRecordId,
                                                          Map<String, String> updateFields) {
        List<PatchOp> jsonPatch = new ArrayList<>();
        jsonPatch.add(new PatchOp("replace", "/updatedAt", LocalDateTime.now().toString()));
        updateFields.forEach((key, value) ->
                jsonPatch.add(new PatchOp("replace", "/" + key, value)));

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                moduleRecordsForLearnerUrl + "/" + moduleRecordId, jsonPatch, null);
        return moduleRecordForLearner(requestWithBearerAuth);
    }

    private ResponseEntity<?> moduleRecordForLearner(RequestEntity<?> requestWithBearerAuth) {
        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(requestWithBearerAuth, ModuleRecord.class);
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, requestWithBearerAuth.getUrl().getPath());
        }
        return response;
    }
}
