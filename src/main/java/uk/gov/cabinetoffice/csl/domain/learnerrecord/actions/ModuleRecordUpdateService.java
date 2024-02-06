package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.util.StringUtilService;

import java.time.Clock;

@Service
public class ModuleRecordUpdateService {

    private final Clock clock;
    private final StringUtilService stringUtilService;

    public ModuleRecordUpdateService(Clock clock, StringUtilService stringUtilService) {
        this.clock = clock;
        this.stringUtilService = stringUtilService;
    }

    public IModuleRecordUpdate getLaunchModuleUpdate(Course course, Module module, boolean isCourseRequired) {
        return switch (module.getModuleType()) {
            case file, link -> new CompleteModuleUpdate(clock, course, module, isCourseRequired);
            default -> new LaunchModuleUpdate(stringUtilService, isCourseRequired, clock);
        };
    }

    public IModuleRecordUpdate getCompleteModuleUpdate(Course course, Module module) {
        return new CompleteModuleUpdate(clock, course, module);
    }

    public IModuleRecordUpdate getApproveEventUpdate(Event event) {
        return new ApproveEventUpdate(event);
    }

    public IModuleRecordUpdate getRegisterEventUpdate(Event event) {
        return new RegisterEventUpdate(event);
    }

    public IModuleRecordUpdate getCancelBookingUpdate() {
        return new CancelBookingUpdate();
    }

    public IModuleRecordUpdate getCompleteBookingUpdate(Course course) {
        return new CompleteBookingUpdate(course, clock);
    }

    public IModuleRecordUpdate getSkipBookingUpdate() {
        return new SkipBookingUpdate();
    }
}
