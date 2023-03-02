package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.invokeService;

@Service
public class LearnerRecordService {

    private final RequestEntityWithBearerAuthFactory requestEntityFactory;

    @Value("${learnerRecord.courseRecordsForLearnerUrl}")
    private String courseRecordsForLearnerUrl;

    @Value("${learnerRecord.moduleRecordsForLearnerUrl}")
    private String moduleRecordsForLearnerUrl;

    public LearnerRecordService(RequestEntityWithBearerAuthFactory requestEntityFactory) {
        this.requestEntityFactory = requestEntityFactory;
    }

    public ResponseEntity<?> getCourseRecordForLearner(String learnerId, String courseId) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createGetRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                null);
        return invokeService(requestWithBearerAuth);
    }

    public ResponseEntity<?> createCourseRecordForLearner(CourseRecordInput courseRecordInput) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                courseRecordsForLearnerUrl, courseRecordInput, null);
        return invokeService(requestWithBearerAuth);
    }

    public ResponseEntity<?> updateCourseRecordForLearner(String learnerId, String courseId,
                                                          Map<String, String> updateFields) {
        List<PatchOp> jsonPatch = new ArrayList<>();
        updateFields.forEach((key, value) -> jsonPatch.add(new PatchOp("replace", "/" + key, value)));

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                courseRecordsForLearnerUrl + "?userId=" + learnerId + "&courseId=" + courseId,
                jsonPatch, null);
        return invokeService(requestWithBearerAuth);
    }

    public ResponseEntity<?> createModuleRecordForLearner(ModuleRecordInput moduleRecordInput) {
        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPostRequestWithBearerAuth(
                moduleRecordsForLearnerUrl, moduleRecordInput, null);
        return invokeService(requestWithBearerAuth);
    }

    public ResponseEntity<?> updateModuleRecordForLearner(Long moduleRecordId,
                                                          Map<String, String> updateFields) {
        List<PatchOp> jsonPatch = new ArrayList<>();
        updateFields.forEach((key, value) -> jsonPatch.add(new PatchOp("replace", "/" + key, value)));

        RequestEntity<?> requestWithBearerAuth = requestEntityFactory.createPatchRequestWithBearerAuth(
                moduleRecordsForLearnerUrl + "/" + moduleRecordId, jsonPatch, null);
        return invokeService(requestWithBearerAuth);
    }
}
