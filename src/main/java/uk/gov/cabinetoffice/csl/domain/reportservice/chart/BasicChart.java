package uk.gov.cabinetoffice.csl.domain.reportservice.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicChart {
    protected Map<String, Integer> chart;
    protected String timezone;
    protected Integer total;
    protected String delimiter;
}
