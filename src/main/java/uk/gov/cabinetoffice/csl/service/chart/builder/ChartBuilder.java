package uk.gov.cabinetoffice.csl.service.chart.builder;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.IAggregation;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ChartBuilder<T extends IAggregation> {

    public AggregationChart buildBasicChart(OrganisationIdsCourseCompletionsParams params) {
        return buildBasicChart(params.getStartDateZoned(), params.getEndDateZoned(), params.getBinDelimiterVal().getChronoUnit());
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

    public ChartWithBreakdowns buildCourseCompletionCharts(OrganisationIdsCourseCompletionsParams params, List<T> aggregations) {
        AggregationChart chart = this.buildBasicChart(params);
        return buildCourseCompletionCharts(chart, aggregations);
    }

    public ChartWithBreakdowns buildCourseCompletionCharts(AggregationChart chart, List<T> aggregations) {
        for (IAggregation result : aggregations) {
            String stringedDateTime = result.getDateBin().format(DateTimeFormatter.ISO_DATE_TIME);
            chart.putAndAggregate(stringedDateTime, result.getTotal());
        }
        return new ChartWithBreakdowns(chart);
    }

    public ChartWithBreakdowns buildCourseCompletionCharts(ZonedDateTime startDate, ZonedDateTime endDate, ChronoUnit unit, List<T> aggregations) {
        AggregationChart chart = this.buildBasicChart(startDate, endDate, unit);
        return buildCourseCompletionCharts(chart, aggregations);
    }
}
