package uk.gov.cabinetoffice.csl.service.chart;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

@Component
public class CourseCompletionChartFactory extends CourseCompletionChartFactoryBase {

    public CourseCompletionChartFactory(IReportServiceClient reportServiceClient, ChartBuilder chartBuilder, CivilServantRegistryService civilServantRegistryService) {
        super(reportServiceClient, chartBuilder, civilServantRegistryService);
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BASIC;
    }

    @Override
    public CourseCompletionChart buildCourseCompletionsChart(OrganisationIdsCourseCompletionsParams params, IdentityDto user) {

        AggregationResponse<Aggregation> aggregations = reportServiceClient.getCourseCompletionAggregations(params);
        AggregationChart chart = chartBuilder.buildChartWithAggregations(params.getStartDateZoned(),
                params.getEndDateZoned(), params.getBinDelimiterVal().getChronoUnit(), aggregations.getResults());
        boolean hasRequests = this.getHasRequests(user);
        return new CourseCompletionChart(chart, new AggregationChart(), params, hasRequests);
    }
}
