package uk.gov.cabinetoffice.csl.domain.reportservice.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aggregation {
    protected LocalDateTime dateBin;
    protected Integer total;
}
