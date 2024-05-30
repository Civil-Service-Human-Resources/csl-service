package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LearningPeriod implements Serializable {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public boolean isDateWithinPeriod(LocalDateTime date) {
        return date.isAfter(startDate);
    }
}
