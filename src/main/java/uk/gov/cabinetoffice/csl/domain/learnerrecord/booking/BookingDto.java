package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Integer id;
    private String learner;
    private String eventUid;
    private BookingStatus status;
    private Instant bookingTime = Instant.now();
    private Instant confirmationTime;
    private Instant cancellationTime;
    private URI paymentDetails;
    private String poNumber;
    private String bookingReference;
    private String accessibilityOptions = "";
    private BookingCancellationReason cancellationReason;
}
