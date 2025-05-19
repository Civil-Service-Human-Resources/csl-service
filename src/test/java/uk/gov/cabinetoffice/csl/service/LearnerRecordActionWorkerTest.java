package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.CourseRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordData;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction.COMPLETE_COURSE;

@ExtendWith(MockitoExtension.class)
public class LearnerRecordActionWorkerTest extends TestDataService {

    private final Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
    private final LearnerRecordActionWorker learnerRecordWorker = new LearnerRecordActionWorker(new LearnerRecordDataFactory());
    private final ILearnerRecordActionType mockActionType = mock(ILearnerRecordActionType.class);
    private final TypedLearnerRecordResourceId id = new CourseRecordResourceId(getUserId(), getCourseId());

    @BeforeEach
    public void setup() {
        reset();
    }

    private ActionWithId getAction() {
        return new ActionWithId(id, mockActionType);
    }

    @Test
    public void testPerformActionNewRecordWithEvent() {
        ActionWithId action = getAction();
        when(mockActionType.canCreateRecord()).thenReturn(true);
        LearnerRecordData result = learnerRecordWorker.processAction(null, action);

        assertTrue(result.isNewRecord());
        assertEquals(id, result.getResourceId());

        assertEquals(1, result.getEvents().size());
        assertTrue(result.getEvents().get(0).isNewEvent());
        assertEquals(mockActionType, result.getEvents().get(0).getActionType());
    }

    @Test
    public void testPerformActionCannotCreateRecord() {
        ActionWithId action = getAction();
        when(mockActionType.canCreateRecord()).thenReturn(false);
        assertNull(learnerRecordWorker.processAction(null, action));
    }

    @Test
    public void testPerformActionExistingRecordNoExistingEvent() {
        ActionWithId action = getAction();
        when(mockActionType.canCreateRecord()).thenReturn(true);
        LearnerRecord learnerRecord = mock(LearnerRecord.class);
        when(learnerRecord.getLearnerRecordId()).thenReturn(id);
        when(learnerRecord.getCreatedTimestamp()).thenReturn(LocalDateTime.now(clock));

        LearnerRecordData result = learnerRecordWorker.processAction(learnerRecord, action);

        assertFalse(result.isNewRecord());
        assertEquals(id, result.getResourceId());

        assertEquals(1, result.getEvents().size());
        assertTrue(result.getEvents().get(0).isNewEvent());
        assertEquals(mockActionType, result.getEvents().get(0).getActionType());
    }

    @Test
    public void testPerformActionExistingRecordExistingEventShouldNotRepeat() {
        ActionWithId action = getAction();
        LearnerRecordEvent learnerRecordEvent = mock(LearnerRecordEvent.class);
        when(learnerRecordEvent.getActionType()).thenReturn(mockActionType);
        when(learnerRecordEvent.getEventTimestamp()).thenReturn(LocalDateTime.now(clock));
        LearnerRecord learnerRecord = mock(LearnerRecord.class);
        when(learnerRecord.getLearnerRecordId()).thenReturn(id);
        when(learnerRecord.getCreatedTimestamp()).thenReturn(LocalDateTime.now(clock));
        when(learnerRecord.getLatestEvent()).thenReturn(learnerRecordEvent);

        LearnerRecordData result = learnerRecordWorker.processAction(learnerRecord, action);

        assertFalse(result.isNewRecord());
        assertEquals(id, result.getResourceId());

        assertEquals(1, result.getEvents().size());
        assertFalse(result.getEvents().get(0).isNewEvent());
        assertEquals(mockActionType, result.getEvents().get(0).getActionType());
    }

    @Test
    public void testPerformActionExistingRecordExistingEventCanRepeat() {
        ActionWithId action = getAction();
        when(mockActionType.canRepeat()).thenReturn(true);
        LearnerRecordEvent learnerRecordEvent = mock(LearnerRecordEvent.class);
        when(learnerRecordEvent.getActionType()).thenReturn(mockActionType);
        when(learnerRecordEvent.getEventTimestamp()).thenReturn(LocalDateTime.now(clock));
        LearnerRecord learnerRecord = mock(LearnerRecord.class);
        when(learnerRecord.getLearnerRecordId()).thenReturn(id);
        when(learnerRecord.getCreatedTimestamp()).thenReturn(LocalDateTime.now(clock));
        when(learnerRecord.getLatestEvent()).thenReturn(learnerRecordEvent);

        LearnerRecordData result = learnerRecordWorker.processAction(learnerRecord, action);

        assertFalse(result.isNewRecord());
        assertEquals(id, result.getResourceId());

        assertEquals(2, result.getEvents().size());
        assertFalse(result.getEvents().get(0).isNewEvent());
        assertEquals(mockActionType, result.getEvents().get(0).getActionType());
        assertTrue(result.getEvents().get(1).isNewEvent());
        assertEquals(mockActionType, result.getEvents().get(1).getActionType());
    }

    @Test
    public void testPerformActionExistingRecordNewEvent() {
        ActionWithId action = getAction();
        LearnerRecordEvent learnerRecordEvent = mock(LearnerRecordEvent.class);
        when(learnerRecordEvent.getActionType()).thenReturn(COMPLETE_COURSE);
        when(learnerRecordEvent.getEventTimestamp()).thenReturn(LocalDateTime.now(clock));
        LearnerRecord learnerRecord = mock(LearnerRecord.class);
        when(learnerRecord.getLearnerRecordId()).thenReturn(id);
        when(learnerRecord.getCreatedTimestamp()).thenReturn(LocalDateTime.now(clock));
        when(learnerRecord.getLatestEvent()).thenReturn(learnerRecordEvent);

        LearnerRecordData result = learnerRecordWorker.processAction(learnerRecord, action);

        assertFalse(result.isNewRecord());
        assertEquals(id, result.getResourceId());

        assertEquals(2, result.getEvents().size());
        assertFalse(result.getEvents().get(0).isNewEvent());
        assertEquals(COMPLETE_COURSE, result.getEvents().get(0).getActionType());
        assertTrue(result.getEvents().get(1).isNewEvent());
        assertEquals(mockActionType, result.getEvents().get(1).getActionType());
    }
}
