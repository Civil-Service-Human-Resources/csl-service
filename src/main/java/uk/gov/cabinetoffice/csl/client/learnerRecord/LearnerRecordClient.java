package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LearnerRecordClient implements ILearnerRecordClient {

    @Value("${learnerRecord.courseRecordsForLearnerUrl}")
    private String courseRecords;
    @Value("${learnerRecord.moduleRecordsForLearnerUrl}")
    private String moduleRecords;

    private final IHttpClient httpClient;

    public LearnerRecordClient(@Qualifier("learnerRecordHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CourseRecords getCourseRecord(String userId, String courseId) {
        try {
            return getCourseRecords(userId, Collections.singletonList(courseId));
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                log.warn("Course record with userID '{}' and courseId '{}' was not found.", userId, courseId);
                return null;
            }
            throw e;
        }
    }

    @Override
    public CourseRecords getCourseRecords(String userId, List<String> courseIds) {
        log.debug("Getting course records with ids '{}' for user '{}'", courseIds, userId);
        String courseIdList = String.join(",", courseIds);
        String url = String.format("%s?userId=%s&courseId=%s", courseRecords, userId, courseIdList);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, CourseRecords.class);
    }

    @Override
    public CourseRecord createCourseRecord(CourseRecordInput body) {
        log.debug("Creating course record '{}'", body);
        RequestEntity<CourseRecordInput> request = RequestEntity.post(courseRecords).body(body);
        return httpClient.executeRequest(request, CourseRecord.class);
    }

    @Override
    public ModuleRecord createModuleRecord(ModuleRecordInput body) {
        log.debug("Creating module record '{}'", body);
        RequestEntity<ModuleRecordInput> request = RequestEntity.post(moduleRecords).body(body);
        return httpClient.executeRequest(request, ModuleRecord.class);
    }

    @Override
    public CourseRecord updateCourseRecord(String userId, String courseId, List<PatchOp> patches) {
        log.debug("Updating course record for user '{}' and course '{}' with patches '{}'", userId, courseId, patches);
        String url = String.format("%s?userId=%s&courseId=%s", courseRecords, userId, courseId);
        RequestEntity<List<PatchOp>> request = RequestEntity
                .patch(url).headers(httpHeaders -> httpHeaders.add("Content-Type", "application/json-patch+json"))
                .body(patches);
        return httpClient.executeRequest(request, CourseRecord.class);
    }

    @Override
    public ModuleRecord updateModuleRecord(Long moduleRecordId, List<PatchOp> patches) {
        log.debug("Updating module record with ID '{}' with patches '{}'", moduleRecordId, patches);
        String url = String.format("%s/%s", moduleRecords, moduleRecordId);
        RequestEntity<List<PatchOp>> request = RequestEntity
                .patch(url).headers(httpHeaders -> httpHeaders.add("Content-Type", "application/json-patch+json"))
                .body(patches);
        return httpClient.executeRequest(request, ModuleRecord.class);
    }
}
