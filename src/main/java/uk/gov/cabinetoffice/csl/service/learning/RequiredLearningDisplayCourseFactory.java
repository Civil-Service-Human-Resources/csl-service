package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayAudience;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequiredLearningDisplayCourseFactory implements IDisplayCourseFactory {

    private final DisplayModuleFactory displayModuleFactory;
    private final DisplayAudienceFactory displayAudienceFactory;

    public DisplayCourse generateDetailedDisplayCourse(Course course, User user, CourseRecord courseRecord) {
        LocalDateTime courseCompletionDate = null;
        DisplayAudience displayAudience = displayAudienceFactory.generateDisplayAudience(course, user);
        LearningPeriod learningPeriod = displayAudience == null ? null : displayAudience.getLearningPeriod();
        Map<String, ModuleRecord> moduleRecordMap = courseRecord.getModuleRecordsAsMap();
        List<String> moduleIdsRequiredForCompletion = course.getRequiredModuleIdsForCompletion();
        List<DisplayModule> displayModules = new ArrayList<>();
        int inProgressCount = 0;
        Integer requiredCompletedCount = 0;
        for (Module m : course.getModules()) {
            ModuleRecord moduleRecord = moduleRecordMap.get(m.getId());
            DisplayModule displayModule = moduleRecord == null ? displayModuleFactory.generateDisplayModule(m) : displayModuleFactory.generateDisplayModule(m, moduleRecord, learningPeriod);
            if (moduleIdsRequiredForCompletion.contains(displayModule.getId())) {
                if (displayModule.getStatus().equals(State.COMPLETED)) {
                    LocalDateTime completionDate = Objects.requireNonNullElse(displayModule.getCompletionDate(), LocalDateTime.MIN);
                    if (completionDate.isAfter(Objects.requireNonNullElse(courseCompletionDate, LocalDateTime.MIN))
                            && courseRecord.getState().equals(State.COMPLETED)) {
                        courseCompletionDate = completionDate;
                    }
                    requiredCompletedCount++;
                } else if (displayModule.getStatus().equals(State.IN_PROGRESS)) {
                    inProgressCount++;
                }
            } else {
                if (!displayModule.getStatus().equals(State.NULL)) {
                    inProgressCount++;
                }
            }
            displayModules.add(displayModule);
        }

        State courseRecordState = State.NULL;
        if (requiredCompletedCount.equals(moduleIdsRequiredForCompletion.size())) {
            courseRecordState = State.COMPLETED;
        } else if (inProgressCount > 0 || requiredCompletedCount > 0) {
            courseRecordState = State.IN_PROGRESS;
        }

        return new DisplayCourse(course.getId(), course.getTitle(), course.getShortDescription(), courseRecord.getLastUpdated(),
                courseCompletionDate, courseRecordState, displayAudience, displayModules, course.getModulesRequiredForCompletion().size(), requiredCompletedCount);
    }

    public DisplayCourse generateDetailedDisplayCourse(Course course, User user) {
        List<DisplayModule> modules = course.getModules().stream().map(displayModuleFactory::generateDisplayModule).toList();
        DisplayAudience displayAudience = displayAudienceFactory.generateDisplayAudience(course, user);
        return new DisplayCourse(course.getId(), course.getTitle(), course.getShortDescription(), null, null,
                State.NULL, displayAudience, modules, course.getModulesRequiredForCompletion().size(), 0);
    }

}
