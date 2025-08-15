package uk.gov.cabinetoffice.csl.client.reportService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "report-service")
@Getter
@RequiredArgsConstructor
public class ReportServiceConfiguration {
    private final String courseCompletionsAggregationsByOrganisationUrl;
    private final String courseCompletionsAggregationsByCourseUrl;
    private final String courseCompletionsAggregationsUrl;
    private final String requestCourseCompletionReportUrl;
}
