package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayModule;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecordCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DisplayCourseFactory implements IDisplayCourseFactory {

    private final DisplayModuleFactory displayModuleFactory;

    @Override
    public DisplayCourse generateDetailedDisplayCourse(Course course, User user, @Nullable CourseRecord courseRecord, LearningRecordCourse userLearningRecordCourse) {
        if (courseRecord != null) {
            Map<String, ModuleRecord> moduleRecordMap = courseRecord.getModuleRecordsAsMap();
            if (moduleRecordMap.isEmpty()) {
                return generateDetailedDisplayCourse(course, user);
            }
            Collection<DisplayModule> modules = course.getModules().stream().map(m -> {
                ModuleRecord moduleRecord = moduleRecordMap.get(m.getId());
                return displayModuleFactory.generateDisplayModule(m, moduleRecord, null);
            }).toList();

            DisplayModuleSummary moduleSummary = displayModuleFactory.generateDisplayModuleSummary(modules, userLearningRecordCourse);
            return DisplayCourse.build(course, modules, moduleSummary, courseRecord.getLastUpdated());
        }
        return generateDetailedDisplayCourse(course, user);
    }

    @Override
    public DisplayCourse generateDetailedDisplayCourse(Course course, User user) {
        List<DisplayModule> modules = course.getModules().stream().map(displayModuleFactory::generateDisplayModule).toList();
        return new DisplayCourse(course.getCacheableId(), course.getTitle(), course.getShortDescription(), null, null,
                State.NULL, modules, course.getRequiredModulesForCompletion().size(), 0);
    }
}
