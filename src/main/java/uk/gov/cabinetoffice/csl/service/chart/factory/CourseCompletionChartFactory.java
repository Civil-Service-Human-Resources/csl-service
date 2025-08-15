package uk.gov.cabinetoffice.csl.service.chart.factory;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.ChartBuilder;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;

@Component
public class CourseCompletionChartFactory extends CourseCompletionChartFactoryBase<Aggregation> {

    protected CourseCompletionChartFactory(OrganisationalUnitListService organisationalUnitListService,
                                           IReportServiceClient reportServiceClient, ChartBuilder<Aggregation> chartBuilder) {
        super(organisationalUnitListService, reportServiceClient, chartBuilder);
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BASIC;
    }

    @Override
    AggregationResponse<Aggregation> getAggregations(OrganisationIdsCourseCompletionsParams params) {
        return reportServiceClient.getCourseCompletionAggregations(params);
    }


}
