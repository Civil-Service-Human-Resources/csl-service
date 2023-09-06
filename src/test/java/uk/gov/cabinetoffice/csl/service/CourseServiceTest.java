package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordActionService;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordActionProcessor;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class CourseServiceTest {

    @Mock
    private LearnerRecordActionProcessor learnerRecordActionProcessor;

    @Mock
    private CourseRecordActionService courseRecordActionService;

    @InjectMocks
    private CourseService courseService;

    private TestDataService testDataService;
    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final CourseRecordUpdate update = mock(CourseRecordUpdate.class);

    @BeforeEach
    public void setup() {
        testDataService = new TestDataService();
        when(update.getName()).thenReturn("fake update");
        reset();
    }

    @Test
    public void shouldAddToLearningPlan() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(courseRecordActionService.getAddToLearningPlanUpdate())
                .thenReturn(update);
        when(learnerRecordActionProcessor.processCourseRecordAction(learnerId, courseId, update))
                .thenReturn(courseRecord);
        CourseResponse result = courseService.addToLearningPlan(learnerId, courseId);
        assertEquals("Successfully applied action 'fake update' to course record", result.getMessage());
        assertEquals(courseId, result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    @Test
    public void shouldRemoveFromLearningPlan() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(courseRecordActionService.getRemoveFromLearningPlanUpdate())
                .thenReturn(update);
        when(learnerRecordActionProcessor.processCourseRecordAction(learnerId, courseId, update))
                .thenReturn(courseRecord);
        CourseResponse result = courseService.removeFromLearningPlan(learnerId, courseId);
        assertEquals("Successfully applied action 'fake update' to course record", result.getMessage());
        assertEquals(courseId, result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    @Test
    public void shouldRemoveFromSuggestions() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(courseRecordActionService.getRemoveFromSuggestionsUpdate())
                .thenReturn(update);
        when(learnerRecordActionProcessor.processCourseRecordAction(learnerId, courseId, update))
                .thenReturn(courseRecord);
        CourseResponse result = courseService.removeFromSuggestions(learnerId, courseId);
        assertEquals("Successfully applied action 'fake update' to course record", result.getMessage());
        assertEquals(courseId, result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

}
