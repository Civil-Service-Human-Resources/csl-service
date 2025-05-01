package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.bulk.BulkCreateOutput;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LearnerRecordClient implements ILearnerRecordClient {

    @Value("${learnerRecord.eventsUrl}")
    private String event;
    @Value("${learnerRecord.bookingsUrl}")
    private String booking;
    @Value("${learnerRecord.v2CourseRecordsUrl}")
    private String v2CourseRecordsUrl;
    @Value("${learnerRecord.learnerRecordsUrl}")
    private String learnerRecordsUrl;
    @Value("${learnerRecord.moduleRecordsForLearnerUrl}")
    private String moduleRecordsUrl;
    @Value("${learnerRecord.learnerRecordEventsUrl}")
    private String learnerRecordEventsUrl;

    private final IHttpClient httpClient;

    public LearnerRecordClient(@Qualifier("learnerRecordHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public BookingDto bookEvent(String eventId, BookingDto booking) {
        log.debug("Booking event {} with data {}", eventId, booking);
        String url = String.format("%s/%s/booking/", event, eventId);
        RequestEntity<BookingDto> request = RequestEntity
                .post(url).body(booking);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public BookingDto updateBooking(String userId, String eventId, BookingDto bookingDto) {
        log.debug("Updating booking for event {} with data {}", eventId, bookingDto);
        String url = String.format("%s/%s/learner/%s", event, eventId, userId);
        RequestEntity<BookingDto> request = RequestEntity
                .patch(url).body(bookingDto);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public BookingDto updateBookingWithId(String eventId, String bookingId, BookingDto bookingDto) {
        log.debug("Updating booking {} with data {}", bookingId, bookingDto);
        String url = String.format("%s/%s%s/%s", event, eventId, booking, bookingId);
        RequestEntity<BookingDto> request = RequestEntity.patch(url).body(bookingDto);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public EventDto updateEvent(String eventId, EventStatusDto dto) {
        log.debug("Updating event {} with data {}", eventId, dto);
        String url = String.format("%s/%s", event, eventId);
        RequestEntity<EventStatusDto> request = RequestEntity.patch(url).body(dto);
        return httpClient.executeRequest(request, EventDto.class);
    }

    @Override
    public List<BookingDto> getBookings(String eventId) {
        log.debug("Fetching bookings for event {}", eventId);
        String url = String.format("%s/%s/booking", event, eventId);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeTypeReferenceRequest(request);
    }

    @Override
    public List<LearnerRecord> getLearnerRecords(LearnerRecordQuery query) {
        return null;
    }

    @Override
    public BulkCreateOutput<LearnerRecord, LearnerRecordDto> createLearnerRecords(List<LearnerRecordDto> newLearnerRecords) {
        log.debug("Creating learner records {}", newLearnerRecords);
        RequestEntity<List<LearnerRecordDto>> request = RequestEntity.post(learnerRecordsUrl).body(newLearnerRecords);
        return httpClient.executeTypeReferenceRequest(request);
    }

    @Override
    public BulkCreateOutput<LearnerRecordEvent, LearnerRecordEventDto> createLearnerRecordEvents(List<LearnerRecordEventDto> body) {
        log.debug("Creating learner record events {}", body);
        RequestEntity<List<LearnerRecordEventDto>> request = RequestEntity.post(learnerRecordEventsUrl).body(body);
        return httpClient.executeTypeReferenceRequest(request);
    }

    @Override
    public List<ModuleRecord> createModuleRecords(List<ModuleRecord> newRecords) {
        return null;
    }

    @Override
    public List<ModuleRecord> getModuleRecords(List<ModuleRecordResourceId> moduleRecordIds) {
        return null;
    }

    @Override
    public List<ModuleRecord> updateModuleRecords(List<ModuleRecord> input) {
        return null;
    }

}
