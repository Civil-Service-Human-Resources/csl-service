package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static uk.gov.cabinetoffice.csl.util.CslTestUtil.toJson;

public class LearnerRecordStubService {

    public static void patchCourseRecord(List<PatchOp> expPatches, CourseRecord response) {
        stubFor(
                WireMock.patch(urlPathEqualTo("/learner_record_api/course_records"))
                        .withQueryParam("courseId", equalTo(response.getCourseId()))
                        .withQueryParam("userId", equalTo(response.getUserId()))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .withHeader("Content-Type", equalTo("application/json-patch+json"))
                        .withRequestBody(equalToJson(
                                toJson(expPatches)
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(toJson(response)))
        );
    }

    public static void patchModuleRecord(long moduleRecordId, List<PatchOp> expPatches, ModuleRecord response) {
        String url = String.format("/learner_record_api/module_records/%s", moduleRecordId);
        stubFor(
                WireMock.patch(urlPathEqualTo(url))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .withHeader("Content-Type", equalTo("application/json-patch+json"))
                        .withRequestBody(equalToJson(
                                toJson(expPatches)
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(toJson(response)))
        );
    }

    public static void getCourseRecord(String courseId, String userId, CourseRecords response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/learner_record_api/course_records"))
                        .withQueryParam("courseIds", equalTo(courseId))
                        .withQueryParam("userId", equalTo(userId))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(toJson(response)))
        );
    }

    public static void createCourseRecord(CourseRecordInput expectedInput, CourseRecord response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/learner_record_api/course_records"))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .withRequestBody(equalToJson(
                                toJson(expectedInput)
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(toJson(response)))
        );
    }
}
