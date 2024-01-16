package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.controller.model.EventResponse;
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
                                   @Valid @RequestBody BookEventDto bookEventDto) {
        log.debug("courseId: {}, moduleId: {}, eventId: {}", courseId, moduleId, eventId);
        String learnerId = userAuthService.getUsername();
        return eventService.bookEvent(learnerId, courseId, moduleId, eventId, bookEventDto);
    }
}
