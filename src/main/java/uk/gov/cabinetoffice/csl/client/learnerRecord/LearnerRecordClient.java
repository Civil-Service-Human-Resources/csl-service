package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;

import java.util.List;

@Slf4j
@Component
public class LearnerRecordClient implements ILearnerRecordClient {

    @Value("${learnerRecord.courseRecordsForLearnerUrl}")
    private String courseRecords;
    @Value("${learnerRecord.eventsUrl}")
    private String event;
    @Value("${learnerRecord.bookingsUrl}")
    private String booking;

    private final IHttpClient httpClient;

    public LearnerRecordClient(@Qualifier("learnerRecordHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<CourseRecord> getCourseRecords(List<CourseRecordId> courseRecordIds) {
        log.debug("Getting course records with ids '{}'", courseRecordIds);
        String url = String.format("%s?userId=%s&courseIds=%s", courseRecords,
                String.join(",", courseRecordIds.stream().map(CourseRecordId::learnerId).toList()),
                String.join(",", courseRecordIds.stream().map(CourseRecordId::courseId).toList()));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        CourseRecords courseRecords = httpClient.executeRequest(request, CourseRecords.class);
        return courseRecords.getCourseRecords();
    }

    @Override
    public List<CourseRecord> createCourseRecords(List<CourseRecord> body) {
        log.debug("Creating course records '{}'", body);
        RequestEntity<List<CourseRecord>> request = RequestEntity.post(String.format("%s/bulk", courseRecords)).body(body);
        return httpClient.executeRequest(request, CourseRecords.class).getCourseRecords();
    }

    @Override
    public List<CourseRecord> updateCourseRecords(List<CourseRecord> input) {
        log.debug("Updating course records '{}'", input);
        RequestEntity<List<CourseRecord>> request = RequestEntity.put(String.format("%s/bulk", courseRecords)).body(input);
        return httpClient.executeRequest(request, CourseRecords.class).getCourseRecords();
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
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }
}
