package uk.gov.cabinetoffice.csl.domain.reportservice.chart;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlotPoint {
    private final String x;
    private final Integer y;
}
