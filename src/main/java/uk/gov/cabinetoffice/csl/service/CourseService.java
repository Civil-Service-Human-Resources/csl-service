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
    private final CourseRecordActionService courseRecordActionService;

    public CourseService(LearnerRecordActionProcessor learnerRecordActionProcessor,
                         CourseRecordActionService courseRecordActionService) {
        this.learnerRecordActionProcessor = learnerRecordActionProcessor;
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
        CourseRecord courseRecord = learnerRecordActionProcessor.processCourseRecordAction(learnerId, courseId, update);
        return new CourseResponse(String.format("Successfully applied action '%s' to course record", update.getName()),
                courseRecord.getCourseTitle(), courseId);
    }

}
