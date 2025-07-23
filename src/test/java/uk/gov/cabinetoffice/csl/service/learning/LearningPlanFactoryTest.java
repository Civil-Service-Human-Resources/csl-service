package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.BookedLearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.DateRange;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningPlanFactoryTest {

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private LearningPlanFactory learningPlanFactory;


    Event event = new Event();
    Module module1 = new Module();
    Module module2 = new Module();
    Course course = new Course();

    @BeforeEach
    public void setUp() {
        when(utilService.getNowDateTime()).thenReturn(
                LocalDateTime.of(2025, 2, 1, 10, 0, 0)
        );
        event.setId("event1");
        event.setDateRanges(List.of(new DateRange(
                LocalDate.of(2025, 1, 1),
                LocalTime.of(10, 0, 0),
                LocalTime.of(12, 0, 0)
        )));
        module1.setId("module1");
        module1.setTitle("Module 1");
        module1.setModuleType(ModuleType.facetoface);
        module1.setCost(BigDecimal.TEN);
        module1.setEvents(List.of(event));

        module2.setId("module2");
        module2.setTitle("Module 2");
        module2.setModuleType(ModuleType.link);
        module2.setDuration(20L);
        module2.setCost(BigDecimal.ZERO);

        course.setId("course1");
        course.setTitle("Course 1");
        course.setShortDescription("Short Description 1");
    }

    @Test
    public void testBuildBookedBlendedCourse() {
        course.setModules(List.of(module1, module2));
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setModuleId("module1");
        moduleRecord.setEventDate(LocalDate.of(2025, 1, 1));
        moduleRecord.setEventId("event1");
        moduleRecord.setState(State.REGISTERED);

        ModuleRecord moduleRecord2 = new ModuleRecord();
        moduleRecord2.setState(State.COMPLETED);

        Optional<BookedLearningPlanCourse> result = learningPlanFactory.getBookedLearningPlanCourse(course, List.of(moduleRecord, moduleRecord2));
        assertTrue(result.isPresent());
        BookedLearningPlanCourse bookedLearningPlanCourse = result.get();
        assertEquals("course1", bookedLearningPlanCourse.getId());
        assertEquals("Course 1", bookedLearningPlanCourse.getTitle());
        assertEquals("Short Description 1", bookedLearningPlanCourse.getShortDescription());
        assertEquals("blended", bookedLearningPlanCourse.getType());
        assertEquals(140, bookedLearningPlanCourse.getDuration());
        assertEquals(2, bookedLearningPlanCourse.getModuleCount());
        assertEquals(10, bookedLearningPlanCourse.getCostInPounds());
        assertEquals(State.NULL, bookedLearningPlanCourse.getStatus());
        assertTrue(bookedLearningPlanCourse.isCanBeMovedToLearningPlan());

        assertEquals("module1", bookedLearningPlanCourse.getEventModule().getId());
        assertEquals("Module 1", bookedLearningPlanCourse.getEventModule().getTitle());
        assertEquals("event1", bookedLearningPlanCourse.getEventModule().getEventId());
        assertEquals(LocalDate.of(2025, 1, 1), bookedLearningPlanCourse.getEventModule().getBookedDate());
        assertEquals(LocalDate.of(2025, 1, 1), bookedLearningPlanCourse.getEventModule().getDates().get(0));
        assertEquals(State.REGISTERED, bookedLearningPlanCourse.getEventModule().getState());
    }

    @Test
    public void testBuildBookedFaceToFaceCourse() {
        course.setModules(List.of(module1));
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setModuleId("module1");
        moduleRecord.setEventDate(LocalDate.of(2025, 3, 1));
        moduleRecord.setEventId("event1");
        moduleRecord.setState(State.REGISTERED);

        Optional<BookedLearningPlanCourse> result = learningPlanFactory.getBookedLearningPlanCourse(course, List.of(moduleRecord));
        assertTrue(result.isPresent());
        BookedLearningPlanCourse bookedLearningPlanCourse = result.get();
        assertEquals("course1", bookedLearningPlanCourse.getId());
        assertEquals("Course 1", bookedLearningPlanCourse.getTitle());
        assertEquals("Short Description 1", bookedLearningPlanCourse.getShortDescription());
        assertEquals("face-to-face", bookedLearningPlanCourse.getType());
        assertEquals(120, bookedLearningPlanCourse.getDuration());
        assertEquals(1, bookedLearningPlanCourse.getModuleCount());
        assertEquals(10, bookedLearningPlanCourse.getCostInPounds());
        assertEquals(State.NULL, bookedLearningPlanCourse.getStatus());

        assertFalse(bookedLearningPlanCourse.isCanBeMovedToLearningPlan());

        assertEquals("module1", bookedLearningPlanCourse.getEventModule().getId());
        assertEquals("Module 1", bookedLearningPlanCourse.getEventModule().getTitle());
        assertEquals("event1", bookedLearningPlanCourse.getEventModule().getEventId());
        assertEquals(LocalDate.of(2025, 3, 1), bookedLearningPlanCourse.getEventModule().getBookedDate());
        assertEquals(LocalDate.of(2025, 1, 1), bookedLearningPlanCourse.getEventModule().getDates().get(0));
        assertEquals(State.REGISTERED, bookedLearningPlanCourse.getEventModule().getState());
    }

}
