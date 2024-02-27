package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class CourseServiceTest extends TestDataService {

    @Mock
    private LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @InjectMocks
    private CourseService courseService;

    private final Course course = generateCourse(false, false);
    private final User user = generateUser();

    @BeforeEach
    public void setup() {
        reset();
    }

    @Test
    public void shouldAddToLearningPlan() {
        when(learningCatalogueService.getCourse(getCourseId())).thenReturn(course);
        verify(learnerRecordUpdateProcessor, atMostOnce()).processCourseRecordAction(course, user, CourseRecordAction.ADD_TO_LEARNING_PLAN);
        CourseResponse result = courseService.addToLearningPlan(user, getCourseId());
        assertEquals("Successfully applied action 'Add to learning plan' to course record", result.getMessage());
        assertEquals(getCourseId(), result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    @Test
    public void shouldRemoveFromLearningPlan() {
        when(learningCatalogueService.getCourse(getCourseId())).thenReturn(course);
        verify(learnerRecordUpdateProcessor, atMostOnce()).processCourseRecordAction(course, user, CourseRecordAction.REMOVE_FROM_LEARNING_PLAN);
        CourseResponse result = courseService.removeFromLearningPlan(user, getCourseId());
        assertEquals("Successfully applied action 'Remove from learning plan' to course record", result.getMessage());
        assertEquals(getCourseId(), result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    @Test
    public void shouldRemoveFromSuggestions() {
        when(learningCatalogueService.getCourse(getCourseId())).thenReturn(course);
        verify(learnerRecordUpdateProcessor, atMostOnce()).processCourseRecordAction(course, user, CourseRecordAction.REMOVE_FROM_SUGGESTIONS);
        CourseResponse result = courseService.removeFromSuggestions(user, getCourseId());
        assertEquals("Successfully applied action 'Remove from suggestions' to course record", result.getMessage());
        assertEquals(getCourseId(), result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

}
