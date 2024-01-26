package uk.gov.cabinetoffice.csl.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.controller.model.CancelBookingDto;
import uk.gov.cabinetoffice.csl.controller.model.EventResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.IModuleRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordUpdateService;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Event;

@Service
@Slf4j
@AllArgsConstructor
public class EventService {

    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final ModuleRecordUpdateService moduleRecordUpdateService;
    private final BookingService bookingService;
    private final LearningCatalogueService learningCatalogueService;

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

    private EventResponse cancelBooking(String learnerId, String courseId, String moduleId, Event event) {
        IModuleRecordUpdate update = moduleRecordUpdateService.getCancelBookingUpdate();
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update);
        return new EventResponse("Module booking was successfully cancelled", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId, event.getId(), event.getStartTime());
    }

    public EventResponse cancelBooking(String learnerId, String courseId, String moduleId, String eventId, CancelBookingDto dto) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        bookingService.cancelBooking(learnerId, eventId, dto.getReason());
        return cancelBooking(learnerId, courseId, moduleId, courseWithModuleWithEvent.getEvent());
    }

    public EventResponse completeBooking(String learnerId, String courseId, String moduleId, String eventId) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        IModuleRecordUpdate update = moduleRecordUpdateService.getCompleteBookingUpdate(courseWithModuleWithEvent.getCourse());
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update);
        return new EventResponse("Module booking was successfully completed", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId, eventId, courseWithModuleWithEvent.getEvent().getStartTime());
    }

    public EventResponse skipBooking(String learnerId, String courseId, String moduleId, String eventId) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        IModuleRecordUpdate update = moduleRecordUpdateService.getSkipBookingUpdate();
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update);
        return new EventResponse("Module booking was successfully skipped", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId, eventId, courseWithModuleWithEvent.getEvent().getStartTime());
    }

    public EventResponse cancelBookingWithBookingId(String courseId, String moduleId, String eventId, String bookingId, CancelBookingDto cancelBookingDto) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        BookingDto dto = bookingService.cancelBookingWithId(bookingId, cancelBookingDto.getReason());
        return cancelBooking(dto.getLearner(), courseId, moduleId, courseWithModuleWithEvent.getEvent());
    }

    public EventResponse approveBookingWithBookingId(String courseId, String moduleId, String eventId, String bookingId) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        BookingDto dto = bookingService.approveBookingWithId(bookingId);
        IModuleRecordUpdate update = moduleRecordUpdateService.getApproveEventUpdate(courseWithModuleWithEvent.getEvent());
        CourseRecord courseRecord = learnerRecordUpdateProcessor.processModuleRecordAction(dto.getLearner(), courseId, moduleId, update);
        return new EventResponse("Module was successfully booked", courseRecord.getCourseTitle(),
                courseRecord.getModuleRecord(moduleId).getModuleTitle(), courseId, moduleId, eventId, courseWithModuleWithEvent.getEvent().getStartTime());
    }
}
