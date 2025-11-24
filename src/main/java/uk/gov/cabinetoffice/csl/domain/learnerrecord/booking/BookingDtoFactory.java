package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;

@Component
public class BookingDtoFactory {
    private final String catalogueUrl;
    private final Clock clock;
    private final UserDetailsService userDetailsService;

    public BookingDtoFactory(@Value("${learningCatalogue.serviceUrl}") String catalogueUrl, Clock clock, UserDetailsService userDetailsService) {
        this.catalogueUrl = catalogueUrl;
        this.clock = clock;
        this.userDetailsService = userDetailsService;
    }

    public BookingDto createCancellation(BookingCancellationReason reason) {
        return BookingDto
                .builder()
                .cancellationReason(reason)
                .status(BookingStatus.CANCELLED).build();
    }

    public BookingDto createApprovedBooking() {
        return BookingDto
                .builder()
                .status(BookingStatus.CONFIRMED).build();
    }

    public BookingDto createBooking(String learnerUid, CourseWithModuleWithEvent courseWithModuleWithEvent,
                                    BookEventDto dto) {

        String eventUrl = String.format("%s/%s", catalogueUrl, courseWithModuleWithEvent.getEventUrl());
        User user = userDetailsService.getUserWithUid(learnerUid);
        BookingDto.BookingDtoBuilder bookingBuilder = BookingDto.builder()
                .event(URI.create(eventUrl))
                .learner(learnerUid)
                .learnerEmail(user.getEmail())
                .learnerName(user.getName())
                .bookingTime(Instant.now(clock));

        if (!dto.getAccessibilityOptions().isEmpty()) {
            String joinedAccessibilityOptions = String.join(",", dto.getAccessibilityOptions());
            bookingBuilder.accessibilityOptions(joinedAccessibilityOptions);
        } else {
            bookingBuilder.accessibilityOptions("");
        }

        if (courseWithModuleWithEvent.getModule().isFree()) {
            bookingBuilder.status(BookingStatus.CONFIRMED);
        } else {
            bookingBuilder.status(BookingStatus.REQUESTED);
            bookingBuilder.poNumber(dto.getPoNumber());
        }

        return bookingBuilder.build();
    }

}
