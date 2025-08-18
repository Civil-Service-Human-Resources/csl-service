package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.BookedLearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.EventModule;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class LearningPlanFactory {

    private final IUtilService utilService;

    public LearningPlanFactory(IUtilService utilService) {
        this.utilService = utilService;
    }

    private EventModule buildEventModule(Module module, Event event, ModuleRecord moduleRecord) {
        return new EventModule(module.getId(), module.getTitle(), event.getId(), moduleRecord.getEventDate(),
                event.getDateRangesAsDates(), moduleRecord.getState());
    }

    public Optional<BookedLearningPlanCourse> getBookedLearningPlanCourse(Course course, List<ModuleRecord> requiredModuleRecords) {
        BookedLearningPlanCourse bookedCourse = null;
        ModuleRecord moduleRecord = null;
        int otherModulesCompleted = 0;
        for (ModuleRecord mr : requiredModuleRecords) {
            if (mr.getEventId() != null && mr.getEventDate() != null &&
                    !mr.equalsStates(State.SKIPPED, State.COMPLETED)) {
                moduleRecord = mr;
            } else if (mr.getState().equals(State.COMPLETED)) {
                otherModulesCompleted++;
            }
        }
        if (moduleRecord != null) {
            Module module = course.getModule(moduleRecord.getModuleId());
            Event event = module.getEvent(moduleRecord.getEventId());
            if (event != null) {
                EventModule eventModule = buildEventModule(module, event, moduleRecord);
                boolean canBeMovedToLearningPlan = utilService.getNowDateTime().isAfter(moduleRecord.getEventDate().atTime(LocalTime.MIN)) &&
                        (course.getCourseType().equals("face-to-face") || otherModulesCompleted == requiredModuleRecords.size() - 1);
                bookedCourse = new BookedLearningPlanCourse(course.getId(), course.getTitle(),
                        course.getShortDescription(), course.getCourseType(), course.getDurationInMinutes(),
                        course.getModules().size(), course.getCost(), State.NULL, eventModule, canBeMovedToLearningPlan);
            }
        }
        return Optional.ofNullable(bookedCourse);
    }

    public LearningPlanCourse getLearningPlanCourse(Course course, State state) {
        return new LearningPlanCourse(course.getId(), course.getTitle(),
                course.getShortDescription(), course.getCourseType(), course.getDurationInMinutes(),
                course.getModules().size(), course.getCost(), state);
    }
}
