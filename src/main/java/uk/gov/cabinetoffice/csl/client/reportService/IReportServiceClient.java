package uk.gov.cabinetoffice.csl.client.reportService;

import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.ReportType;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionWithOrganisationAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.reportRequest.ReportRequest;
import uk.gov.cabinetoffice.csl.service.report.params.IOrganisationalReportRequestParams;

import java.util.List;

public interface IReportServiceClient {

    AggregationResponse<Aggregation> getCourseCompletionAggregations(OrganisationIdsCourseCompletionsParams body);

    AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregationsByCourse(OrganisationIdsCourseCompletionsParams body);

    AggregationResponse<CourseCompletionWithOrganisationAggregation> getCourseCompletionAggregationsByCourseAndOrganisation(OrganisationIdsCourseCompletionsParams body);

    <T extends IOrganisationalReportRequestParams> AddReportRequestResponse postReportExportRequest(ReportType reportType, T params);

    <T extends ReportRequest> GetReportRequestsResponse<T> getReportExportRequest(ReportType reportType, String userId, List<String> statuses);

    DownloadableFile downloadCourseCompletionsReport(String slug);

}
