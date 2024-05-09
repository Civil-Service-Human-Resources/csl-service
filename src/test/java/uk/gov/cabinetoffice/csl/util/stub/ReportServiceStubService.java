package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class ReportServiceStubService {

    public void getCourseCompletionAggregations(GetCourseCompletionsParams expectedParams, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/report-service/course-completions/aggregations/by-course"))
                        .withQueryParam("courseIds", equalTo(expectedParams.getCourseIdsAsString()))
                        .withQueryParam("organisationIds", equalTo(expectedParams.getOrganisationIdsAsString()))
                        .withQueryParam("professionIds", equalTo(expectedParams.getProfessionIdsAsString()))
                        .withQueryParam("gradeIds", equalTo(expectedParams.getGradeIdsAsString()))
                        .withQueryParam("binDelimiter", equalTo(expectedParams.getBinDelimiter().name()))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

}
