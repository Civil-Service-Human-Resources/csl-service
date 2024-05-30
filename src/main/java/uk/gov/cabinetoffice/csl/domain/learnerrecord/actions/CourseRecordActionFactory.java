package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.AddToLearningPlan;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.RemoveFromLearningPlan;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.RemoveFromSuggestions;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRecordActionFactory {

    private final UtilService utilService;

    public ICourseRecordAction getCourseRecordAction(Course course, User user, CourseRecordAction action) {
        return switch (action) {
            case ADD_TO_LEARNING_PLAN -> new AddToLearningPlan(utilService, course, user);
            case REMOVE_FROM_LEARNING_PLAN -> new RemoveFromLearningPlan(utilService, course, user);
            case REMOVE_FROM_SUGGESTIONS -> new RemoveFromSuggestions(utilService, course, user);
        };
    }

    public ICourseRecordAction getModuleRecordAction(CourseWithModule courseWithModule, User user, ModuleRecordAction action) {
        return switch (action) {
            case LAUNCH_MODULE -> new LaunchModule(utilService, courseWithModule, user);
            case COMPLETE_MODULE -> new CompleteModule(utilService, courseWithModule, user);
            case FAIL_MODULE -> new FailModule(utilService, courseWithModule, user);
            case PASS_MODULE -> new PassModule(utilService, courseWithModule, user);
            case ROLLUP_COMPLETE_MODULE -> new RollupCompleteModule(utilService, courseWithModule, user);
        };
    }

    public ICourseRecordAction getEventModuleRecordAction(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, EventModuleRecordAction action) {
        return switch (action) {
            case REGISTER_BOOKING -> new RegisterEvent(utilService, courseWithModuleWithEvent, user);
            case CANCEL_BOOKING -> new CancelBooking(utilService, courseWithModuleWithEvent, user);
            case APPROVE_BOOKING -> new ApproveBooking(utilService, courseWithModuleWithEvent, user);
            case COMPLETE_BOOKING -> new CompleteBooking(utilService, courseWithModuleWithEvent, user);
            case SKIP_BOOKING -> new SkipBooking(utilService, courseWithModuleWithEvent, user);
        };
    }

    public ICourseRecordAction getMultipleModuleRecordActions(CourseWithModule courseWithModule, User user, List<ModuleRecordAction> actions) {
        MultiCourseRecordAction multiCourseRecordAction = new MultiCourseRecordAction(actions.stream().map(a -> getModuleRecordAction(courseWithModule, user, a)).collect(Collectors.toList()));
        return new MultiModuleRecordActionProcessor(utilService, courseWithModule, user, multiCourseRecordAction);
    }

}
