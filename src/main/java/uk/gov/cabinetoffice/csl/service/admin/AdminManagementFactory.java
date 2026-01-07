package uk.gov.cabinetoffice.csl.service.admin;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.admin.model.BookingOverview;
import uk.gov.cabinetoffice.csl.controller.admin.model.EventOverview;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.invite.InviteDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Venue;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

import java.util.List;
import java.util.Map;

@Service
public class AdminManagementFactory {

    public EventOverview createEventOverview(CourseWithModuleWithEvent courseWithModuleWithEvent, EventDto eventDto, Map<String, String> uidsToEmails) {
        List<BookingOverview> bookings = eventDto.getActiveBookings()
                .stream().map(b -> new BookingOverview(b.getId(), b.getBookingReference(), uidsToEmails.get(b.getLearner()), b.getStatus()))
                .filter(b -> b.getLearnerEmail() != null)
                .toList();
        Course course = courseWithModuleWithEvent.getCourse();
        Module module = courseWithModuleWithEvent.getModule();
        Event event = courseWithModuleWithEvent.getEvent();
        Venue venue = event.getVenue();
        venue.setAvailability(venue.getCapacity() - eventDto.getActiveBookings().size());
        return new EventOverview(event.getId(), event.getVenue(), event.getDateRangesAsStrings(), event.getStatus().getValue(),
                event.getCancellationReason().getValue(), module.getId(), module.getTitle(), course.getId(), course.getTitle(), course.getStatus().getName(),
                eventDto.getInvites().stream().map(InviteDto::getLearnerEmail).toList(), bookings);
    }
}
