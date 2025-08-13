package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;
import uk.gov.cabinetoffice.csl.service.chart.builder.ChartBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ChartBuilderTest {

    ChartBuilder<Aggregation> chartBuilder = new ChartBuilder<>();


    @Test
    void testBuildBasicChart() {
        ZoneId zoneId = ZoneId.of("+1");
        ZonedDateTime startDate = LocalDateTime.of(2024, 1, 1, 23, 0, 0)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(zoneId);
        ZonedDateTime endDate = LocalDateTime.of(2024, 1, 2, 11, 0, 0)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(zoneId);
        ChronoUnit unit = ChronoUnit.HOURS;
        chartBuilder.buildBasicChart(startDate, endDate, unit);
        AggregationChart result = chartBuilder.buildBasicChart(startDate, endDate, unit);
        assertEquals(13, result.size());
        assertEquals(0, result.get("2024-01-02T00:00:00"));
        assertEquals(0, result.get("2024-01-02T01:00:00"));
        assertEquals(0, result.get("2024-01-02T02:00:00"));
        assertEquals(0, result.get("2024-01-02T03:00:00"));
        assertEquals(0, result.get("2024-01-02T04:00:00"));
        assertEquals(0, result.get("2024-01-02T05:00:00"));
        assertEquals(0, result.get("2024-01-02T06:00:00"));
        assertEquals(0, result.get("2024-01-02T07:00:00"));
        assertEquals(0, result.get("2024-01-02T08:00:00"));
        assertEquals(0, result.get("2024-01-02T09:00:00"));
        assertEquals(0, result.get("2024-01-02T10:00:00"));
        assertEquals(0, result.get("2024-01-02T11:00:00"));
        assertEquals(0, result.get("2024-01-02T12:00:00"));
    }

    @Test
    public void testBuildAggregationChart() {
        ZoneId zoneId = ZoneId.of("Europe/London");
        ZonedDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(zoneId);
        ZonedDateTime endDate = LocalDateTime.of(2024, 1, 2, 0, 0, 0)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(zoneId);
        ChronoUnit unit = ChronoUnit.HOURS;
        LocalDateTime date1 = LocalDateTime.of(2024, 1, 1, 10, 0, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 1, 1, 15, 0, 0, 0);
        List<Aggregation> aggregations = new ArrayList<>();
        aggregations.add(new Aggregation(date1, 10));
        aggregations.add(new Aggregation(date1, 17));
        aggregations.add(new Aggregation(date1, 42));
        aggregations.add(new Aggregation(date2, 100));
        aggregations.add(new Aggregation(date2, 21));
        AggregationChart result = chartBuilder.buildCourseCompletionCharts(startDate, endDate, unit, aggregations).getChart();
        assertEquals(190, result.getTotal());
        assertEquals(69, result.get("2024-01-01T10:00:00"));
        assertEquals(121, result.get("2024-01-01T15:00:00"));
    }

}
