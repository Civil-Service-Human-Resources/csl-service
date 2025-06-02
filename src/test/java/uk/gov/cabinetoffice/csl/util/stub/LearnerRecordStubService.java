package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class LearnerRecordStubService {

    private final CslTestUtil utils;

    public LearnerRecordStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    public StubMapping getModuleRecordsForUser(String userId, String response) {
        return getModuleRecords(List.of(userId), List.of(), response);
    }

    public StubMapping getModuleRecord(String moduleId, String userId, String getModuleRecordsResponse) {
        return getModuleRecords(List.of(userId), List.of(moduleId), getModuleRecordsResponse);
    }

    public StubMapping getModuleRecords(List<LearnerRecordResourceId> ids, String response) {
        return getModuleRecords(ids.stream().map(LearnerRecordResourceId::getLearnerId).toList(),
                ids.stream().map(LearnerRecordResourceId::getResourceId).toList(), response);
    }

    public StubMapping getModuleRecords(List<String> userIds, List<String> moduleIds, String response) {
        MappingBuilder mappingBuilder = WireMock.get(urlPathEqualTo("/learner_record_api/module_records"))
                .withQueryParam("userIds", including(userIds.toArray(String[]::new)));
        if (!moduleIds.isEmpty()) {
            mappingBuilder.withQueryParam("moduleIds", including(moduleIds.toArray(String[]::new)));
        }
        return stubFor(
                mappingBuilder
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping createModuleRecords(String expectedInput, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo("/learner_record_api/module_records/bulk"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping updateModuleRecords(String expectedInput, String response) {
        return stubFor(
                WireMock.put(urlPathEqualTo("/learner_record_api/module_records/bulk"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping getLearnerRecords(String learnerIds, Integer page, String response) {
        return getLearnerRecords(learnerIds, null, page, response);
    }

    public StubMapping getLearnerRecords(String learnerIds, @Nullable String resourceIds, Integer page, String response) {
        MappingBuilder mappingBuilder = WireMock.get(urlPathEqualTo("/learner_record_api/learner_records"))
                .withQueryParam("learnerIds", equalTo(learnerIds))
                .withQueryParam("size", equalTo("50"))
                .withQueryParam("page", equalTo(page.toString()));
        if (resourceIds != null) {
            mappingBuilder.withQueryParam("resourceIds", equalTo(resourceIds));
        }
        return stubFor(
                mappingBuilder
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping createLearnerRecords(String expectedEventPOST, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo("/learner_record_api/learner_records/bulk"))
                        .withRequestBody(equalToJson(expectedEventPOST))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping createLearnerRecordEvent(String expectedEventPOST, String response) {
        return stubFor(
                WireMock.post(urlPathEqualTo("/learner_record_api/learner_record_events"))
                        .withRequestBody(equalToJson(expectedEventPOST))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping bookEvent(String eventId, String expectedInput, BookingDto response) {
        return stubFor(
                WireMock.post(urlPathEqualTo(String.format("/learner_record_api/event/%s/booking/", eventId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(
                                expectedInput
                        ))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public StubMapping cancelBooking(String eventId, String userId, String expectedInput, String response) {
        return stubFor(
                WireMock.patch(urlPathEqualTo(String.format("/learner_record_api/event/%s/learner/%s", eventId, userId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping updateBookingWithId(String eventId, Integer bookingId, String expectedInput, String response) {
        return stubFor(
                WireMock.patch(urlPathEqualTo(String.format("/learner_record_api/event/%s/booking/%s", eventId, bookingId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping cancelEvent(String eventId, String expectedInput) {
        return stubFor(
                WireMock.patch(urlPathEqualTo(String.format("/learner_record_api/event/%s", eventId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json"))
        );
    }

    public StubMapping getBookings(String eventId, String response) {
        return stubFor(
                WireMock.get(urlPathEqualTo(String.format("/learner_record_api/event/%s/booking", eventId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

}
