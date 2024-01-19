package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;

@Component
public class BookingDtoFactory {
    private final String catalogueUrl;
    private final Clock clock;

    public BookingDtoFactory(@Value("${learningCatalogue.serviceUrl}") String catalogueUrl, Clock clock) {
        this.catalogueUrl = catalogueUrl;
        this.clock = clock;
    }

    public BookingDto createCancellation(BookingCancellationReason reason) {
        return BookingDto
                .builder()
                .cancellationReason(reason)
                .status(BookingStatus.CANCELLED).build();
    }

    public BookingDto createBooking(String learnerUid, CourseWithModuleWithEvent courseWithModuleWithEvent,
                                    BookEventDto dto) {

        String eventUrl = String.format("%s/%s", catalogueUrl, courseWithModuleWithEvent.getEventUrl());

        BookingDto.BookingDtoBuilder bookingBuilder = BookingDto.builder()
                .event(URI.create(eventUrl))
                .learner(learnerUid)
                .learnerEmail(dto.getLearnerEmail())
                .learnerName(dto.getLearnerName())
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
