package uk.gov.cabinetoffice.csl.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.controller.model.*;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordActionFactory;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.UserToModuleAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.notification.INotificationService;
import uk.gov.cabinetoffice.csl.service.notification.NotificationFactory;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class EventService {

    private final UserDetailsService userDetailsService;
    private final ModuleRecordActionFactory moduleRecordActionFactory;
    private final ModuleActionService moduleActionService;
    private final NotificationFactory notificationFactory;
    private final INotificationService notificationService;
    private final ILearnerRecordClient learnerRecordClient;
    private final BookingService bookingService;
    private final LearningCatalogueService learningCatalogueService;

    public EventResponse bookEvent(String userId, String courseId, String moduleId, String eventId, BookEventDto dto) {
        User user = userDetailsService.getUserWithUid(userId);
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        BookingDto booking = bookingService.createBooking(userId, courseWithModuleWithEvent, dto);
        Event event = courseWithModuleWithEvent.getEvent();
        IModuleAction actionType;
        List<IEmail> emails = new ArrayList<>();
        if (booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            actionType = moduleRecordActionFactory.getApproveBookingAction(event);
            emails.addAll(notificationFactory.getNotifyUserAndLineManagerOfCreatedBookingMessage(courseWithModuleWithEvent, user, booking));
        } else {
            actionType = moduleRecordActionFactory.getRegisterEventAction(event);
            emails.addAll(notificationFactory.getNotifyUserAndLineManagerOfRequestedBookingMessage(courseWithModuleWithEvent, user, booking));
        }
        EventResponse respose = processCourseRecordActionWithResponse(courseWithModuleWithEvent, new UserToModuleAction(userId, actionType));
        notificationService.sendEmails(emails);
        return respose;
    }

    public EventResponse cancelBooking(String userId, String courseId, String moduleId, String eventId, CancelBookingDto dto) {
        bookingService.cancelBooking(userId, eventId, dto.getReason());
        return cancelBooking(userId, courseId, moduleId, eventId);
    }

    public EventResponse completeBooking(String userId, String courseId, String moduleId, String eventId) {
        IModuleAction action = moduleRecordActionFactory.getCompleteBookingAction();
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        moduleActionService.completeModule(courseWithModuleWithEvent, userId, action);
        return EventResponse.fromMetaData(action.getAction(), courseWithModuleWithEvent);
    }

    public EventResponse skipBooking(String userId, String courseId, String moduleId, String eventId) {
        UserToModuleAction action = new UserToModuleAction(userId, moduleRecordActionFactory.getSkipBookingAction());
        return processCourseRecordActionWithResponse(courseId, moduleId, eventId, action);
    }

    public BookingResponse cancelBookingWithBookingId(String courseId, String moduleId, String eventId, String bookingId, CancelBookingDto cancelBookingDto) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        BookingDto dto = bookingService.cancelBookingWithId(eventId, bookingId, cancelBookingDto.getReason());
        User user = userDetailsService.getUserWithUid(dto.getLearner());
        cancelBooking(dto.getLearner(), courseId, moduleId, eventId);
        List<IEmail> emails = notificationFactory.getNotifyUserAndLineManagerOfCancelledBookingMessage(courseWithModuleWithEvent, user, dto);
        notificationService.sendEmails(emails);
        return new BookingResponse(bookingId, dto.getLearner());
    }

    public BookingResponse approveBookingWithBookingId(String courseId, String moduleId, String eventId, String bookingId) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        BookingDto dto = bookingService.approveBookingWithId(eventId, bookingId);
        User user = userDetailsService.getUserWithUid(dto.getLearner());
        UserToModuleAction action = new UserToModuleAction(dto.getLearner(), moduleRecordActionFactory.getApproveBookingAction(courseWithModuleWithEvent.getEvent()));
        processCourseRecordActionWithResponse(courseWithModuleWithEvent, action);
        List<IEmail> emails = notificationFactory.getNotifyUserAndLineManagerOfCreatedBookingMessage(courseWithModuleWithEvent, user, dto);
        notificationService.sendEmails(emails);
        return new BookingResponse(bookingId, dto.getLearner());
    }

    private EventResponse processCourseRecordActionWithResponse(CourseWithModuleWithEvent courseWithModuleWithEvent, UserToModuleAction action) {
        moduleActionService.processModuleAction(courseWithModuleWithEvent, action);
        return EventResponse.fromMetaData(action.getAction().getAction(), courseWithModuleWithEvent);
    }

    private EventResponse processCourseRecordActionWithResponse(String courseId, String moduleId, String eventId, UserToModuleAction action) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        return processCourseRecordActionWithResponse(courseWithModuleWithEvent, action);
    }

    private EventResponse cancelBooking(String userId, String courseId, String moduleId, String eventId) {
        UserToModuleAction action = new UserToModuleAction(userId, moduleRecordActionFactory.getCancelBookingAction());
        return processCourseRecordActionWithResponse(courseId, moduleId, eventId, action);
    }

    private void cancelBookings(List<BookingDto> bookingDtos, CourseWithModuleWithEvent courseWithModuleWithEvent) {
        List<UserToModuleAction> actions = bookingDtos.stream()
                .map(b -> new UserToModuleAction(b.getLearner(), moduleRecordActionFactory.getCancelBookingAction())).toList();
        moduleActionService.processModuleActions(courseWithModuleWithEvent, actions);
    }

    public CancelEventResponse cancelEvent(String courseId, String moduleId, String eventId, CancelEventDto cancelEventDto) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        List<BookingDto> activeBookings = bookingService.getBookings(eventId)
                .stream().filter(b -> !b.getStatus().equals(BookingStatus.CANCELLED)).toList();
        learningCatalogueService.cancelEvent(courseWithModuleWithEvent, cancelEventDto);
        learnerRecordClient.updateEvent(eventId, new EventStatusDto(EventStatus.CANCELLED, cancelEventDto.getReason()));
        cancelBookings(activeBookings, courseWithModuleWithEvent);
        Map<String, String> uidsToEmails = new HashMap<>();
        userDetailsService.fetchByUids(activeBookings.stream().map(BookingDto::getLearner).collect(Collectors.toList()))
                .forEach((uid, identityDto) -> uidsToEmails.put(uid, identityDto.getUsername()));
        List<IEmail> emails = notificationFactory.getNotifyUserOfCancelledEventMessage(courseWithModuleWithEvent, uidsToEmails, activeBookings, cancelEventDto.getReason());
        notificationService.sendEmails(emails);
        return new CancelEventResponse(courseId, moduleId, eventId, activeBookings.stream().map(BookingDto::getLearner).toList());
    }

}
