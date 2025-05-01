package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRecordActionFactory {

    private final UtilService utilService;


    public IModuleRecordAction getModuleRecordAction(CourseWithModule courseWithModule, User user, ModuleRecordAction action) {
        return switch (action) {
            case LAUNCH_MODULE -> new LaunchModule(utilService, courseWithModule, user);
            case COMPLETE_MODULE -> new CompleteModule(utilService, courseWithModule, user);
            case FAIL_MODULE -> new FailModule(utilService, courseWithModule, user);
            case PASS_MODULE -> new PassModule(utilService, courseWithModule, user);
            case ROLLUP_COMPLETE_MODULE -> new RollupCompleteModule(utilService, courseWithModule, user);
        };
    }

    public ModuleRecordActionCollection getEventModuleRecordActions(CourseWithModuleWithEvent courseWithModuleWithEvent, List<UserToAction<EventModuleRecordAction>> users) {
        List<LearnerRecordResourceId> courseRecordIds = new ArrayList<>();
        List<IModuleRecordAction> actions = new ArrayList<>();
        users.forEach(user -> {
            courseRecordIds.add(new LearnerRecordResourceId(user.getUser().getId(), courseWithModuleWithEvent.getCourse().getCacheableId()));
            actions.add(getEventModuleRecordAction(courseWithModuleWithEvent, user.getUser(), user.getAction()));
        });
        return new ModuleRecordActionCollection(actions, courseRecordIds);
    }

    public IModuleRecordAction getEventModuleRecordAction(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, EventModuleRecordAction action) {
        return switch (action) {
            case REGISTER_BOOKING -> new RegisterEvent(utilService, courseWithModuleWithEvent, user);
            case CANCEL_BOOKING -> new CancelBooking(utilService, courseWithModuleWithEvent, user);
            case APPROVE_BOOKING -> new ApproveBooking(utilService, courseWithModuleWithEvent, user);
            case COMPLETE_BOOKING -> new CompleteBooking(utilService, courseWithModuleWithEvent, user);
            case SKIP_BOOKING -> new SkipBooking(utilService, courseWithModuleWithEvent, user);
        };
    }

    public IModuleRecordAction getMultipleModuleRecordActions(CourseWithModule courseWithModule, User user, List<ModuleRecordAction> actions) {
        MultiCourseRecordAction multiCourseRecordAction = new MultiCourseRecordAction(actions.stream().map(a -> getModuleRecordAction(courseWithModule, user, a)).collect(Collectors.toList()));
        return new MultiModuleRecordActionProcessor(utilService, courseWithModule, user, multiCourseRecordAction);
    }

}
