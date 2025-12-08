package uk.gov.cabinetoffice.csl.client.reportService;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import uk.gov.cabinetoffice.csl.domain.reportservice.ReportType;

@ConfigurationProperties(prefix = "report-service")
@Getter
@Setter
@Validated
public class ReportServiceConfiguration {
    @NotNull
    private String courseCompletionsAggregationsByOrganisationUrl;
    @NotNull
    private String courseCompletionsAggregationsByCourseUrl;
    @NotNull
    private String courseCompletionsAggregationsUrl;
    @NotNull
    private String requestCourseCompletionReportUrl;
    @NotNull
    private String requestRegisteredLearnerReportUrl;

    public String getReportRequestUrl(ReportType reportType) {
        return switch (reportType) {
            case COURSE_COMPLETIONS -> requestCourseCompletionReportUrl;
            case REGISTERED_LEARNER -> requestRegisteredLearnerReportUrl;
        };
    }
    
}
