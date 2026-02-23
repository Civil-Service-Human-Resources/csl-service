package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "learner-record")
@RequiredArgsConstructor
public class LearnerRecordClientConfigParams {
    private final String eventsUrl;
    private final String bookingsUrl;
    private final String learnerRecordsUrl;
    private final String learnerRecordsSearchUrl;
    private final Integer learnerRecordsMaxPageSize;
    private final String moduleRecordsUrl;
    private final Integer moduleRecordBatchSize;
    private final String learnerRecordEventsUrl;

    public String getEventUrl(String eventUid) {
        return String.format("%s/%s", eventsUrl, eventUid);
    }

    public String getBookingUrl(String eventUid, String bookingId) {
        return String.format("%s%s/%s", getEventUrl(eventUid), getBookingsUrl(), bookingId);
    }

}
