package uk.gov.cabinetoffice.csl.controller.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookingOverview {

    private Integer id;
    private String reference;
    private String learnerEmail;
    private BookingStatus status;
    @Nullable
    private String poNumber;

}
