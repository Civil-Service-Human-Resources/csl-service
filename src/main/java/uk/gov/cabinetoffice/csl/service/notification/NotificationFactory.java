package uk.gov.cabinetoffice.csl.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventCancellationReason;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;
import uk.gov.cabinetoffice.csl.service.notification.messages.NotifyLineManagerCompletedLearning;
import uk.gov.cabinetoffice.csl.service.notification.messages.NotifyUserCancelledEvent;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationFactory {

    public IEmail getRequiredLearningCompleteMessage(User user, Course course) {
        return new NotifyLineManagerCompletedLearning(user.getLineManagerEmail(), user.getLineManagerName(),
                user.getName(), user.getEmail(), course.getTitle());
    }

    public IEmail getNotifyUserOfCancelledEventMessage(CourseWithModuleWithEvent courseWithModuleWithEvent,
                                                       BookingDto bookingDto, EventCancellationReason cancellationReason) {
        Event event = courseWithModuleWithEvent.getEvent();
        return new NotifyUserCancelledEvent(bookingDto.getLearnerEmail(), cancellationReason.getValue(),
                courseWithModuleWithEvent.getCourse().getTitle(), event.getStartTimeAsString(), event.getVenue().getLocation(), bookingDto.getBookingReference());
    }

    public List<IEmail> getNotifyUserOfCancelledEventMessage(CourseWithModuleWithEvent courseWithModuleWithEvent,
                                                             List<BookingDto> bookings, EventCancellationReason cancellationReason) {
        List<IEmail> emails = new ArrayList<>();
        bookings.forEach(b -> emails.add(getNotifyUserOfCancelledEventMessage(courseWithModuleWithEvent, b, cancellationReason)));
        return emails;
    }

}
