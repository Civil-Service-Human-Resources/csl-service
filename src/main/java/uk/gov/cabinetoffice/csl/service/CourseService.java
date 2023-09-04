package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordActionService;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordActionProcessor;

@Service
@Slf4j
public class CourseService {
    private final LearnerRecordActionProcessor learnerRecordActionProcessor;
    private final LearnerRecordService learnerRecordService;
    private final CourseRecordActionService courseRecordActionService;

    public CourseService(LearnerRecordActionProcessor learnerRecordActionProcessor,
                         LearnerRecordService learnerRecordService, CourseRecordActionService courseRecordActionService) {
        this.learnerRecordActionProcessor = learnerRecordActionProcessor;
        this.learnerRecordService = learnerRecordService;
        this.courseRecordActionService = courseRecordActionService;
    }

    public CourseResponse addToLearningPlan(String learnerId, String courseId) {
        return processCourseRecordActionWithResponse(learnerId, courseId, courseRecordActionService.getAddToLearningPlanUpdate());
    }

    public CourseResponse removeFromLearningPlan(String learnerId, String courseId) {
        return processCourseRecordActionWithResponse(learnerId, courseId, courseRecordActionService.getRemoveFromLearningPlanUpdate());
    }

    public CourseResponse removeFromSuggestions(String learnerId, String courseId) {
        return processCourseRecordActionWithResponse(learnerId, courseId, courseRecordActionService.getRemoveFromSuggestionsUpdate());
    }

    private CourseResponse processCourseRecordActionWithResponse(String learnerId, String courseId, CourseRecordUpdate update) {
        CourseRecord courseRecord = processCourseRecordAction(learnerId, courseId, update);
        return new CourseResponse(String.format("Successfully applied action '%s' to course record", update.getName()),
                courseRecord.getCourseTitle(), courseId);
    }

    private CourseRecord processCourseRecordAction(String learnerId, String courseId, CourseRecordUpdate update) {
        log.info(String.format("Applying update '%s' to course record for course '%s' and user '%s'",
                update.getName(), courseId, learnerId));
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord == null) {
            courseRecord = learnerRecordActionProcessor.applyCreateUpdateToCourseRecord(learnerId, courseId, update);
        } else {
            courseRecord = learnerRecordActionProcessor.applyPatchUpdateToCourseRecord(courseRecord, update);
        }
        learnerRecordService.updateCourseRecordCache(courseRecord);
        return courseRecord;
    }
}
