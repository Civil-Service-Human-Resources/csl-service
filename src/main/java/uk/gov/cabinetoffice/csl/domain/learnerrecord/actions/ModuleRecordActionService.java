package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.util.StringUtilService;

import java.time.Clock;

@Service
public class ModuleRecordActionService {

    private final Clock clock;
    private final StringUtilService stringUtilService;

    public ModuleRecordActionService(Clock clock, StringUtilService stringUtilService) {
        this.clock = clock;
        this.stringUtilService = stringUtilService;
    }

    public ModuleRecordUpdate getLaunchModuleUpdate(Course course, Module module, boolean isCourseRequired) {
        return switch (module.getModuleType()) {
            case elearning, video -> new LaunchModuleUpdate(stringUtilService, isCourseRequired, clock);
            case file, link -> new CompleteModuleUpdate(course, module);
        };
    }

    public ModuleRecordUpdate getCompleteModuleUpdate(Course course, Module module) {
        return new CompleteModuleUpdate(course, module);
    }
}
