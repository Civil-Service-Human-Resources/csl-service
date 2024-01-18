package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.annotations.ValidEnum;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.BookingCancellationReason;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CancelBookingDto {
    @ValidEnum(enumClass = BookingCancellationReason.class)
    BookingCancellationReason reason;
}
