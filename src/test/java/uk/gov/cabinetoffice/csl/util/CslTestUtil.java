package uk.gov.cabinetoffice.csl.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.cabinetoffice.csl.domain.error.ErrorResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.convertObjectToJsonString;

public class CslTestUtil {

    private final LearnerRecordService learnerRecordService;
    private final String learnerId;
    private final String courseId;
    private final String moduleId;
    private final String uid;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime completedAt;

    private final String courseTitle = "courseTitle";
    private final Boolean isRequired = true;
    private final String moduleTitle = "moduleTitle";
    private final String moduleType = "elearning";

    public CslTestUtil(LearnerRecordService learnerRecordService,
                       String learnerId, String courseId, String moduleId, String uid,
                       LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime completedAt) {
        this.learnerRecordService = learnerRecordService;
        this.learnerId = learnerId;
        this.courseId = courseId;
        this.moduleId = moduleId;
        this.uid = uid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public void mockLearnerRecordServiceGetAndUpdateCalls(ResponseEntity responseForCourseRecords,
                                                           ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(responseForCourseRecords);
        when(learnerRecordService.updateModuleRecordForLearner(any(), any())).thenReturn(responseForModuleRecord);
    }

    public void mockLearnerRecordServiceCreateCalls(
            CourseRecordInput courseRecordInput, ResponseEntity responseForCourseRecord,
            ModuleRecordInput moduleRecordInput, ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.createCourseRecordForLearner(courseRecordInput)).thenReturn(responseForCourseRecord);
        when(learnerRecordService.createModuleRecordForLearner(moduleRecordInput)).thenReturn(responseForModuleRecord);
    }

    public void mockLearnerRecordServiceForGetCourseRecord(ResponseEntity responseForCourseRecords) {
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(responseForCourseRecords);
    }

    public void mockLearnerRecordServiceForCreateInProgressCourseRecordWithModuleRecord(
            CourseRecordInput courseRecordInput, CourseRecord courseRecord) {
        when(learnerRecordService.createInProgressCourseRecordWithModuleRecord(courseRecordInput)).thenReturn(courseRecord);
    }

    public void mockLearnerRecordServiceForCreateCourseRecord(
            CourseRecordInput courseRecordInput, ResponseEntity responseForCourseRecord) {
        when(learnerRecordService.createCourseRecordForLearner(courseRecordInput)).thenReturn(responseForCourseRecord);
    }

    public void mockLearnerRecordServiceForCreateModuleRecord(
            ModuleRecordInput moduleRecordInput, ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.createModuleRecordForLearner(moduleRecordInput)).thenReturn(responseForModuleRecord);
    }

    public void mockLearnerRecordServiceForUpdateModuleRecord(ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.updateModuleRecordForLearner(any(), any())).thenReturn(responseForModuleRecord);
    }

    public void mockLearnerRecordServiceForUpdateModuleUpdateDateTime(ModuleRecord moduleRecord) {
        when(learnerRecordService.updateModuleUpdateDateTime(any(), any(), any(), any())).thenReturn(moduleRecord);
    }

    public void mockLearnerRecordServiceForUpdateModuleRecordToAssignUid(ModuleRecord moduleRecord, String learnerId, String courseId) {
        ModuleRecord moduleRecord1 = createModuleRecord();
        when(learnerRecordService.updateModuleRecordToAssignUid(moduleRecord, learnerId, courseId)).thenReturn(moduleRecord1);
    }

    public CourseRecordInput createCourseRecordInput(String learnerId, String courseId, String moduleId) {
        CourseRecordInput courseRecordInput = new CourseRecordInput();
        courseRecordInput.setUserId(learnerId);
        courseRecordInput.setCourseId(courseId);
        courseRecordInput.setCourseTitle(courseTitle);
        courseRecordInput.setState(State.IN_PROGRESS.name());
        courseRecordInput.setIsRequired(isRequired);
        courseRecordInput.setModuleRecords(new ArrayList<>());
        courseRecordInput.getModuleRecords().add(createModuleRecordInput(learnerId, courseId, moduleId));
        return courseRecordInput;
    }

    public ModuleRecordInput createModuleRecordInput(String learnerId, String courseId, String moduleId) {
        ModuleRecordInput moduleRecordInput = new ModuleRecordInput();
        moduleRecordInput.setUid(uid);
        moduleRecordInput.setUserId(learnerId);
        moduleRecordInput.setCourseId(courseId);
        moduleRecordInput.setModuleId(moduleId);
        moduleRecordInput.setModuleTitle(moduleTitle);
        moduleRecordInput.setOptional(false);
        moduleRecordInput.setModuleType(moduleType);
        moduleRecordInput.setState(State.IN_PROGRESS.name());
        return moduleRecordInput;
    }

    public CourseRecords createCourseRecords() {
        CourseRecords courseRecords = new CourseRecords(new ArrayList<>());
        courseRecords.getCourseRecords().add(createCourseRecord());
        return courseRecords;
    }

    public CourseRecord createCourseRecord() {
        return new CourseRecord(courseId, learnerId, courseTitle, State.IN_PROGRESS, null, null,
                null, isRequired, createModuleRecords(), updatedAt);
    }

    public List<ModuleRecord> createModuleRecords() {
        List<ModuleRecord> moduleRecords =  new ArrayList<>();
        moduleRecords.add(createModuleRecord());
        return moduleRecords;
    }

    public ModuleRecord createModuleRecord() {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setId(1L);
        moduleRecord.setUid(uid);
        moduleRecord.setModuleId(moduleId);
        moduleRecord.setModuleTitle(moduleTitle);
        moduleRecord.setModuleType(moduleType);
        moduleRecord.setOptional(false);
        moduleRecord.setState(State.IN_PROGRESS);
        moduleRecord.setCreatedAt(createdAt);
        moduleRecord.setUpdatedAt(updatedAt);
        moduleRecord.setCompletionDate(completedAt);
        return moduleRecord;
    }

    public ResponseEntity<?> createSuccessResponseForCourseRecords() {
        return new ResponseEntity<>(convertObjectToJsonString(createCourseRecords()), HttpStatus.OK);
    }

    public ResponseEntity<?> createSuccessResponseForCourseRecordsWithEmptyModuleUid(CourseRecords courseRecords) {
        courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId).setUid(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.OK);
    }

    public ResponseEntity<?> createSuccessResponseForCourseRecordsWithEmptyModule() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.getCourseRecord(courseId).setModuleRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.OK);
    }

    public ResponseEntity<?> createSuccessResponseForCourseRecordsWithEmptyCourseRecord() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.setCourseRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.OK);
    }

    public ResponseEntity<?> createErrorResponseForCourseRecordsWithEmptyCourseRecord() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.setCourseRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> createSuccessResponseForCourseRecordWithEmptyModules() {
        CourseRecord courseRecord = createCourseRecord();
        courseRecord.setModuleRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecord), HttpStatus.OK);
    }

    public ResponseEntity<?> createSuccessResponseForModuleRecord() {
        return new ResponseEntity<>(convertObjectToJsonString(createModuleRecord()), HttpStatus.OK);
    }

    public ResponseEntity<?> createErrorRusticiResponseForInvalidCourseIdBadRequest() {
        return new ResponseEntity<>(createErrorResponseForInvalidCourseId(), HttpStatus.BAD_REQUEST);
    }

    public ErrorResponse createErrorResponseForInvalidCourseId() {
        String invalidCourseIdMessage = "Course ID '" + courseId + "." + moduleId + "' is invalid";
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(invalidCourseIdMessage);
        return errorResponse;
    }
}
