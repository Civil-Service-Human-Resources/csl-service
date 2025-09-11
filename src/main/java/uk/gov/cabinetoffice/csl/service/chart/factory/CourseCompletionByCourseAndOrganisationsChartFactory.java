package uk.gov.cabinetoffice.csl.service.chart.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.controller.model.SelectedOrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionWithOrganisationAggregation;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionChartBuilderParams;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionsWithOrganisationChartBuilder;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class CourseCompletionByCourseAndOrganisationsChartFactory extends CourseCompletionChartFactoryBase<CourseCompletionWithOrganisationAggregation> {

    private final CourseCompletionsWithOrganisationChartBuilder chartBuilder;

    protected CourseCompletionByCourseAndOrganisationsChartFactory(OrganisationalUnitService organisationalUnitService,
                                                                   IReportServiceClient reportServiceClient, CourseCompletionsWithOrganisationChartBuilder chartBuilder) {
        super(organisationalUnitService, reportServiceClient, chartBuilder);
        this.chartBuilder = chartBuilder;
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BY_ORGANISATION;
    }

    @Override
    AggregationResponse<CourseCompletionWithOrganisationAggregation> getAggregations(OrganisationIdsCourseCompletionsParams params) {
        return reportServiceClient.getCourseCompletionAggregationsByCourseAndOrganisation(params);
    }

    @Override
    protected OrganisationIdsCourseCompletionsParams getParamsFromApiParams(SelectedOrganisationIdsCourseCompletionsParams apiParams) {
        OrganisationIdsCourseCompletionsParams params = new OrganisationIdsCourseCompletionsParams();
        params.setStartDate(apiParams.getStartDate());
        params.setEndDate(apiParams.getEndDate());
        params.setTimezone(ZoneId.of(apiParams.getTimezone()));
        params.setCourseIds(apiParams.getCourseIds());
        params.setProfessionIds(apiParams.getProfessionIds());
        params.setGradeIds(apiParams.getGradeIds());
        params.setOrganisationIds(apiParams.getSelectedOrganisationIds());
        return params;
    }

    @Override
    protected ChartWithBreakdowns getAggregationsAndBuildCharts(OrganisationIdsCourseCompletionsParams params) {
        Map<Long, List<OrganisationalUnit>> organisationalMap = organisationalUnitService.getOrganisationsWithChildrenAsFlatListMap(params.getOrganisationIds());
        List<CourseCompletionChartBuilderParams<CourseCompletionWithOrganisationAggregation>> chartBuilderParams = params.getOrganisationIds().stream().map(id -> {
                    List<OrganisationalUnit> organisationalUnits = organisationalMap.get(id);
                    if (organisationalUnits != null) {
                        params.setOrganisationIds(organisationalUnits.stream().map(OrganisationalUnit::getId).toList());
                        AggregationResponse<CourseCompletionWithOrganisationAggregation> aggregations = getAggregations(params);
                        return new CourseCompletionChartBuilderParams<>(params, aggregations.getResults(), organisationalUnits.get(0).getFormattedName());
                    } else {
                        log.warn("Organisational unit with ID {} could not be built into a breakdown", id);
                        return null;
                    }
                })
                .filter(Objects::nonNull).toList();
        return chartBuilder.buildCourseCompletionCharts(chartBuilderParams);
    }

}
