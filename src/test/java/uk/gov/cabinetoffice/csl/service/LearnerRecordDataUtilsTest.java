package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LearnerRecordDataUtilsTest {

    LearnerRecordService learnerRecordService = mock(LearnerRecordService.class);
    LearnerRecordDataUtils learnerRecordDataUtils = new LearnerRecordDataUtils(learnerRecordService);

    @Test
    void getLearnerRecordEventsNormalisedMostRecent() {
        LearnerRecordEvent e1 = new LearnerRecordEvent();
        e1.setResourceId("course1");
        e1.setEventTimestamp(LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0));
        LearnerRecordEvent e2 = new LearnerRecordEvent();
        e2.setResourceId("course2");
        e2.setEventTimestamp(LocalDateTime.of(2026, 1, 1, 10, 0, 0, 0));
        LearnerRecordEvent e3 = new LearnerRecordEvent();
        e3.setResourceId("course2");
        e3.setEventTimestamp(LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0));

        List<LearnerRecordEvent> events = List.of(e1, e2, e3);
        when(learnerRecordService.getLearnerRecordEvents(any())).thenReturn(events);

        Map<String, LearnerRecordEvent> map = learnerRecordDataUtils.getLearnerRecordEventsNormalisedMostRecent(LearnerRecordEventQuery.builder().build());
        assertEquals(2, map.size());
        assertEquals(2026, map.get("course2").getEventTimestamp().getYear());
    }
}
