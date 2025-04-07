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

import java.util.List;
import java.util.Map;

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
        Map<String, CourseRecord> map = Map.of(courseRecord.getId(), courseRecord);
        when(action.getCourseRecordId()).thenReturn(getCourseRecordId());
        when(learnerRecordService.getCourseRecords(List.of(getCourseRecordId())))
                .thenReturn(List.of());
        when(action.generateNewCourseRecord())
                .thenReturn(courseRecord);
        when(action.getCourseId()).thenReturn(courseRecord.getCourseId());
        when(action.getUserId()).thenReturn(courseRecord.getUserId());
        when(learnerRecordService.createCourseRecords(List.of(courseRecord)))
                .thenReturn(List.of(courseRecord));
        CourseRecord resp = learnerRecordUpdateProcessor.processCourseRecordAction(action, null);
        verify(learnerRecordService, never()).updateCourseRecords(map);
        assertEquals(courseRecord, resp);
    }

    @Test
    public void shouldUpdateCourseRecord() {
        CourseRecord courseRecord = generateCourseRecord(false);
        Map<String, CourseRecord> map = Map.of(courseRecord.getId(), courseRecord);
        when(action.getCourseRecordId()).thenReturn(getCourseRecordId());
        when(learnerRecordService.getCourseRecords(List.of(getCourseRecordId())))
                .thenReturn(List.of(courseRecord));
        when(action.getCourseId()).thenReturn(courseRecord.getCourseId());
        when(action.getUserId()).thenReturn(courseRecord.getUserId());
        when(action.applyUpdatesToCourseRecord(courseRecord))
                .thenReturn(courseRecord);
        when(learnerRecordService.updateCourseRecords(map))
                .thenReturn(map);
        CourseRecord resp = learnerRecordUpdateProcessor.processCourseRecordAction(action, null);
        verify(learnerRecordService, never()).createCourseRecords(List.of(courseRecord));
        assertEquals(courseRecord, resp);
    }

    @Test
    public void shouldBustLearnerRecordCacheOnError() {
        when(learnerRecordService.getCourseRecords(List.of(getCourseRecordId()))).thenThrow(new RuntimeException("Error"));
        when(action.getCourseRecordId()).thenReturn(getCourseRecordId());
        when(action.getUserId()).thenReturn(getUserId());
        assertThrowsExactly(RuntimeException.class, () -> learnerRecordUpdateProcessor.processCourseRecordAction(action, null));
        verify(learnerRecordService, atMostOnce()).bustCourseRecordCache(getCourseRecordId());
    }
}
