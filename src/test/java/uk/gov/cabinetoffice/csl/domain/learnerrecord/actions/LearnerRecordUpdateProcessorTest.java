package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.notification.INotificationService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class LearnerRecordUpdateProcessorTest extends TestDataService {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private IMessagingClient iMessagingClient;

    @Mock
    private INotificationService notificationService;

    @InjectMocks
    private LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final Course course = generateCourse(true, false);

    private final ICourseRecordAction action = mock(ICourseRecordAction.class);

    @BeforeEach
    public void before() {
        reset();
    }

    @Test
    public void shouldCreateCourseRecord() {
        CourseRecord courseRecord = generateCourseRecord(false);
        when(learnerRecordService.getCourseRecord(getUserId(), getCourseId()))
                .thenReturn(null);
        when(action.generateNewCourseRecord())
                .thenReturn(courseRecord);
        when(action.getCourseId()).thenReturn(courseRecord.getCourseId());
        when(action.getUserId()).thenReturn(courseRecord.getUserId());
        when(learnerRecordService.createCourseRecord(courseRecord))
                .thenReturn(courseRecord);
        CourseRecord resp = learnerRecordUpdateProcessor.processCourseRecordAction(action);
        verify(learnerRecordService, never()).updateCourseRecord(courseRecord);
        assertEquals(courseRecord, resp);
    }

    @Test
    public void shouldUpdateCourseRecord() {
        CourseRecord courseRecord = generateCourseRecord(false);
        when(learnerRecordService.getCourseRecord(getUserId(), getCourseId()))
                .thenReturn(courseRecord);
        when(action.getCourseId()).thenReturn(courseRecord.getCourseId());
        when(action.getUserId()).thenReturn(courseRecord.getUserId());
        when(action.applyUpdatesToCourseRecord(courseRecord))
                .thenReturn(courseRecord);
        when(learnerRecordService.updateCourseRecord(courseRecord))
                .thenReturn(courseRecord);
        CourseRecord resp = learnerRecordUpdateProcessor.processCourseRecordAction(action);
        verify(learnerRecordService, never()).createCourseRecord(courseRecord);
        assertEquals(courseRecord, resp);
    }

    @Test
    public void shouldBustLearnerRecordCacheOnError() {
        when(learnerRecordService.getCourseRecord(getUserId(), getCourseId())).thenThrow(new RuntimeException("Error"));
        when(action.getCourseId()).thenReturn(getCourseId());
        when(action.getUserId()).thenReturn(getUserId());
        assertThrowsExactly(RuntimeException.class, () -> {
            learnerRecordUpdateProcessor.processCourseRecordAction(action);
        });
        verify(learnerRecordService, atMostOnce()).bustCourseRecordCache(getUserId(), getCourseId());
    }
}
