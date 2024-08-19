package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class CourseTest extends TestDataService {

    private Course course;
    private CourseRecord courseRecord;
    private final User user = generateUser();

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
    public void testGetRequiredModules() {
        course.getModule("mod1").setOptional(false);
        course.getModule("mod2").setOptional(false);
        course.getModule("mod3").setOptional(true);
        Module[] requiredModules = course.getModulesRequiredForCompletion().toArray(new Module[]{});
        assertEquals("Expected mod1 to be in the required modules", "mod1", requiredModules[0].getId());
        assertEquals("Expected mod2 to be in the required modules", "mod2", requiredModules[1].getId());
    }

    @Test
    public void testGetRequiredModulesAllRequired() {
        course.getModule("mod1").setOptional(false);
        course.getModule("mod2").setOptional(false);
        course.getModule("mod3").setOptional(false);
        Module[] requiredModules = course.getModulesRequiredForCompletion().toArray(new Module[]{});
        assertEquals("Expected mod1 to be in the required modules", "mod1", requiredModules[0].getId());
        assertEquals("Expected mod2 to be in the required modules", "mod2", requiredModules[1].getId());
        assertEquals("Expected mod2 to be in the required modules", "mod3", requiredModules[2].getId());
    }

    @Test
    public void testGetRequiredModulesAllOptional() {
        course.getModule("mod1").setOptional(true);
        course.getModule("mod2").setOptional(true);
        course.getModule("mod3").setOptional(true);
        Module[] requiredModules = course.getModulesRequiredForCompletion().toArray(new Module[]{});
        assertEquals("Expected mod1 to be in the required modules", "mod1", requiredModules[0].getId());
        assertEquals("Expected mod2 to be in the required modules", "mod2", requiredModules[1].getId());
        assertEquals("Expected mod2 to be in the required modules", "mod3", requiredModules[2].getId());
    }

    // Completion tests

    @Test
    public void testCourseCompletedWithMandatoryModules() {
        course.getModule("mod1").setOptional(false);
        course.getModule("mod2").setOptional(false);
        course.getModule("mod3").setOptional(true);
        courseRecord.getModuleRecord("mod1").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod1").get().setCompletionDate(LocalDateTime.now());
        courseRecord.getModuleRecord("mod2").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").get().setCompletionDate(LocalDateTime.now());
        courseRecord.getModuleRecord("mod3").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod3").get().setCompletionDate(LocalDateTime.now());
        Integer result = course.getRemainingModulesForCompletion(courseRecord, user).size();
        assertEquals("Expected course to be completed (0 modules remaining)", 0, result);
    }

    @Test
    public void testCourseCompletedWithOptionalModules() {
        course.getModules().forEach(m -> m.setOptional(true));
        courseRecord.getModuleRecord("mod1").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod1").get().setCompletionDate(LocalDateTime.now());
        courseRecord.getModuleRecord("mod2").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").get().setCompletionDate(LocalDateTime.now());
        courseRecord.getModuleRecord("mod3").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod3").get().setCompletionDate(LocalDateTime.now());
        Integer result = course.getRemainingModulesForCompletion(courseRecord, user).size();
        assertEquals("Expected course to be completed", 0, result);
    }

    @Test
    public void testCourseNotCompletedWithOptionalModules() {
        course.getModule("mod1").setOptional(true);
        course.getModule("mod2").setOptional(true);
        course.getModule("mod3").setOptional(false);
        courseRecord.getModuleRecord("mod1").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod1").get().setCompletionDate(LocalDateTime.now());
        courseRecord.getModuleRecord("mod2").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").get().setCompletionDate(LocalDateTime.now());
        courseRecord.getModuleRecord("mod3").get().setState(State.IN_PROGRESS);
        Integer result = course.getRemainingModulesForCompletion(courseRecord, user).size();
        assertEquals("Expected course to not be completed (expected 1 module remaining)", 1, result);
    }

    @Test
    public void testCourseCompletedInLearningPeriod() {
        LearningPeriod learningPeriod = new LearningPeriod(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
        Audience audience = new Audience();
        audience.setLearningPeriod(learningPeriod);
        course.setAudiences(List.of(audience));
        course.setDepartmentCodeToRequiredAudienceMap(Map.of("HMRC", 0));
        course.getModule("mod1").setOptional(false);
        course.getModule("mod2").setOptional(false);
        course.getModule("mod3").setOptional(false);
        courseRecord.getModuleRecord("mod1").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod1").get().setCompletionDate(LocalDateTime.of(2023, 2, 1, 10, 0, 0));
        courseRecord.getModuleRecord("mod2").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").get().setCompletionDate(LocalDateTime.of(2023, 4, 30, 10, 0, 0));
        courseRecord.getModuleRecord("mod3").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod3").get().setCompletionDate(LocalDateTime.of(2023, 12, 1, 10, 0, 0));
        Integer result = course.getRemainingModulesForCompletion(courseRecord, user).size();
        assertEquals("Expected course to be completed (expected 0 modules remaining)", 0, result);
    }

    @Test
    public void testCourseHalfCompletedInLearningPeriod() {
        LearningPeriod learningPeriod = new LearningPeriod(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
        Audience audience = new Audience();
        audience.setLearningPeriod(learningPeriod);
        course.setAudiences(List.of(audience));
        course.setDepartmentCodeToRequiredAudienceMap(Map.of("HMRC", 0));
        course.getModule("mod1").setOptional(false);
        course.getModule("mod2").setOptional(false);
        course.getModule("mod3").setOptional(false);
        courseRecord.getModuleRecord("mod1").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod1").get().setCompletionDate(LocalDateTime.of(2022, 2, 1, 10, 0, 0));
        courseRecord.getModuleRecord("mod2").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod2").get().setCompletionDate(LocalDateTime.of(2023, 4, 30, 10, 0, 0));
        courseRecord.getModuleRecord("mod3").get().setState(State.COMPLETED);
        courseRecord.getModuleRecord("mod3").get().setCompletionDate(LocalDateTime.of(2023, 12, 1, 10, 0, 0));
        Collection<Module> result = course.getRemainingModulesForCompletion(courseRecord, user);
        assertEquals("Expected course not to be completed (expected 1 module remaining)", 1, result.size());
        assertEquals("Expected mod1 to not be completed in this learning period", "mod1", result.stream().findFirst().get().getId());
    }

    // Required learning

    @Test
    public void testGetRequiredLearningFromCourse() {
        LearningPeriod learningPeriod = new LearningPeriod(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
        Audience audience = new Audience();
        audience.setLearningPeriod(learningPeriod);
        course.setAudiences(List.of(audience));
        course.setDepartmentCodeToRequiredAudienceMap(Map.of("HMRC", 0));
        assertTrue("Expected course to be mandatory for user", course.isMandatoryLearningForUser(user));
    }

    @Test
    public void testGetRequiredLearningFromCourseMultipleDepartments() {
        LearningPeriod learningPeriod = new LearningPeriod(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1)
        );
        Audience audience = new Audience();
        audience.setLearningPeriod(learningPeriod);
        
        LearningPeriod learningPeriod2 = new LearningPeriod(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
        Audience audience2 = new Audience();
        audience2.setLearningPeriod(learningPeriod2);

        course.setAudiences(List.of(audience, audience2));
        course.setDepartmentCodeToRequiredAudienceMap(Map.of("CO", 0, "HMRC", 1));
        // HMRC is higher up in the hierarchy for this list, so the HMRC learning period should be selected
        LearningPeriod learningPeriodResult = course.getLearningPeriodForDepartmentHierarchy(List.of("CO", "HMRC")).get();
        assertEquals("Expected start date to equal 2024-01-01", LocalDate.of(2024, 1, 1), learningPeriod.getStartDate());
        assertEquals("Expected start date to equal 2025-01-01", LocalDate.of(2025, 1, 1), learningPeriod.getEndDate());
    }


}
