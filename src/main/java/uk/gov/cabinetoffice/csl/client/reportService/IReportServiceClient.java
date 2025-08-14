package uk.gov.cabinetoffice.csl.client.reportService;

import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionWithOrganisationAggregation;

import java.util.List;

public interface IReportServiceClient {

    AggregationResponse<Aggregation> getCourseCompletionAggregations(OrganisationIdsCourseCompletionsParams body);

    AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregationsByCourse(OrganisationIdsCourseCompletionsParams body);

    AggregationResponse<CourseCompletionWithOrganisationAggregation> getCourseCompletionAggregationsByCourseAndOrganisation(OrganisationIdsCourseCompletionsParams body);

    AddCourseCompletionReportRequestResponse postCourseCompletionsExportRequest(CreateReportRequestWithOrganisationIdsParams params);

    GetCourseCompletionReportRequestsResponse getCourseCompletionsExportRequest(String userId, List<String> statuses);

    DownloadableFile downloadCourseCompletionsReport(String slug);

}
