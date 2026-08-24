package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseStatusServiceTest extends TestDataService {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @Mock
    private LearnerRecordDataUtils learnerRecordDataUtils;

    @InjectMocks
    private CourseStatusService courseStatusService;

    private Course course1;
    private Course course2;
    private Course course3;

    @BeforeEach
    void setup() {
        course1 = this.generateCourse(2);
        course1.setId("course1");
        course1.setAudiences(List.of(this.generateRequiredAudience("CO")));
        course2 = this.generateCourse(2);
        course2.setId("course2");
        course3 = this.generateCourse(2);
        course3.setId("course3");
        when(learningCatalogueService.getCourses(List.of("course1", "course2", "course3")))
                .thenReturn(List.of(course1, course2, course3));
        when(userDetailsService.getUserWithUid("UID"))
                .thenReturn(this.generateUser());
    }

    @Test
    void getStateForCoursesNoProgress() {
        Map<String, State> stateForCourses = courseStatusService.getStateForCourses("UID", List.of("course1", "course2", "course3"));
        assertEquals(State.NULL, stateForCourses.get("course1"));
        assertEquals(State.NULL, stateForCourses.get("course2"));
        assertEquals(State.NULL, stateForCourses.get("course3"));
    }

    @Test
    void getStateForCoursesInProgress() {
        var course1Modules = mock(ModuleRecordCollection.class);
        when(course1Modules.getLatestUpdatedDate())
                .thenReturn(LocalDateTime.MIN.plusWeeks(1L));
        when(learnerRecordDataUtils.getModuleRecordsForCourses("UID", List.of(course1, course2, course3)))
                .thenReturn(Map.of("course1", course1Modules));
        Map<String, State> stateForCourses = courseStatusService.getStateForCourses("UID", List.of("course1", "course2", "course3"));
        assertEquals(State.IN_PROGRESS, stateForCourses.get("course1"));
        assertEquals(State.NULL, stateForCourses.get("course2"));
        assertEquals(State.NULL, stateForCourses.get("course3"));
    }

    @Test
    void getStateForCoursesInCompleted() {
        when(learnerRecordDataUtils.getCompletionDatesForCourses("UID", List.of("course1", "course2", "course3")))
                .thenReturn(Map.of("course1", LocalDateTime.MIN.plusWeeks(1L)));
        Map<String, State> stateForCourses = courseStatusService.getStateForCourses("UID", List.of("course1", "course2", "course3"));
        assertEquals(State.COMPLETED, stateForCourses.get("course1"));
        assertEquals(State.NULL, stateForCourses.get("course2"));
        assertEquals(State.NULL, stateForCourses.get("course3"));
    }
}
