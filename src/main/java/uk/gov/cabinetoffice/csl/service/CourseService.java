package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final LearningCatalogueService learningCatalogueService;
    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    public CourseResponse addToLearningPlan(User user, String courseId) {
        return processCourseRecordActionWithResponse(user, courseId, CourseRecordAction.ADD_TO_LEARNING_PLAN);
    }

    public CourseResponse removeFromLearningPlan(User user, String courseId) {
        return processCourseRecordActionWithResponse(user, courseId, CourseRecordAction.REMOVE_FROM_LEARNING_PLAN);
    }

    public CourseResponse removeFromSuggestions(User user, String courseId) {
        return processCourseRecordActionWithResponse(user, courseId, CourseRecordAction.REMOVE_FROM_SUGGESTIONS);
    }

    private CourseResponse processCourseRecordActionWithResponse(User user, String courseId, CourseRecordAction actionType) {
        Course course = learningCatalogueService.getCourse(courseId);
        learnerRecordUpdateProcessor.processCourseRecordAction(course, user, actionType);
        return CourseResponse.fromMetaData(actionType, course);
    }

}
