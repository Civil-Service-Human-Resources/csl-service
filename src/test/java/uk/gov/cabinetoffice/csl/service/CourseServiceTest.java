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
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordUpdateFactory;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class CourseServiceTest {

    @Mock
    private LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    @Mock
    private CourseRecordUpdateFactory courseRecordUpdateFactory;

    @InjectMocks
    private CourseService courseService;

    private TestDataService testDataService;
    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final ICourseRecordUpdate update = mock(ICourseRecordUpdate.class);

    @BeforeEach
    public void setup() {
        testDataService = new TestDataService();
        when(update.getName()).thenReturn("fake update");
        reset();
    }

    @Test
    public void shouldAddToLearningPlan() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(courseRecordUpdateFactory.getAddToLearningPlanUpdate())
                .thenReturn(update);
        when(learnerRecordUpdateProcessor.processCourseRecordAction(learnerId, courseId, update))
                .thenReturn(courseRecord);
        CourseResponse result = courseService.addToLearningPlan(learnerId, courseId);
        assertEquals("Successfully applied action 'fake update' to course record", result.getMessage());
        assertEquals(courseId, result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    @Test
    public void shouldRemoveFromLearningPlan() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(courseRecordUpdateFactory.getRemoveFromLearningPlanUpdate())
                .thenReturn(update);
        when(learnerRecordUpdateProcessor.processCourseRecordAction(learnerId, courseId, update))
                .thenReturn(courseRecord);
        CourseResponse result = courseService.removeFromLearningPlan(learnerId, courseId);
        assertEquals("Successfully applied action 'fake update' to course record", result.getMessage());
        assertEquals(courseId, result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    @Test
    public void shouldRemoveFromSuggestions() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(courseRecordUpdateFactory.getRemoveFromSuggestionsUpdate())
                .thenReturn(update);
        when(learnerRecordUpdateProcessor.processCourseRecordAction(learnerId, courseId, update))
                .thenReturn(courseRecord);
        CourseResponse result = courseService.removeFromSuggestions(learnerId, courseId);
        assertEquals("Successfully applied action 'fake update' to course record", result.getMessage());
        assertEquals(courseId, result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

}
