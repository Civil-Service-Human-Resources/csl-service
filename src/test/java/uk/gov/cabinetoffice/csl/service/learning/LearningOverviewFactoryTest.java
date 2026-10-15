package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.FaceToFaceModuleOverview;
import uk.gov.cabinetoffice.csl.util.IUtilService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LearningOverviewFactoryTest extends TestDataService {

    @Mock
    private IUtilService utilService;

    private Module module;
    private Event event;

    @InjectMocks
    private LearningOverviewFactory service;

    @BeforeEach
    void setUp() {
        lenient().when(utilService.getNowDateTime()).thenReturn(LocalDateTime.of(2022, 10, 1, 12, 0));
        module = generateModule();
        event = generateEvent();
        module.setEvents(List.of(event));
    }

    @Test
    void getOverview_NoEventId_ChecksCanBeBooked() {
        FaceToFaceModuleOverview overview = service.getFaceToFaceModuleOverview(module, null, null);

        assertNull(overview.getEventId());
        assertTrue(overview.isCanBeBooked());
        assertFalse(overview.isCanBeCancelled());
    }

    @Test
    void getOverview_DifferentEventId_ChecksCanBeBooked() {
        FaceToFaceModuleOverview overview = service.getFaceToFaceModuleOverview(module, "event2", null);

        assertFalse(overview.isCanBeBooked());
        assertFalse(overview.isCanBeCancelled());
    }

    @Test
    void getOverview_DatePassed_ChecksCanBeBooked() {
        lenient().when(utilService.getNowDateTime()).thenReturn(LocalDateTime.of(2026, 10, 1, 12, 0));
        FaceToFaceModuleOverview overview = service.getFaceToFaceModuleOverview(module, "event2", null);

        assertFalse(overview.isCanBeBooked());
        assertFalse(overview.isCanBeCancelled());
    }

    @Test
    void getOverview_CanBeCancelled() {
        FaceToFaceModuleOverview overview = service.getFaceToFaceModuleOverview(module, "eventId", State.REGISTERED);

        assertEquals("eventId", overview.getEventId());
        assertFalse(overview.isCanBeBooked());
        assertTrue(overview.isCanBeCancelled());
    }

    @Test
    void getOverview_CantBeCancelled() {
        FaceToFaceModuleOverview overview = service.getFaceToFaceModuleOverview(module, "eventId", State.NULL);

        assertEquals("eventId", overview.getEventId());
        assertFalse(overview.isCanBeBooked());
        assertFalse(overview.isCanBeCancelled());
    }

    @Test
    void getOverview_CancelledEvent() {
        event.setStatus(EventStatus.CANCELLED);
        FaceToFaceModuleOverview overview = service.getFaceToFaceModuleOverview(module, "eventId", State.REGISTERED);

        assertEquals("eventId", overview.getEventId());
        assertFalse(overview.isCanBeCancelled());
    }

}
