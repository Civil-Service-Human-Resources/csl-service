package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.controller.model.CancelBookingDto;
import uk.gov.cabinetoffice.csl.controller.model.EventResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
import uk.gov.cabinetoffice.csl.service.EventService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;

@Slf4j
@RestController
public class EventController {

    private final EventService eventService;
    private final IUserAuthService userAuthService;

    public EventController(EventService eventService, IUserAuthService userAuthService) {
        this.eventService = eventService;
        this.userAuthService = userAuthService;
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/create_booking", produces = "application/json")
    @ResponseBody
    public EventResponse bookEvent(@PathVariable("courseId") String courseId,
                                   @PathVariable("moduleId") String moduleId,
                                   @PathVariable("eventId") String eventId,
                                   @Valid @RequestBody BookEventDto dto) {
        log.debug("courseId: {}, moduleId: {}, eventId: {}", courseId, moduleId, eventId);
        String learnerId = userAuthService.getUsername();
        User user = new User(learnerId);
        return eventService.bookEvent(user, courseId, moduleId, eventId, dto);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/cancel_booking", produces = "application/json")
    @ResponseBody
    public EventResponse cancelBooking(@PathVariable("courseId") String courseId,
                                       @PathVariable("moduleId") String moduleId,
                                       @PathVariable("eventId") String eventId,
                                       @Valid @RequestBody CancelBookingDto dto) {
        log.debug("courseId: {}, moduleId: {}, eventId: {}", courseId, moduleId, eventId);
        String learnerId = userAuthService.getUsername();
        User user = new User(learnerId);
        return eventService.cancelBooking(user, courseId, moduleId, eventId, dto);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/complete_booking", produces = "application/json")
    @ResponseBody
    public EventResponse completeBooking(@PathVariable("courseId") String courseId,
                                         @PathVariable("moduleId") String moduleId,
                                         @PathVariable("eventId") String eventId,
                                         @Valid @RequestBody UserDetailsDto dto) {
        log.debug("courseId: {}, moduleId: {}, eventId: {}", courseId, moduleId, eventId);
        User user = User.fromUserDetails(userAuthService.getUsername(), dto);
        return eventService.completeBooking(user, courseId, moduleId, eventId);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/skip_booking", produces = "application/json")
    @ResponseBody
    public EventResponse skipBooking(@PathVariable("courseId") String courseId,
                                     @PathVariable("moduleId") String moduleId,
                                     @PathVariable("eventId") String eventId) {
        log.debug("courseId: {}, moduleId: {}, eventId: {}", courseId, moduleId, eventId);
        String learnerId = userAuthService.getUsername();
        return eventService.skipBooking(new User(learnerId), courseId, moduleId, eventId);
    }
}
