package uk.gov.cabinetoffice.csl.service.chart.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CourseCompletionChartBuilderParams<A extends CourseCompletionAggregation> {

    private final OrganisationIdsCourseCompletionsParams params;
    private final List<A> aggregations;
    private final String chartTitle;

}
