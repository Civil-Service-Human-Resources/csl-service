package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class LearningPeriod implements Serializable {

    @Nullable
    private LocalDate startDate;
    private LocalDate endDate;

    @JsonIgnore
    public LocalDateTime getStartDateAsDateTime() {
        return startDate == null ? LocalDateTime.MIN : startDate.atTime(LocalTime.MAX);
    }

}
