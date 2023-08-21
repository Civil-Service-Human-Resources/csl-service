package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordInput;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordInput;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class LearnerRecordServiceTest {

    @Mock
    private ILearnerRecordClient learnerRecordClient;

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @InjectMocks
    private LearnerRecordService learnerRecordService;

    private final String courseId = "courseId";
    private final String learnerId = "learnerId";

    @Test
    public void testCourseCompletedWithMandatoryModules() {
        Course course = getCourse(3);
        course.getModule("mod1").setOptional(false);
        course.getModule("mod2").setOptional(false);
        course.getModule("mod3").setOptional(true);
        when(learningCatalogueService.getCourse("courseId")).thenReturn(course);
        boolean result = learnerRecordService.isCourseCompleted("courseId", List.of(
                "mod1", "mod2"
        ));
        assertTrue("Expected course to be completed", result);
    }

    @Test
    public void testCourseCompletedWithOptionalModules() {
        Course course = getCourse(3);
        course.getModules().forEach(m -> m.setOptional(true));
        when(learningCatalogueService.getCourse(courseId)).thenReturn(course);
        boolean result = learnerRecordService.isCourseCompleted(courseId, List.of(
                "mod1", "mod2", "mod3"
        ));
        assertTrue("Expected course to be completed", result);
    }

    @Test
    public void testCourseNotCompletedWithOptionalModules() {
        Course course = getCourse(3);
        course.getModule("mod3").setOptional(false);
        when(learningCatalogueService.getCourse(courseId)).thenReturn(course);
        boolean result = learnerRecordService.isCourseCompleted(courseId, List.of(
                "mod1", "mod2"
        ));
        assertFalse("Expected course to be completed", result);
    }

    @Test
    public void testCreateCourseRecordNoModule() {
        Course course = getCourse(0);
        CourseRecordInput expectedInput = getCourseRecordInput(false);
        CourseRecordStatus status = CourseRecordStatus.builder()
                .preference("LIKED")
                .isRequired(false)
                .state("IN_PROGRESS")
                .build();
        when(learningCatalogueService.getCourse(courseId)).thenReturn(course);
        learnerRecordService.createCourseRecord(learnerId, courseId, status);
        verify(learnerRecordClient, atMostOnce()).createCourseRecord(expectedInput);
    }

    @Test
    public void testCreateCourseRecord() {
        Course course = getCourse(1);
        Module mod = course.getModule("mod1");
        mod.setOptional(false);
        mod.setTitle("Module Title");
        mod.setDuration(10L);
        mod.setModuleType("elearning");
        mod.setCost(BigDecimal.valueOf(10));
        CourseRecordInput expectedInput = getCourseRecordInput(true);
        CourseRecordStatus status = CourseRecordStatus.builder()
                .preference("LIKED")
                .isRequired(false)
                .state("IN_PROGRESS")
                .build();
        ModuleRecordStatus moduleRecordStatus = ModuleRecordStatus.builder()
                .state("IN_PROGRESS").result("PASSED").eventId("eventId")
                .eventDate(LocalDate.now()).completedDate(LocalDateTime.now())
                .build();
        when(learningCatalogueService.getCourse(courseId)).thenReturn(course);
        learnerRecordService.createCourseRecord(learnerId, courseId, "mod1", status, moduleRecordStatus);
        verify(learnerRecordClient, atMostOnce()).createCourseRecord(expectedInput);
    }

    private ModuleRecordInput getModuleRecordInput() {
        return new ModuleRecordInput(
                "uid", learnerId, courseId, "mod1", "Module Title",
                false, 10L, "elearning", BigDecimal.valueOf(10),
                "IN_PROGRESS", "PASSED", LocalDate.now(), "eventId", LocalDateTime.now()
        );
    }

    private CourseRecordInput getCourseRecordInput(boolean withModule) {
        return new CourseRecordInput(
                courseId,
                learnerId,
                "Course Title",
                "IN_PROGRESS",
                false,
                "LIKED",
                withModule ? List.of(getModuleRecordInput()) : List.of()
        );
    }

    private Course getCourse(int moduleCount) {
        Course course = new Course();
        course.setId(courseId);
        List<Module> mods = new ArrayList<>();
        for (int i = 0; i < moduleCount; i++) {
            mods.add(createBasicModule("mod" + (i + 1)));
        }
        course.setModules(mods);
        return course;
    }

    private Module createBasicModule(String id) {
        Module module = new Module();
        module.setId(id);
        module.setOptional(true);
        return module;
    }
}
