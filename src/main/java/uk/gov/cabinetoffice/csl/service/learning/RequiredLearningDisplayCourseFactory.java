package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequiredLearningDisplayCourseFactory implements IDisplayCourseFactory {

    private final DisplayModuleFactory displayModuleFactory;
    private final DisplayAudienceFactory displayAudienceFactory;

    public DisplayCourse generateDetailedDisplayCourse(Course course, User user, @Nullable CourseRecord courseRecord) {
        if (courseRecord != null) {
            Map<String, ModuleRecord> moduleRecordMap = courseRecord.getModuleRecordsAsMap();
            if (moduleRecordMap.isEmpty()) {
                return generateDetailedDisplayCourse(course, user);
            }
            DisplayAudience displayAudience = displayAudienceFactory.generateDisplayAudience(course, user);
            LearningPeriod learningPeriod = displayAudience == null ? null : displayAudience.getLearningPeriod();
            Collection<DisplayModule> modules = course.getModules().stream().map(m -> {
                ModuleRecord moduleRecord = moduleRecordMap.get(m.getId());
                return displayModuleFactory.generateDisplayModule(m, moduleRecord, learningPeriod);
            }).toList();
            DisplayModuleSummary moduleSummary = displayModuleFactory.generateDisplayModuleSummary(modules);
            return DisplayCourse.build(course, modules, moduleSummary, displayAudience, courseRecord.getLastUpdated());
        }
        return generateDetailedDisplayCourse(course, user);
    }

    public DisplayCourse generateDetailedDisplayCourse(Course course, User user) {
        List<DisplayModule> modules = course.getModules().stream().map(displayModuleFactory::generateDisplayModule).toList();
        DisplayAudience displayAudience = displayAudienceFactory.generateDisplayAudience(course, user);
        return new DisplayCourse(course.getId(), course.getTitle(), course.getShortDescription(), null, null,
                State.NULL, displayAudience, modules, course.getRequiredModulesForCompletion().size(), 0);
    }

}
