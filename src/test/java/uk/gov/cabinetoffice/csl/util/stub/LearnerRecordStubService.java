package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class LearnerRecordStubService {

    private final CslTestUtil utils;

    public LearnerRecordStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    public void getCourseRecord(String courseId, String userId, CourseRecords response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/learner_record_api/course_records"))
                        .withQueryParam("courseIds", equalTo(courseId))
                        .withQueryParam("userId", equalTo(userId))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public void createCourseRecord(String expectedInput, CourseRecord response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/learner_record_api/course_records"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public void updateCourseRecord(String expectedInput, CourseRecord response) {
        stubFor(
                WireMock.put(urlPathEqualTo("/learner_record_api/course_records"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

    public void bookEvent(String eventId, String expectedInput, BookingDto response) {
        stubFor(
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

    public void cancelBooking(String eventId, String userId, String expectedInput, String response) {
        stubFor(
                WireMock.patch(urlPathEqualTo(String.format("/learner_record_api/event/%s/learner/%s", eventId, userId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void updateBookingWithId(String eventId, Integer bookingId, String expectedInput, String response) {
        stubFor(
                WireMock.patch(urlPathEqualTo(String.format("/learner_record_api/event/%s/booking/%s", eventId, bookingId)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expectedInput))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }
}
