package uk.gov.cabinetoffice.csl.domain.learnerrecord.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventCancellationReason;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;

import java.net.URI;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {
    private Integer id;
    private String uid;
    private URI uri;
    private EventStatus status;
    private EventCancellationReason cancellationReason;
    private Integer activeBookingCount;
    private List<BookingDto> bookings;
}
