package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class LearnerRecordUpdateProcessorTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @InjectMocks
    private LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    private TestDataService testDataService;
    private String courseId;
    private String learnerId;
    private ICourseRecordUpdate update = mock(ICourseRecordUpdate.class);

    @BeforeEach
    public void before() {
        testDataService = new TestDataService();
        courseId = testDataService.getCourseId();
        learnerId = testDataService.getUserId();
        reset();
    }

    @Test
    public void shouldCreateCourseRecord() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(false);
        CourseRecordStatus status = CourseRecordStatus.builder().build();
        when(learnerRecordService.getCourseRecord(learnerId, courseId))
                .thenReturn(null);
        when(update.getCreateCourseRecordStatus())
                .thenReturn(status);
        when(learnerRecordService.createCourseRecord(learnerId, courseId, status))
                .thenReturn(courseRecord);
        when(learnerRecordService.updateCourseRecordCache(courseRecord))
                .thenReturn(courseRecord);
        CourseRecord resp = learnerRecordUpdateProcessor.processCourseRecordAction(learnerId, courseId, update);
        verify(learnerRecordService, never()).updateCourseRecord(any(), any(), any());
        assertEquals(courseRecord, resp);
    }

    @Test
    public void shouldUpdateCourseRecord() {
        CourseRecord courseRecord = testDataService.generateCourseRecord(false);
        List<PatchOp> patches = List.of(PatchOp.replacePatch("state", "COMPLETED"));
        when(learnerRecordService.getCourseRecord(learnerId, courseId))
                .thenReturn(courseRecord);
        when(update.getUpdateCourseRecordPatches(courseRecord))
                .thenReturn(patches);
        when(learnerRecordService.updateCourseRecord(learnerId, courseId, patches))
                .thenReturn(courseRecord);
        when(learnerRecordService.updateCourseRecordCache(courseRecord))
                .thenReturn(courseRecord);
        CourseRecord resp = learnerRecordUpdateProcessor.processCourseRecordAction(learnerId, courseId, update);
        verify(learnerRecordService, never()).createCourseRecord(any(), any(), any());
        assertEquals(courseRecord, resp);
    }
}
