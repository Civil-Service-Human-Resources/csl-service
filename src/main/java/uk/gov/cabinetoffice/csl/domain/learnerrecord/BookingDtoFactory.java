package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;

import java.net.URI;

@Component
public class BookingDtoFactory {
    private final String catalogueUrl;

    public BookingDtoFactory(@Value("${learningCatalogue.serviceUrl}") String catalogueUrl) {
        this.catalogueUrl = catalogueUrl;
    }

    public BookingDto createBooking(String learnerUid, CourseWithModuleWithEvent courseWithModuleWithEvent,
                                    BookEventDto dto) {

        String eventUrl = String.format("%s/%s", catalogueUrl, courseWithModuleWithEvent.getEventUrl());

        BookingDto.BookingDtoBuilder bookingBuilder = BookingDto.builder()
                .event(URI.create(eventUrl))
                .learner(learnerUid)
                .learnerEmail(dto.getLearnerEmail());

        if (!dto.getAccessibilityOptions().isEmpty()) {
            String joinedAccessibilityOptions = String.join(",", dto.getAccessibilityOptions());
            bookingBuilder.accessibilityOptions(joinedAccessibilityOptions);
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