package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseRecordActionFactory {

    private final UtilService utilService;

    public CourseRecordActionFactory(UtilService utilService) {
        this.utilService = utilService;
    }

    public ICourseRecordAction getCourseRecordAction(Course course, User user, CourseRecordAction action) {
        return switch (action) {
            case ADD_TO_LEARNING_PLAN -> new AddToLearningPlan(utilService, course, user);
            case REMOVE_FROM_LEARNING_PLAN -> new RemoveFromLearningPlan(utilService, course, user);
            case REMOVE_FROM_SUGGESTIONS -> new RemoveFromSuggestions(utilService, course, user);
        };
    }

    public ICourseRecordAction getModuleRecordAction(CourseWithModule courseWithModule, User user, ModuleRecordAction action, LocalDateTime completionDate) {
        return switch (action) {
            case LAUNCH_MODULE -> new LaunchModule(utilService, courseWithModule, user);
            case COMPLETE_MODULE -> new CompleteModule(utilService, courseWithModule, user, completionDate);
            case FAIL_MODULE -> new FailModule(utilService, courseWithModule, user);
            case PASS_MODULE -> new PassModule(utilService, courseWithModule, user);
            case ROLLUP_COMPLETE_MODULE -> new RollupCompleteModule(utilService, courseWithModule, user, completionDate);
        };
    }

    public CourseRecordActionCollection getEventModuleRecordActions(CourseWithModuleWithEvent courseWithModuleWithEvent, List<UserToAction<EventModuleRecordAction>> users) {
        List<CourseRecordId> courseRecordIds = new ArrayList<>();
        List<ICourseRecordAction> actions = new ArrayList<>();
        users.forEach(user -> {
            courseRecordIds.add(new CourseRecordId(user.getUser().getId(), courseWithModuleWithEvent.getCourse().getId()));
            actions.add(getEventModuleRecordAction(courseWithModuleWithEvent, user.getUser(), user.getAction()));
        });
        return new CourseRecordActionCollection(actions, courseRecordIds);
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

    public ICourseRecordAction getMultipleModuleRecordActions(CourseWithModule courseWithModule, User user, List<ModuleRecordAction> actions, LocalDateTime completionDate) {
        MultiCourseRecordAction multiCourseRecordAction = new MultiCourseRecordAction(actions.stream().map(a -> getModuleRecordAction(courseWithModule, user, a, completionDate)).collect(Collectors.toList()));
        return new MultiModuleRecordActionProcessor(utilService, courseWithModule, user, multiCourseRecordAction);
    }

}
