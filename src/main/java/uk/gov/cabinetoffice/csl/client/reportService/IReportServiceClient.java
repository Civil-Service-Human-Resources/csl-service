package uk.gov.cabinetoffice.csl.client.reportService;

import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;

import java.util.List;

public interface IReportServiceClient {

    AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregations(GetCourseCompletionsParams params);

    AddCourseCompletionReportRequestResponse postCourseCompletionsExportRequest(CreateReportRequestParams params);

    GetCourseCompletionReportRequestsResponse getCourseCompletionsExportRequest(String userId, List<String> statuses);

    DownloadableFile downloadCourseCompletionsReport(String slug);
}
