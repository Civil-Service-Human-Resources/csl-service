package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisplayCourseFactoryTest {

    @Mock
    private DisplayModuleFactory displayModuleFactory;

    @Mock
    private DisplayAudienceFactory displayAudienceFactory;

    @InjectMocks
    private DisplayCourseFactory displayCourseFactory;

    @Test
    void testGenerateDetailedDisplayCourse() {
        Course course = new Course();
        course.setId("courseId");
        course.setTitle("Course Title");
        Module module = new Module();
        module.setId("moduleId");
        course.setModules(List.of(module));

        User user = mock(User.class);

        DisplayModule displayModule = new DisplayModule("moduleId", "Module Title", "", "elearning", true, true, null, null, State.NULL);
        when(displayModuleFactory.generateDisplayModule(module)).thenReturn(displayModule);

        DisplayCourse result = displayCourseFactory.generateDetailedDisplayCourse(course, user);

        assertEquals("courseId", result.getCourseId());
        assertEquals("Course Title", result.getCourseTitle());
        assertEquals(State.NULL, result.getStatus());
        assertEquals(1, result.getModules().size());
        assertEquals(0, result.getCompletedRequiredModules());
    }

    @Test
    void testGenerateDetailedDisplayCourseWithCourseRecord() {
        Course course = new Course();
        course.setId("courseId");
        course.setTitle("Course Title");
        Module module = new Module();
        module.setId("moduleId");
        course.setModules(List.of(module));

        User user = mock(User.class);
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setCourseId("courseId");
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setModuleId("moduleId");
        courseRecord.setModuleRecords(List.of(moduleRecord));
        courseRecord.setLastUpdated(LocalDateTime.now());

        DisplayModule displayModule = new DisplayModule("moduleId", "Module Title", "", "elearning", true, true, null, null, State.COMPLETED);
        when(displayModuleFactory.generateDisplayModule(any(), any(), any())).thenReturn(displayModule);

        DisplayModuleSummary summary = new DisplayModuleSummary(null, 0, 1, 1, State.COMPLETED);
        when(displayModuleFactory.generateDisplayModuleSummary(any(), any())).thenReturn(summary);

        DisplayCourse result = displayCourseFactory.generateDetailedDisplayCourse(course, user, courseRecord, null);

        assertEquals("courseId", result.getCourseId());
        assertEquals(State.COMPLETED, result.getStatus());
        assertEquals(1, result.getCompletedRequiredModules());
    }
}
