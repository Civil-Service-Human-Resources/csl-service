package uk.gov.cabinetoffice.csl.domain.learningcatalogue.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.DateRange;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Venue;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event implements Serializable {

    private String id;
    private List<DateRange> dateRanges = new ArrayList<>();
    private EventCancellationReason cancellationReason;
    private EventStatus status;
    private Venue venue;

    public LocalDate getStartTime() {
        return dateRanges.get(0).getDate();
    }

    @JsonIgnore
    public String getStartTimeAsString() {
        return this.getStartTime().format(DateTimeFormatter.ofPattern("dd MMM uuuu"));
    }

}
