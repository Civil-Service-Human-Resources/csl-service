package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final LearningCatalogueService learningCatalogueService;
    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    public CourseResponse addToLearningPlan(User user, String courseId) {
        return processCourseRecordActionWithResponse(user, courseId, CourseRecordAction.ADD_TO_LEARNING_PLAN, null);
    }

    public CourseResponse removeFromLearningPlan(User user, String courseId) {
        return processCourseRecordActionWithResponse(user, courseId, CourseRecordAction.REMOVE_FROM_LEARNING_PLAN, null);
    }

    public CourseResponse removeFromSuggestions(User user, String courseId) {
        return processCourseRecordActionWithResponse(user, courseId, CourseRecordAction.REMOVE_FROM_SUGGESTIONS, null);
    }

    private CourseResponse processCourseRecordActionWithResponse(User user, String courseId, CourseRecordAction actionType, LocalDateTime completionDate) {
        Course course = learningCatalogueService.getCourse(courseId);
        learnerRecordUpdateProcessor.processCourseRecordAction(course, user, actionType, completionDate);
        return CourseResponse.fromMetaData(actionType, course);
    }

}
