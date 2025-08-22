package uk.gov.cabinetoffice.csl.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.BookingResponse;
import uk.gov.cabinetoffice.csl.controller.model.CancelBookingDto;
import uk.gov.cabinetoffice.csl.controller.model.CancelEventDto;
import uk.gov.cabinetoffice.csl.controller.model.CancelEventResponse;
import uk.gov.cabinetoffice.csl.service.EventService;

@Slf4j
@RestController
@AllArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PostMapping(path = "/admin/courses/{courseId}/modules/{moduleId}/events/{eventId}/cancel", produces = "application/json")
    @ResponseBody
    public CancelEventResponse cancelEvent(@PathVariable("courseId") String courseId,
                                           @PathVariable("moduleId") String moduleId,
                                           @PathVariable("eventId") String eventId,
                                           @Valid @RequestBody CancelEventDto cancelEventDto) {
        return eventService.cancelEvent(courseId, moduleId, eventId, cancelEventDto);
    }

    @PostMapping(path = "/admin/courses/{courseId}/modules/{moduleId}/events/{eventId}/bookings/{bookingId}/cancel_booking", produces = "application/json")
    @ResponseBody
    public BookingResponse bookEvent(@PathVariable("courseId") String courseId,
                                     @PathVariable("moduleId") String moduleId,
                                     @PathVariable("eventId") String eventId,
                                     @PathVariable("bookingId") String bookingId,
                                     @Valid @RequestBody CancelBookingDto cancelBookingDto) {
        return eventService.cancelBookingWithBookingId(courseId, moduleId, eventId, bookingId, cancelBookingDto);
    }

    @PostMapping(path = "/admin/courses/{courseId}/modules/{moduleId}/events/{eventId}/bookings/{bookingId}/approve_booking", produces = "application/json")
    @ResponseBody
    public BookingResponse approveBooking(@PathVariable("courseId") String courseId,
                                          @PathVariable("moduleId") String moduleId,
                                          @PathVariable("eventId") String eventId,
                                          @PathVariable("bookingId") String bookingId) {
        return eventService.approveBookingWithBookingId(courseId, moduleId, eventId, bookingId);
    }
}
