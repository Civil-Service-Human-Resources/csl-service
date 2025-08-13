package uk.gov.cabinetoffice.csl.service.chart.factory;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionWithOrganisationAggregation;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionsWithOrganisationsChartBuilder;

@Component
public class CourseCompletionByCourseAndOrganisationsChartFactory extends CourseCompletionChartFactoryBase<CourseCompletionWithOrganisationAggregation> {

    public CourseCompletionByCourseAndOrganisationsChartFactory(IReportServiceClient reportServiceClient,
                                                                CourseCompletionsWithOrganisationsChartBuilder chartBuilder) {
        super(reportServiceClient, chartBuilder);
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BY_ORGANISATION;
    }

    @Override
    AggregationResponse<CourseCompletionWithOrganisationAggregation> getAggregations(OrganisationIdsCourseCompletionsParams params) {
        return reportServiceClient.getCourseCompletionAggregationsByCourseAndOrganisation(params);
    }

}
