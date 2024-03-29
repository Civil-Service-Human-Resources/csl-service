package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingCancellationReason;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CancelBookingDto {
    @NotNull
    BookingCancellationReason reason;
}
