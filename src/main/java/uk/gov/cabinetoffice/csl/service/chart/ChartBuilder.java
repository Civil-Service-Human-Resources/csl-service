package uk.gov.cabinetoffice.csl.service.chart;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.IAggregation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ChartBuilder {

    public AggregationChart buildBasicChart(List<String> textRows) {
        AggregationChart chart = new AggregationChart();
        textRows.forEach(row -> chart.put(row, 0));
        return chart;
    }

    public AggregationChart buildBasicChart(ZonedDateTime startDate, ZonedDateTime endDate,
                                            ChronoUnit interval) {
        AggregationChart chart = new AggregationChart();
        LocalDateTime nextDate = startDate.withFixedOffsetZone().toLocalDateTime();
        nextDate = nextDate.with(LocalTime.MIDNIGHT);
        while (!nextDate.isAfter(endDate.toLocalDateTime())) {
            String label = nextDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            chart.putAndAggregate(label, 0);
            nextDate = nextDate.plus(1, interval);
        }
        return chart;
    }

    public AggregationChart buildChartWithAggregations(ZonedDateTime startDate, ZonedDateTime endDate,
                                                       ChronoUnit interval, List<Aggregation> aggregations) {
        AggregationChart chart = this.buildBasicChart(startDate, endDate, interval);
        for (IAggregation result : aggregations) {
            String stringedDateTime = result.getDateBin().format(DateTimeFormatter.ISO_DATE_TIME);
            chart.putAndAggregate(stringedDateTime, result.getTotal());
        }
        return chart;
    }

}
