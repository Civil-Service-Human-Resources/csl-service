package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.IAggregation;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationResponse<A extends IAggregation> {
    private String timezone;
    private AggregationBinDelimiter delimiter;
    private List<A> results;

}
