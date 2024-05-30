package uk.gov.cabinetoffice.csl.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.controller.model.CancelBookingDto;
import uk.gov.cabinetoffice.csl.controller.model.EventResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.EventModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;

@Service
@Slf4j
@AllArgsConstructor
public class EventService {

    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final BookingService bookingService;
    private final LearningCatalogueService learningCatalogueService;

    public EventResponse bookEvent(User user, String courseId, String moduleId, String eventId, BookEventDto dto) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        bookingService.createBooking(user.getId(), courseWithModuleWithEvent, dto);
        EventModuleRecordAction actionType = courseWithModuleWithEvent.getModule().isFree() ? EventModuleRecordAction.APPROVE_BOOKING : EventModuleRecordAction.REGISTER_BOOKING;
        return processCourseRecordActionWithResponse(user, courseWithModuleWithEvent, actionType);
    }

    public EventResponse cancelBooking(User user, String courseId, String moduleId, String eventId, CancelBookingDto dto) {
        bookingService.cancelBooking(user.getId(), eventId, dto.getReason());
        return cancelBooking(user, courseId, moduleId, eventId);
    }

    public EventResponse completeBooking(User user, String courseId, String moduleId, String eventId) {
        return processCourseRecordActionWithResponse(user, courseId, moduleId, eventId, EventModuleRecordAction.COMPLETE_BOOKING);
    }

    public EventResponse skipBooking(User user, String courseId, String moduleId, String eventId) {
        return processCourseRecordActionWithResponse(user, courseId, moduleId, eventId, EventModuleRecordAction.SKIP_BOOKING);
    }

    public EventResponse cancelBookingWithBookingId(String courseId, String moduleId, String eventId, String bookingId, CancelBookingDto cancelBookingDto) {
        BookingDto dto = bookingService.cancelBookingWithId(eventId, bookingId, cancelBookingDto.getReason());
        return cancelBooking(new User(dto.getLearner()), courseId, moduleId, eventId);
    }

    public EventResponse approveBookingWithBookingId(String courseId, String moduleId, String eventId, String bookingId) {
        BookingDto dto = bookingService.approveBookingWithId(eventId, bookingId);
        return processCourseRecordActionWithResponse(new User(dto.getLearner()), courseId, moduleId, eventId, EventModuleRecordAction.APPROVE_BOOKING);
    }

    private EventResponse processCourseRecordActionWithResponse(User user, CourseWithModuleWithEvent courseWithModuleWithEvent, EventModuleRecordAction actionType) {
        learnerRecordUpdateProcessor.processEventModuleRecordAction(courseWithModuleWithEvent, user, actionType);
        return EventResponse.fromMetaData(actionType, courseWithModuleWithEvent);
    }

    private EventResponse processCourseRecordActionWithResponse(User user, String courseId, String moduleId, String eventId, EventModuleRecordAction actionType) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        return processCourseRecordActionWithResponse(user, courseWithModuleWithEvent, actionType);
    }

    private EventResponse cancelBooking(User user, String courseId, String moduleId, String eventId) {
        return processCourseRecordActionWithResponse(user, courseId, moduleId, eventId, EventModuleRecordAction.CANCEL_BOOKING);
    }
}
