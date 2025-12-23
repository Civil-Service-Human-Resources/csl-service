package uk.gov.cabinetoffice.csl.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.*;
import uk.gov.cabinetoffice.csl.service.EventService;
import uk.gov.cabinetoffice.csl.service.InviteService;

@Slf4j
@AllArgsConstructor
@RestController("/admin")
public class AdminEventController {

    private final EventService eventService;
    private final InviteService inviteService;

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/cancel", produces = "application/json")
    @ResponseBody
    public CancelEventResponse cancelEvent(@PathVariable("courseId") String courseId,
                                           @PathVariable("moduleId") String moduleId,
                                           @PathVariable("eventId") String eventId,
                                           @Valid @RequestBody CancelEventDto cancelEventDto) {
        return eventService.cancelEvent(courseId, moduleId, eventId, cancelEventDto);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/bookings/{bookingId}/cancel_booking", produces = "application/json")
    @ResponseBody
    public BookingResponse bookEvent(@PathVariable("courseId") String courseId,
                                     @PathVariable("moduleId") String moduleId,
                                     @PathVariable("eventId") String eventId,
                                     @PathVariable("bookingId") String bookingId,
                                     @Valid @RequestBody CancelBookingDto cancelBookingDto) {
        return eventService.cancelBookingWithBookingId(courseId, moduleId, eventId, bookingId, cancelBookingDto);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/bookings/{bookingId}/approve_booking", produces = "application/json")
    @ResponseBody
    public BookingResponse approveBooking(@PathVariable("courseId") String courseId,
                                          @PathVariable("moduleId") String moduleId,
                                          @PathVariable("eventId") String eventId,
                                          @PathVariable("bookingId") String bookingId) {
        return eventService.approveBookingWithBookingId(courseId, moduleId, eventId, bookingId);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/invite", produces = "application/json")
    @ResponseBody
    public void inviteLearnerToEvent(@PathVariable("courseId") String courseId,
                                     @PathVariable("moduleId") String moduleId,
                                     @PathVariable("eventId") String eventId,
                                     @Valid @RequestBody EmailAddressDto emailAddressDto) {
        inviteService.inviteLearnerToEvent(courseId, moduleId, eventId, emailAddressDto);
    }

}
