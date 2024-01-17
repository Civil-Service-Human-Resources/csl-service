package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class LearnerRecordStubService {

    private final CslTestUtil utils;

    public LearnerRecordStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    public void patchCourseRecord(List<PatchOp> expPatches, CourseRecord response) {
        stubFor(
                WireMock.patch(urlPathEqualTo("/learner_record_api/course_records"))
                        .withQueryParam("courseId", equalTo(response.getCourseId()))
                        .withQueryParam("userId", equalTo(response.getUserId()))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .withHeader("Content-Type", equalTo("application/json-patch+json"))
                        .withRequestBody(equalToJson(
                                utils.toJson(expPatches)
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public void patchModuleRecord(long moduleRecordId, List<PatchOp> expPatches, ModuleRecord response) {
        String url = String.format("/learner_record_api/module_records/%s", moduleRecordId);
        stubFor(
                WireMock.patch(urlPathEqualTo(url))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .withHeader("Content-Type", equalTo("application/json-patch+json"))
                        .withRequestBody(equalToJson(
                                utils.toJson(expPatches)
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public void getCourseRecord(String courseId, String userId, CourseRecords response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/learner_record_api/course_records"))
                        .withQueryParam("courseIds", equalTo(courseId))
                        .withQueryParam("userId", equalTo(userId))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public void createCourseRecord(CourseRecordInput expectedInput, CourseRecord response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/learner_record_api/course_records"))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .withRequestBody(equalToJson(
                                utils.toJson(expectedInput)
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public void bookEvent(String eventId, BookingDto expectedInput, BookingDto response) {
        stubFor(
                WireMock.post(urlPathEqualTo(String.format("/learner_record_api/event/%s/booking/", eventId)))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .withRequestBody(equalToJson(
                                utils.toJson(expectedInput)
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }
}
