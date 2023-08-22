package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.error.RecordAlreadyExistsException;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CourseService {

    private final LearnerRecordService learnerRecordService;

    public CourseService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public CourseResponse removeFromLearningPlan(String learnerId, String courseId) {
        log.info("Removing course '{}' from user '{}' learning plan", courseId, learnerId);
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord != null) {
            try {
                List<PatchOp> patches = Collections.singletonList(PatchOp.replacePatch("state", State.ARCHIVED.name()));
                courseRecord = learnerRecordService.updateCourseRecord(learnerId, courseId, patches);
                return new CourseResponse("Successfully removed course from learning plan",
                        courseRecord.getCourseTitle(), courseId);
            } catch (Exception e) {
                String msg = String.format("Failed to remove course '%s' from user '%s' learning plan: %s", courseId, learnerId, e.getMessage());
                throw new GenericServerException(msg);
            }
        } else {
            throw new RecordNotFoundException(String.format("Course record with ID '%s' does not exist for user '%s'", courseId, learnerId));
        }
    }

    public CourseResponse addCourseToLearningPlan(String learnerId, String courseId) {
        log.info("Adding course '{}' to user '{}' learning plan", courseId, learnerId);
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        try {
            if (courseRecord == null) {
                CourseRecordStatus status = CourseRecordStatus.builder().preference(Preference.LIKED.name()).build();
                courseRecord = learnerRecordService.createCourseRecord(learnerId, courseId, status);
            } else {
                List<PatchOp> patches = List.of(
                        PatchOp.replacePatch("preference", Preference.LIKED.name()),
                        PatchOp.removePatch("state")
                );
                courseRecord = learnerRecordService.updateCourseRecord(learnerId, courseId, patches);
            }
            return new CourseResponse("Successfully added course to learning plan",
                    courseRecord.getCourseTitle(), courseId);
        } catch (Exception e) {
            String msg = String.format("Failed to add course '%s' to user '%s' learning plan: %s", courseId, learnerId, e.getMessage());
            throw new GenericServerException(msg);
        }
    }

    public CourseResponse removeCourseFromSuggestions(String learnerId, String courseId) {
        log.info("Removing course '{}' from user '{}' suggestions", courseId, learnerId);
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord == null) {
            try {
                CourseRecordStatus status = CourseRecordStatus.builder().preference(Preference.DISLIKED.name()).build();
                courseRecord = learnerRecordService.createCourseRecord(learnerId, courseId, status);
                return new CourseResponse("Successfully removed course from suggestions",
                        courseRecord.getCourseTitle(), courseId);
            } catch (Exception e) {
                String msg = String.format("Failed to remove course '%s' from user '%s' suggestions: %s", courseId, learnerId, e.getMessage());
                throw new GenericServerException(msg);
            }
        } else {
            throw new RecordAlreadyExistsException(String.format("Course record with ID '%s' already exists for user '%s'", courseId, learnerId));
        }
    }
}
