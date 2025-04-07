package uk.gov.cabinetoffice.csl.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.controller.model.CancelBookingDto;
import uk.gov.cabinetoffice.csl.controller.model.CancelEventDto;
import uk.gov.cabinetoffice.csl.controller.model.EventResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.UserToAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.EventModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.notification.INotificationService;
import uk.gov.cabinetoffice.csl.service.notification.NotificationFactory;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EventService {

    private final LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;
    private final NotificationFactory notificationFactory;
    private final INotificationService notificationService;
    private final ILearnerRecordClient learnerRecordClient;
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
        learnerRecordUpdateProcessor.processEventModuleRecordAction(courseWithModuleWithEvent, user, actionType, null);
        return EventResponse.fromMetaData(actionType, courseWithModuleWithEvent);
    }

    private EventResponse processCourseRecordActionWithResponse(User user, String courseId, String moduleId, String eventId, EventModuleRecordAction actionType) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        return processCourseRecordActionWithResponse(user, courseWithModuleWithEvent, actionType);
    }

    private EventResponse cancelBooking(User user, String courseId, String moduleId, String eventId) {
        return processCourseRecordActionWithResponse(user, courseId, moduleId, eventId, EventModuleRecordAction.CANCEL_BOOKING);
    }

    private void cancelBookings(List<BookingDto> bookingDtos, CourseWithModuleWithEvent courseWithModuleWithEvent) {
        List<UserToAction<EventModuleRecordAction>> actions = bookingDtos.stream()
                .map(b -> new UserToAction<>(new User(b.getLearner()), EventModuleRecordAction.CANCEL_BOOKING)).toList();
        learnerRecordUpdateProcessor.processMultipleEventModuleRecordActions(courseWithModuleWithEvent, actions, null);
    }

    public void cancelEvent(String courseId, String moduleId, String eventId, CancelEventDto cancelEventDto) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        List<BookingDto> activeBookings = bookingService.getBookings(eventId)
                .stream().filter(b -> !b.getStatus().equals(BookingStatus.CANCELLED)).toList();
        learningCatalogueService.cancelEvent(courseWithModuleWithEvent, cancelEventDto);
        learnerRecordClient.updateEvent(eventId, new EventStatusDto(EventStatus.CANCELLED, cancelEventDto.getReason()));
        cancelBookings(activeBookings, courseWithModuleWithEvent);
        List<IEmail> emails = notificationFactory.getNotifyUserOfCancelledEventMessage(courseWithModuleWithEvent, activeBookings, cancelEventDto.getReason());
        notificationService.sendEmails(emails);
    }
}
