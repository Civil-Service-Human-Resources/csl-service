package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class CourseTest {

    private Course course;
    private CourseRecord courseRecord;

    @BeforeEach
    public void before() {
        course = new Course();
        course.setId("courseId");
        Module module1 = new Module();
        module1.setId("mod1");
        Module module2 = new Module();
        module2.setId("mod2");
        Module module3 = new Module();
        module3.setId("mod3");
        course.setModules(List.of(
                module1, module2, module3
        ));

        courseRecord = new CourseRecord();
        courseRecord.setCourseId(course.getId());
        ModuleRecord moduleRecord1 = new ModuleRecord();
        moduleRecord1.setModuleId(module1.getId());
        ModuleRecord moduleRecord2 = new ModuleRecord();
        moduleRecord2.setModuleId(module2.getId());
        ModuleRecord moduleRecord3 = new ModuleRecord();
        moduleRecord3.setModuleId(module3.getId());
        courseRecord.setModuleRecords(List.of(
                moduleRecord1, moduleRecord2, moduleRecord3
        ));
    }

    @Test
    public void testCourseCompletedWithMandatoryModules() {
        course.getModule("mod1").setOptional(false);
        course.getModule("mod2").setOptional(false);
        course.getModule("mod3").setOptional(true);
        courseRecord.getModuleRecord("mod1").setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod3").setState(State.COMPLETED);
        boolean result = course.isCourseComplete(courseRecord);
        assertTrue("Expected course to be completed", result);
    }

    @Test
    public void testCourseCompletedWithOptionalModules() {
        course.getModules().forEach(m -> m.setOptional(true));
        courseRecord.getModuleRecord("mod1").setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod3").setState(State.COMPLETED);
        boolean result = course.isCourseComplete(courseRecord);
        assertTrue("Expected course to be completed", result);
    }

    @Test
    public void testCourseNotCompletedWithOptionalModules() {
        course.getModule("mod1").setOptional(true);
        course.getModule("mod2").setOptional(true);
        course.getModule("mod3").setOptional(false);
        courseRecord.getModuleRecord("mod1").setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod3").setState(State.IN_PROGRESS);
        boolean result = course.isCourseComplete(courseRecord);
        assertFalse("Expected course to not be completed", result);
    }

}
