package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.time.Clock;
import java.time.Instant;

@Component
public class BookingDtoFactory {
    private final Clock clock;

    public BookingDtoFactory(Clock clock) {
        this.clock = clock;
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

    public BookingDto createBooking(String learnerUid, Module module, BookEventDto dto) {
        BookingDto.BookingDtoBuilder bookingBuilder = BookingDto.builder()
                .learner(learnerUid)
                .bookingTime(Instant.now(clock));

        if (!dto.getAccessibilityOptions().isEmpty()) {
            String joinedAccessibilityOptions = String.join(",", dto.getAccessibilityOptions());
            bookingBuilder.accessibilityOptions(joinedAccessibilityOptions);
        } else {
            bookingBuilder.accessibilityOptions("");
        }

        if (module.isFree()) {
            bookingBuilder.status(BookingStatus.CONFIRMED);
        } else {
            bookingBuilder.status(BookingStatus.REQUESTED);
            bookingBuilder.poNumber(dto.getPoNumber());
        }

        return bookingBuilder.build();
    }

}
