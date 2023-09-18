package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordUpdateFactory;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;

@Service
@Slf4j
public class CourseService {
    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final CourseRecordUpdateFactory courseRecordUpdateFactory;

    public CourseService(LearnerRecordUpdateProcessor learnerRecordUpdateProcessor,
                         CourseRecordUpdateFactory courseRecordUpdateFactory) {
        this.learnerRecordUpdateProcessor = learnerRecordUpdateProcessor;
        this.courseRecordUpdateFactory = courseRecordUpdateFactory;
    }

    public CourseResponse addToLearningPlan(String learnerId, String courseId) {
        return processCourseRecordActionWithResponse(learnerId, courseId, courseRecordUpdateFactory.getAddToLearningPlanUpdate());
    }

    public CourseResponse removeFromLearningPlan(String learnerId, String courseId) {
        return processCourseRecordActionWithResponse(learnerId, courseId, courseRecordUpdateFactory.getRemoveFromLearningPlanUpdate());
    }

    public CourseResponse removeFromSuggestions(String learnerId, String courseId) {
        return processCourseRecordActionWithResponse(learnerId, courseId, courseRecordUpdateFactory.getRemoveFromSuggestionsUpdate());
    }

    private CourseResponse processCourseRecordActionWithResponse(String learnerId, String courseId, ICourseRecordUpdate update) {
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processCourseRecordAction(learnerId, courseId, update);
        return new CourseResponse(String.format("Successfully applied action '%s' to course record", update.getName()),
                courseRecord.getCourseTitle(), courseId);
    }

}
