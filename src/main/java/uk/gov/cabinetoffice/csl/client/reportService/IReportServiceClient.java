package uk.gov.cabinetoffice.csl.client.reportService;

import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;

import java.util.List;

public interface IReportServiceClient {

    AggregationResponse<Aggregation> getCourseCompletionAggregations(OrganisationIdsCourseCompletionsParams params);

    AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregationsByCourse(OrganisationIdsCourseCompletionsParams params);

    AddCourseCompletionReportRequestResponse postCourseCompletionsExportRequest(CreateReportRequestWithOrganisationIdsParams params);

    GetCourseCompletionReportRequestsResponse getCourseCompletionsExportRequest(String userId, List<String> statuses);

    DownloadableFile downloadCourseCompletionsReport(String slug);
}
