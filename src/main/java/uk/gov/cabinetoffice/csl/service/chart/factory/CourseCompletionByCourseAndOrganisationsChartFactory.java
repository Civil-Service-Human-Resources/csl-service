package uk.gov.cabinetoffice.csl.service.chart.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionChartBuilderParams;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionsChartBuilder;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class CourseCompletionByCourseAndOrganisationsChartFactory extends CourseCompletionByCourseChartFactory {

    private final OrganisationalUnitListService organisationalUnitListService;

    public CourseCompletionByCourseAndOrganisationsChartFactory(IReportServiceClient reportServiceClient,
                                                                CourseCompletionsChartBuilder<CourseCompletionAggregation> chartBuilder,
                                                                OrganisationalUnitListService organisationalUnitListService) {
        super(reportServiceClient, chartBuilder);
        this.organisationalUnitListService = organisationalUnitListService;
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BY_ORGANISATION;
    }

    @Override
    protected ChartWithBreakdowns getAggregationsAndBuildCharts(OrganisationIdsCourseCompletionsParams params) {
        Map<Long, List<OrganisationalUnit>> organisationalMap = organisationalUnitListService.getOrganisationsWithChildrenAsFlatListMap(params.getOrganisationIds());
        List<CourseCompletionChartBuilderParams> chartBuilderParams = params.getOrganisationIds().stream().map(id -> {
                    List<OrganisationalUnit> organisationalUnits = organisationalMap.get(id);
                    if (organisationalUnits != null) {
                        params.setOrganisationIds(organisationalUnits.stream().map(OrganisationalUnit::getId).toList());
                        AggregationResponse<CourseCompletionAggregation> aggregations = getAggregations(params);
                        return new CourseCompletionChartBuilderParams(params, aggregations.getResults(), organisationalUnits.get(0).getFormattedName());
                    } else {
                        log.warn("Organisational unit with ID {} could not be built into a breakdown", id);
                        return null;
                    }
                })
                .filter(Objects::nonNull).toList();
        return chartBuilder.buildCourseCompletionCharts(chartBuilderParams);
    }

}
