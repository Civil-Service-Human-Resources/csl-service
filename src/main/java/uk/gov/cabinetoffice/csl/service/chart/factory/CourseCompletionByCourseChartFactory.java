package uk.gov.cabinetoffice.csl.service.chart.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionsChartBuilder;

@Slf4j
@Component
public class CourseCompletionByCourseChartFactory extends CourseCompletionChartFactoryBase<CourseCompletionAggregation> {

    public CourseCompletionByCourseChartFactory(IReportServiceClient reportServiceClient,
                                                CourseCompletionsChartBuilder<CourseCompletionAggregation> chartBuilder) {
        super(reportServiceClient, chartBuilder);
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BY_COURSE;
    }

    @Override
    AggregationResponse<CourseCompletionAggregation> getAggregations(OrganisationIdsCourseCompletionsParams params) {
        return reportServiceClient.getCourseCompletionAggregationsByCourse(params);
    }


}
