package uk.gov.cabinetoffice.csl.client.reportService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import uk.gov.cabinetoffice.csl.domain.reportservice.ReportType;

@ConfigurationProperties(prefix = "report-service")
@Getter
@RequiredArgsConstructor
public class ReportServiceConfiguration {
    private final String courseCompletionsAggregationsByOrganisationUrl;
    private final String courseCompletionsAggregationsByCourseUrl;
    private final String courseCompletionsAggregationsUrl;
    private final String requestCourseCompletionReportUrl;
    private final String requestRegisteredLearnerReportUrl;

    public String getReportRequestUrl(ReportType reportType) {
        return switch (reportType) {
            case COURSE_COMPLETIONS -> requestCourseCompletionReportUrl;
            case REGISTERED_LEARNER -> requestRegisteredLearnerReportUrl;
        };
    }
}
