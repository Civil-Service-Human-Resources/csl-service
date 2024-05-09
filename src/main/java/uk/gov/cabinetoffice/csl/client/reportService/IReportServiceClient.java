package uk.gov.cabinetoffice.csl.client.reportService;

import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;

public interface IReportServiceClient {

    AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregations(GetCourseCompletionsParams params);

}
