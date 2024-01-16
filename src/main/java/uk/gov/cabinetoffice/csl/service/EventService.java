package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.controller.model.EventResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.IModuleRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordUpdateService;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;

@Service
@Slf4j
public class EventService {

    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final ModuleRecordUpdateService moduleRecordUpdateService;
    private final BookingService bookingService;
    private final LearningCatalogueService learningCatalogueService;

    public EventService(LearnerRecordUpdateProcessor learnerRecordUpdateProcessor, ModuleRecordUpdateService moduleRecordUpdateService,
                        BookingService bookingService, LearningCatalogueService learningCatalogueService) {
        this.learnerRecordUpdateProcessor = learnerRecordUpdateProcessor;
        this.moduleRecordUpdateService = moduleRecordUpdateService;
        this.bookingService = bookingService;
        this.learningCatalogueService = learningCatalogueService;
    }

    public EventResponse bookEvent(String learnerId, String courseId, String moduleId, String eventId, BookEventDto dto) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        bookingService.createBooking(learnerId, courseWithModuleWithEvent, dto);
        IModuleRecordUpdate update;
        if (courseWithModuleWithEvent.getModule().isFree()) {
            log.info("Module is free; automatically approving booking");
            update = moduleRecordUpdateService.getApproveEventUpdate(courseWithModuleWithEvent.getEvent());
        } else {
            log.info("Module is not free; requesting a booking");
            update = moduleRecordUpdateService.getRegisterEventUpdate(courseWithModuleWithEvent.getEvent());
        }
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update);
        return new EventResponse("Module was successfully booked", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId, eventId, courseWithModuleWithEvent.getEvent().getStartTime());
    }
}
