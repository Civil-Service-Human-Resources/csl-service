package uk.gov.cabinetoffice.csl.service.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventCancellationReason;
import uk.gov.cabinetoffice.csl.service.notification.messages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NotificationFactory {

    private final String lpgUiBaseUrl;

    public NotificationFactory(@Value("${ui.lpg.baseUrl}") String lpgUiBaseUrl) {
        this.lpgUiBaseUrl = lpgUiBaseUrl;
    }

    public IEmail getRequiredLearningCompleteMessage(User user, Course course) {
        return new NotifyLineManagerCompletedLearning(user.getLineManagerEmail(), user.getLineManagerName(),
                user.getName(), user.getEmail(), course.getTitle());
    }

    public IEmail getNotifyUserOfCancelledEventMessage(CourseWithModuleWithEvent courseWithModuleWithEvent,
                                                       String learnerEmail, BookingDto bookingDto, EventCancellationReason cancellationReason) {
        Event event = courseWithModuleWithEvent.getEvent();
        return new NotifyUserCancelledEvent(learnerEmail, cancellationReason.getValue(),
                courseWithModuleWithEvent.getCourse().getTitle(), event.getStartTimeAsString(), event.getVenue().getLocation(), bookingDto.getBookingReference());
    }

    public List<IEmail> getNotifyUserOfCancelledEventMessage(CourseWithModuleWithEvent courseWithModuleWithEvent,
                                                             Map<String, String> uidToEmailMap, List<BookingDto> bookings, EventCancellationReason cancellationReason) {
        List<IEmail> emails = new ArrayList<>();
        bookings.forEach(b -> emails.add(getNotifyUserOfCancelledEventMessage(courseWithModuleWithEvent, uidToEmailMap.get(b.getLearner()), b, cancellationReason)));
        return emails;
    }

    public List<IEmail> getNotifyUserAndLineManagerOfCancelledBookingMessage(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, BookingDto booking) {
        Course course = courseWithModuleWithEvent.getCourse();
        Module module = courseWithModuleWithEvent.getModule();
        Event event = courseWithModuleWithEvent.getEvent();
        String eventTime = event.getStartTimeAsString();
        String eventLocation = event.getVenue().getLocation();
        return List.of(
                new CancelBookingMessageParams(user.getEmail(), booking.getCancellationReason().getValue(), user.getName(), booking.getBookingReference(),
                        course.getTitle(), eventTime, eventLocation),
                new CancelBookingLMMessageParams(user.getLineManagerEmail(), user.getName(), user.getEmail(),
                        course.getTitle(), eventTime, eventLocation,
                        module.getCost().toString(), booking.getBookingReference())
        );
    }

    public List<IEmail> getNotifyUserAndLineManagerOfCreatedBookingMessage(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, BookingDto booking) {
        Course course = courseWithModuleWithEvent.getCourse();
        Module module = courseWithModuleWithEvent.getModule();
        Event event = courseWithModuleWithEvent.getEvent();
        String eventTime = event.getStartTimeAsString();
        String eventLocation = event.getVenue().getLocation();
        return List.of(
                new ConfirmBookingMessageParams(user.getEmail(), booking.getAccessibilityOptions(), booking.getBookingReference(),
                        course.getTitle(), eventTime, eventLocation),
                new ConfirmBookingLMMessageParams(user.getLineManagerEmail(), user.getName(), user.getEmail(),
                        course.getTitle(), eventTime, eventLocation,
                        module.getCost().toString(), booking.getBookingReference())
        );
    }

    public List<IEmail> getNotifyUserAndLineManagerOfRequestedBookingMessage(CourseWithModuleWithEvent courseWithModuleWithEvent, User user, BookingDto booking) {
        Course course = courseWithModuleWithEvent.getCourse();
        Module module = courseWithModuleWithEvent.getModule();
        Event event = courseWithModuleWithEvent.getEvent();
        String eventTime = event.getStartTimeAsString();
        String eventLocation = event.getVenue().getLocation();
        return List.of(
                new RequestBookingMessageParams(user.getEmail(), course.getTitle(), eventTime, eventLocation,
                        booking.getAccessibilityOptions(), booking.getBookingReference()),
                new RequestBookingLMMessageParams(user.getLineManagerEmail(), user.getName(), user.getEmail(),
                        course.getTitle(), eventTime, eventLocation,
                        module.getCost().toString(), booking.getBookingReference())
        );
    }

    public IEmail getInviteUserToEventNotification(CourseWithModuleWithEvent courseWithModuleWithEvent, String userEmail) {
        Course course = courseWithModuleWithEvent.getCourse();
        Module module = courseWithModuleWithEvent.getModule();
        Event event = courseWithModuleWithEvent.getEvent();
        String eventTime = event.getStartTimeAsString();
        String eventLocation = event.getVenue().getLocation();
        String inviteUrl = String.format("%s/book/%s/%s/choose-date", lpgUiBaseUrl, course.getId(), module.getId());
        return new InviteLearnerToEventMessageParams(userEmail, course.getTitle(), eventTime, eventLocation, inviteUrl);
    }
}
