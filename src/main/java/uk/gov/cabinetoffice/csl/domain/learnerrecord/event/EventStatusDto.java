package uk.gov.cabinetoffice.csl.domain.learnerrecord.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventCancellationReason;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStatusDto {
    private EventStatus status;
    private EventCancellationReason cancellationReason;
}
